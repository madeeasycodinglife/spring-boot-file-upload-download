package com.madeeasy.controller;

import com.madeeasy.entity.Files;
import com.madeeasy.model.FileUploadResponse;
import com.madeeasy.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/single/file-upload")
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Files files = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/file-download/")
                .path(String.valueOf(files.getId()))
                .toUriString();

        return new FileUploadResponse(files.getName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

 @PostMapping(value = "/multi-file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<?> handleFileUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
     List<Files> filesStored = new ArrayList<>();
     List<String> fileDownloadUri = new ArrayList<>();

     for (MultipartFile file : files) {
         if (file.isEmpty()) {
             throw new FileNotFoundException("Failed to read file: " + file.getOriginalFilename());
         }

         Files storedFile = fileStorageService.storeListOfFiles(Collections.singletonList(file));
         filesStored.add(storedFile);

         String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                 .path("/file-download/")
                 .path(String.valueOf(storedFile.getId()))
                 .toUriString();
         fileDownloadUri.add(downloadUri);
     }

     List<String> fileNames = filesStored.stream()
             .map(Files::getName)
             .collect(Collectors.toList());

     FileUploadResponse response = new FileUploadResponse(
             fileNames.toString(),
             fileDownloadUri.toString(),
             Arrays.stream(files).map(MultipartFile::getContentType).collect(Collectors.joining(" , ")),
             Arrays.stream(files).map(MultipartFile::getSize).mapToLong(Number::longValue).sum()
     );

     return ResponseEntity.ok()
             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNames.toString() + "\"")
             .body(response);
 }

    @GetMapping("/file-download/{fileId}")
    public ResponseEntity downloadFile(@PathVariable String fileId) throws FileNotFoundException {
        Files dbFile = fileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "/attachment; filename=\"" + dbFile.getName() + "\"")
                .body(new ByteArrayResource(dbFile.getFileContent()));// for this we can see image in postman
    }
}
