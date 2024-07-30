package com.ivan_degtev.documentaccounting2.service.other;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
