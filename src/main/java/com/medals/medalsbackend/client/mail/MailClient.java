package com.medals.medalsbackend.client.mail;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "mailClient", url = "${app.email.url}")
public interface MailClient {

    @PostMapping(value = "/send.php", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    void sendMail(@RequestPart("author") String author, @RequestPart("receiver") String receiver, @RequestPart("message") String message, @RequestPart("subject") String subject);
}
