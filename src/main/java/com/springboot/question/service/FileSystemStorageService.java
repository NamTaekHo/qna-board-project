package com.springboot.question.service;

import com.springboot.exception.StorageException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.SimpleTimeZone;

@Slf4j
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation = Paths.get("src/main/resources/static/questionImage");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    @Override
    public String store(MultipartFile file, String customFileName) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to upload empty file");
            }
            // 확장자 확인
            String originalFileName = file.getOriginalFilename();
            if(!isAllowedExtension(originalFileName)){
                throw new StorageException("File type not allowed: " + originalFileName);
            }

            String extention = getFileExtension(originalFileName);

            // 커스텀 이름 + 확장자
            String newFileName = customFileName + "." + extention;
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(newFileName)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot upload file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                log.info("# store coffee image!!");
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return newFileName;
        } catch (IOException e) {
            throw new StorageException("Failed to upload file.", e);
        }
    }

    private String getFileExtension(String fileName){
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if(lastIndexOfDot == -1){
            return ""; // 확장자 없을 때
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private boolean isAllowedExtension(String fileName){
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
}

