package com.ivan_degtev.documentaccounting2.service.other;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Утилитарный компонент для создания хедеров при работе с файлами  - скачивание или просмотр, реализуется через статический класс
 * внутри бина. В контроллере при создании нужного хедера, вызвается этот бин(дефолтный - с png-хедеров, если простая операция
 * отоброажения, или кастомный headerService, с дополнительной логикой)
 */
@Component
public class HeaderConfig {

    @Bean
    public HttpHeaders httpHeadersPNG() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return headers;
    }

    @Bean
    public HeaderService headerService() {
        return new HeaderService();
    }
    public static class HeaderService {

        public HttpHeaders headersToInteractFile(FileEntity fileEntity, boolean download) {
            String filetype = fileEntity.getFiletype();
            String filename = fileEntity.getFilename();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(filetype));

            String dispositionType = download ? "attachment" : "inline";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            String contentDisposition = String.format("%s; filename=\"%s\"", dispositionType, encodedFilename);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

            return headers;
        }
    }
}
