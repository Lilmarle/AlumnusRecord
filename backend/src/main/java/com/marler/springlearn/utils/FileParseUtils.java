package com.marler.springlearn.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
public class FileParseUtils {
    
    public static String parseFileContent(MultipartFile file, String uploadPath, String uploadUrl) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return null;
        }
        
        String extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        
        if (!extName.matches(".*\\.(txt|md|docx|doc)$")) {
            throw new IOException("不支持的文件类型，仅支持txt、md、docx、doc文件");
        }
        
        if (extName.equals(".txt") || extName.equals(".md")) {
            return new String(file.getBytes(), "UTF-8");
        } else if (extName.equals(".docx")) {
            return parseDocx(file, uploadPath, uploadUrl);
        } else if (extName.equals(".doc")) {
            return parseDoc(file, uploadPath, uploadUrl);
        }
        return null;
    }
    
    private static String parseDocx(MultipartFile file, String uploadPath, String uploadUrl) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            List<XWPFPictureData> pictures = document.getAllPictures();
            for (XWPFPictureData picture : pictures) {
                try {
                    String pictureName = UUID.randomUUID().toString() + ".jpg";
                    String picturePath = uploadPath + "/" + pictureName;
                    File pictureFile = new File(picturePath);
                    
                    try (FileOutputStream fos = new FileOutputStream(pictureFile)) {
                        fos.write(picture.getData());
                    }
                    
                    String pictureUrl = uploadUrl + "/" + pictureName;
                    log.info("提取图片成功，URL：{}", pictureUrl);
                } catch (Exception e) {
                    log.error("提取图片失败：{}", e.getMessage());
                }
            }
            
            try (XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            }
        }
    }
    
    private static String parseDoc(MultipartFile file, String uploadPath, String uploadUrl) throws IOException {
        try (InputStream is = file.getInputStream();
             org.apache.poi.hwpf.HWPFDocument document = new org.apache.poi.hwpf.HWPFDocument(is)) {
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            List<Picture> pictures = document.getPicturesTable().getAllPictures();
            for (Picture picture : pictures) {
                try {
                    String pictureName = UUID.randomUUID().toString() + ".jpg";
                    String picturePath = uploadPath + "/" + pictureName;
                    File pictureFile = new File(picturePath);
                    
                    try (FileOutputStream fos = new FileOutputStream(pictureFile)) {
                        fos.write(picture.getContent());
                    }
                    
                    String pictureUrl = uploadUrl + "/" + pictureName;
                    log.info("提取图片成功，URL：{}", pictureUrl);
                } catch (Exception e) {
                    log.error("提取图片失败：{}", e.getMessage());
                }
            }           
            try (WordExtractor extractor = new WordExtractor(document)) {
                return extractor.getText();
            }
        }
    }
}
