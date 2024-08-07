package com.ivan_degtev.documentaccounting2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.documentaccounting2.dto.address.AddressUpdateDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.dto.user.BaseUpdateUserDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.mapper.FileEntityMapper;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.FileRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.FileService;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

import java.io.ByteArrayOutputStream;

@Service
@AllArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private UserUtils userUtils;
    private AddressServiceImpl addressService;
    private FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileEntityMapper fileEntityMapper;
    private final MappingIdAndEntityDataImpl mappingIdAndEntityData;

    @Override
    @Transactional(readOnly = true)
    public List<FileEntityDTO> getAll() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        return fileEntities.stream()
                .map(fileEntityMapper::toFileEntityDTO)
                .collect(Collectors.toList());
    }

    /**
     * Метод для вывода только тех файлов, которые подходят по параметрам доступа(определено в контроллере)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FileEntityDTO> getAllForUsers() {
        Long userId = userUtils.getCurrentUser().getIdUser();
        List<FileEntity> fileEntities = fileRepository.findAllByAuthorIdUserAndPublicFile(userId);
        List<FileEntityDTO> resultList = fileEntities.stream()
                .map(fileEntityMapper::toFileEntityDTO)
                .toList();
        return resultList;
    }

    /**
     * В методе идет подготовка данных для передачи, как паарметры в репозиторий для поиска файлов по определеённым полям.
     * Присутствует баг, по которому не получается произвести поиск, если поле с датой будет типа LocalDate, поэтому
     * используется преобразование в String с последуещим поиском в БД через конструкцию TO_CHAR()
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FileEntityDTO> searchFiles(FileEntityParamsDTO params, int pageNumber) {
        Long userId = userUtils.getCurrentUser().getIdUser();
        String sortBy = params.getSortBy();
        String sortDirection = params.getSortDirection();

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "filename";
        }
        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNumber - 1, 10, sort);

        Page<FileEntity> files = fileRepository.searchFiles(
                params.getFileNameCont(),
                params.getAuthorCont(),
                params.getFileTypeCont(),
                params.getCreationDate() != null ? params.getCreationDate().toString() : null,
                userId,
                pageable);

        return files.map(fileEntityMapper::toFileEntityDTO);
    }

    /**
     * Выводит общие данные о конкретной сущности документа - типы, имя, авторов, доступы, исп. на страницах просмотра
     * информации о файле
     */
    @Override
    @Transactional(readOnly = true)
    public FileEntityDTO getDataFile(Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
        fileEntityMapper.toFileEntityDTO(fileEntity);
        return fileEntityMapper.toFileEntityDTO(fileEntity);
    }

    /**
     * Общий метод для получения миниатюры. Вызывает метод нахождения файла в БД, и далее этот файл прокидывет
     * через утилитный метод создания миниатюры.
     */
    @Override
    @Transactional
    public byte[] getThumbnailFile(Long id) {
        FileEntity fileEntity = getFile(id);
        return generateThumbnail(fileEntity.getData(), fileEntity.getFiletype());
    }

    /**
     * Утилитарный метод, создающий миниатюру файла в формате png для обложки файла. При исп с картинками - создает входной
     * поток с данными файла, передает файл в буффер(BufferedImage), черещ библиотеку thumbnailator делает из буферизированного
     * файла миниатюру с указанными размерами, png формат и передаёт, как способ вывода - ByteArrayOutputStream, созданный ранее.
     * Если файл  - pdf - использует утилитный метод generatePdfThumbnail
     */
    @Override
    public byte[] generateThumbnail(byte[] fileData, String fileType) {
        try {
            if (fileType.startsWith("image/")) {
                ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
                BufferedImage originalImage = ImageIO.read(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Thumbnails.of(originalImage)
                        .size(100, 100)
                        .outputFormat(fileType.split("/")[1])
                        .toOutputStream(baos);
                return baos.toByteArray();
            } else if (fileType.equals("application/pdf")) {
                return generatePdfThumbnail(fileData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Утилитный метод, создающий миниатюру файла в формате png для обложки файла. При исп с пдф - создает входной
     * поток с данными файла, передает файл в буффер(BufferedImage), черещ библиотеку apache.pdfbox - достает док-т,
     * реднерит его первую(0 индекс) страницу в png-формат.
     * Отдает данные типа byte[] через тот же ByteArrayOutputStream, формирую миниатюру.
     */
    private byte[] generatePdfThumbnail(byte[] fileData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
        PDDocument document = PDDocument.load(bais);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage pageImage = pdfRenderer.renderImageWithDPI(0, 72);
        document.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(pageImage)
                .size(100, 100)
                .outputFormat("png")
                .toOutputStream(baos);

        return baos.toByteArray();
    }

    /**
     * Получает MultipartFile, добавляет необходимые данные(автора, тип, название) в FileEntity
     * и параметры, что пришли на входе(доступ).
     * Для маппинга Сетов айди юзеров в сущности  - используется внедрённый бин маппера MappingIdAndEntityData
     */
    @Override
    @Transactional
    public FileEntity storeFile(MultipartFile file, String paramsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonNullableModule());

        FileEntityUpdateDTO paramsDTO = objectMapper.readValue(paramsJson, FileEntityUpdateDTO.class);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFiletype(file.getContentType());
        fileEntity.setData(file.getBytes());
        fileEntity.setAuthor(userUtils.getCurrentUser());

        if (paramsDTO != null && paramsDTO.getPublicEntity()) {
            fileEntity.setPublicEntity(true);
        }
        if (paramsDTO != null && paramsDTO.getAvailableFor() != null && !paramsDTO.getAvailableFor().isEmpty()) {
            fileEntity.setAvailableFor(
                    mappingIdAndEntityData.convertIdsToEntities(
                            paramsDTO.getAvailableFor(), User.class));
        }
        if (paramsDTO != null && paramsDTO.getEnteredAddress().isPresent()) {
            fileEntity.setAddress(changeFileEntityAddressIfAvailable(paramsDTO));
        }
        return fileRepository.save(fileEntity);
    }


    @Override
    @Transactional(readOnly = true)
    public FileEntity getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File not found with id " + id));
    }

    @Override
    @Transactional
    public FileEntityDTO update(FileEntityUpdateDTO fileEntityUpdateDTO, Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("FileEntity with this id " + id + " not found!"));
        if (fileEntityUpdateDTO != null && fileEntityUpdateDTO.getEnteredAddress().isPresent()) {
            fileEntity.setAddress(changeFileEntityAddressIfAvailable(fileEntityUpdateDTO));
        }
        fileEntityMapper.update(fileEntityUpdateDTO, fileEntity);
        fileRepository.save(fileEntity);
        return fileEntityMapper.toFileEntityDTO(fileEntity);
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    private AddressEntity changeFileEntityAddressIfAvailable(FileEntityUpdateDTO updateFileEntityDTO) {
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setEnteredFullAddressForUpdate(updateFileEntityDTO.getEnteredAddress().get());
        return addressService.updateAddress(addressUpdateDTO);
    }
}
