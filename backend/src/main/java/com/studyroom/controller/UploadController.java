package com.studyroom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String UPLOAD_DIR = "uploads";

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "file";
            
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalName.substring(dotIndex);
                originalName = originalName.substring(0, dotIndex);
            }
            
            // Clean filename
            originalName = originalName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String safeName = UUID.randomUUID().toString().substring(0, 8) + "-" + originalName + extension;
            
            Path filePath = Paths.get(UPLOAD_DIR, safeName);
            Files.write(filePath, file.getBytes());

            String type = "file";
            String mimeType = file.getContentType();
            if (mimeType != null && mimeType.startsWith("image/")) {
                type = "image";
            }

            Map<String, String> response = new HashMap<>();
            response.put("url", "/uploads/" + safeName);
            response.put("type", type);
            response.put("name", file.getOriginalFilename());
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed");
        }
    }
}
