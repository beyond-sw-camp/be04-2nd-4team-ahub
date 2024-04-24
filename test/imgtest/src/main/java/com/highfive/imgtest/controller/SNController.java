package com.highfive.imgtest.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class SNController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    private final AmazonS3Client amazonS3Client;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("file") List<MultipartFile> files) {

        System.out.println("files = " + files);
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String originName = file.getOriginalFilename(); // 원본 파일명 저장할거면 사용
                String renameName = UUID.randomUUID().toString() + "_" + originName;    // 파일명 중복을 피하기 위한 파일명 리네임
                String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + renameName;    // 저장 경로
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(bucket, renameName, file.getInputStream(), metadata);

                ObjectMapper mapper = new ObjectMapper();
                String json;
                try {
                    json = mapper.writeValueAsString(fileUrl);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                urls.add(json);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        System.out.println("urls = " + urls);
        return ResponseEntity.ok(urls);
    }
}
