package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.mapper.FileEntityMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.ByteArrayOutputStream;

@Service
@AllArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private UserUtils userUtils;
    private FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileEntityMapper fileEntityMapper;

    @Override
    public List<FileEntityDTO> getAll() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        return fileEntities.stream()
                .map(fileEntityMapper::toFileEntityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileEntityDTO> getAllForUsers(Long userId) {
        log.info("зашёл в метод сервиса для юзеров, айди {}", userId);
        List<FileEntity> fileEntities = fileRepository.findAllByAuthorIdUserAndPublicFile(userId);
        log.info("получил данные из репозитория, первый файл из них {}", fileEntities.get(0).toString());
        List<FileEntityDTO> resultList = fileEntities.stream()
                .map(fileEntityMapper::toFileEntityDTO)
                .toList();
        return resultList;
    }

//    @Override
//    public List<FileEntity> findAll() {
//        log.info("зашёл в сервисный метод получить все файлы ");
//        return fileRepository.findAll();
//    }

    @Override
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
                params.getCreationDate(),
                userId,
                pageable);

        return files.map(fileEntityMapper::toFileEntityDTO);
    }


    @Override
    public FileEntityDTO getDataFile(Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
        fileEntityMapper.toFileEntityDTO(fileEntity);
        return fileEntityMapper.toFileEntityDTO(fileEntity);
    }
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

    private byte[] generatePdfThumbnail(byte[] fileData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
        PDDocument document = PDDocument.load(bais);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage pageImage = pdfRenderer.renderImageWithDPI(0, 72); // Render the first page at 72 DPI
        document.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(pageImage)
                .size(100, 100)
                .outputFormat("png")
                .toOutputStream(baos);

        return baos.toByteArray();
    }

//    @Override
//    public byte[] generateThumbnail(byte[] fileData, String fileType) {
//        try {
//            if (fileType.startsWith("image/")) {
//                ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
//                BufferedImage originalImage = ImageIO.read(bais);
//                int width = originalImage.getWidth() / 4;
//                int height = originalImage.getHeight() / 4;
//                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
//                BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g2d = thumbnail.createGraphics();
//                g2d.drawImage(scaledImage, 0, 0, null);
//                g2d.dispose();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(thumbnail, fileType.split("/")[1], baos);
//                return baos.toByteArray();
//            } else if (fileType.equals(MimeTypeUtils.APPLICATION_PDF_VALUE)) {
//                // Логика для генерации миниатюры PDF
//                // Возвращаем оригинальные данные для простоты
//                return fileData;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new byte[0];
//    }

    @Override
    public FileEntity storeFile(MultipartFile file, FileEntityUpdateDTO paramsDTO) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFiletype(file.getContentType());
        fileEntity.setData(file.getBytes());
        fileEntity.setAuthor(userUtils.getCurrentUser());

        if (paramsDTO != null && paramsDTO.getPublicEntity()) {
            fileEntity.setPublicEntity(true);
        }
        if (paramsDTO != null && paramsDTO.getAvailableFor() != null && !paramsDTO.getAvailableFor().isEmpty()) {
            fileEntity.setAvailableFor(mappingFromDtoToEntity(paramsDTO.getAvailableFor()));
        }
        return fileRepository.save(fileEntity);
    }
    private Set<User> mappingFromDtoToEntity(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }
    //маппер в классе для теста, мб позже перенесу его в маппер, но пока здесь для наглядности


    @Override
    public FileEntity getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File not found with id " + id));
    }
    @Override
    public FileEntityDTO update(FileEntityUpdateDTO fileEntityUpdateDTO, Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("FileEntity with this id " + id + " not found!"));
        log.info("нашёл в репозитории нужный файл по айди {}", fileEntity.toString());
        fileEntityMapper.update(fileEntityUpdateDTO, fileEntity);
        log.info("замапил изменения из дто в сущность {}", fileEntity.toString());
        fileRepository.save(fileEntity);
        log.info("сохранил обновленную сущность в репо");
        return fileEntityMapper.toFileEntityDTO(fileEntity);
    }

    @Override
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
}
