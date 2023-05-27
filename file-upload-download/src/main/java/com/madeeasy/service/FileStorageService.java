package com.madeeasy.service;

import com.madeeasy.entity.Files;
import com.madeeasy.error.exception.FileStorageException;
import com.madeeasy.repository.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class FileStorageService {

    @Autowired
    private FileRepo fileRepo;

    public Files storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequenth " + fileName);
            }

            Files dbFile = new Files(fileName, file.getContentType(), file.getBytes());
            return fileRepo.save(dbFile);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Files getFile(String fileId) throws FileNotFoundException {
        return fileRepo.findById(Long.valueOf(fileId))
                .orElseThrow(() -> new FileNotFoundException("File you requested not found with id " + fileId));

    }

    public Files storeListOfFiles(List<MultipartFile> list) {
        Files storedFile = null;
        for (MultipartFile file : list) {
            storedFile = storeFile(file);
        }
        return storedFile;
    }
}

