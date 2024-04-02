package com.highfive.imgtest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class SNController {

    // 썸머노트 동작시 이미지 처리 메소드
//    @PostMapping("/uploadSummernoteImageFile")
//    public String uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile,
//                                            HttpServletRequest request ) throws IOException {
//        JsonObject jsonObject = new JsonObject();
//
//
//        String fileRoot = "C:\\SummerNote\\"; // 외부경로로 저장을 희망할때.
//
//        // 내부경로로 저장
////        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
////        String fileRoot = contextRoot+"resources/fileupload/";
//
//        String originalFileName = multipartFile.getOriginalFilename();	//오리지날 파일명
//        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));	//파일 확장자
//        String savedFileName = UUID.randomUUID() + extension;	//저장될 파일 명
//
//        File targetFile = new File(fileRoot + savedFileName);
//        try {
//            InputStream fileStream = multipartFile.getInputStream();
//            FileUtils.copyInputStreamToFile(fileStream, targetFile);	//파일 저장
//            jsonObject.addProperty("url", fileRoot + savedFileName); // contextroot + resources + 저장할 내부 폴더명
//            jsonObject.addProperty("responseCode", "success");
//            System.out.println("jsonObject = " + jsonObject);
//
//        } catch (IOException e) {
//            FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
//            jsonObject.addProperty("responseCode", "error");
//            e.printStackTrace();
//        }
//        String a = jsonObject.toString();
//        System.out.println("a = " + a);
//        return a;
//    }

    @Value("${upload.path}")
    private String uploadPath; // 파일을 저장할 경로 설정

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("file") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                System.out.println("uploadPath = " + uploadPath);
                Path filepath = Paths.get(uploadPath, filename);
                Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/get_image/")
                        .path(filename)
                        .toUriString();
                urls.add(fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(urls);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println("urls = " + urls);
        System.out.println("json = " + json);
        System.out.println(ResponseEntity.ok(urls));
        return ResponseEntity.ok(json);
    }

}
