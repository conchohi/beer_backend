package com.zipbeer.beerbackend.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


//util 패키지는 실제 파일을 저장하는 역할을 함
// 파일 데이터의 입출력을 담당
// 프로그램이 시작되면 upload라는 이름의 폴더를 체크해서 자동으로 생성하도록 @PostConstruct를 이용
// saveFiles() : 파일 업로드 작업

@Component
@Log4j2
@RequiredArgsConstructor
public class FileUtil {
    @Value("${uploadPath}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);
        if (!tempFolder.exists()) {
            //tempFolder.mkdirs();
            tempFolder.mkdir();
        }
        uploadPath = tempFolder.getAbsolutePath();
    }

    // 파일 저장 시 중복된 이름의 파일이 저장되는 것을 막기 위해서 UUID로 중복이 발생하지 않도록 파일 이름을 구성
    public String saveFile(MultipartFile file) throws RuntimeException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path savePath = Paths.get(uploadPath, savedName);

        try {
            Files.copy(file.getInputStream(), savePath);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } //end of try

        return savedName;
    }

    //변경2,  추가 (브라우저에서 업로드 파일 보여주기)
// Resource 주의 : import org.springframework.core.io.Resource;
    public ResponseEntity<Resource> getFile(String fileName) {
        //File.separator : 운영체제(OS)에 관계없이 파일 경로 구분
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.png");
        }

        HttpHeaders headers = new HttpHeaders();

        try {
            //getFile()은 파일의 종류마다 다르게 HTTP 헤더 'Content-Type'값을 생성해야하기 때문에
            // Files.probeContentType() 으로 헤더 메시지를 생성
            //getFile()은  ProductController에서 특정한 파일을 조회할 때 사용
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }//end of try catch
        return ResponseEntity.ok().headers(headers).body(resource);
    }//end of getFile()


    //변경3,  추가 - 서버 내부에서 파일 삭제
// 파일의 삭제는 컨트롤러 계층 혹은 서비스 계층에서 데이터베이스 작업이 완료된 후에
//필요 없는 파일들을 삭제하는 용도로 처리할 때 사용
    public void deleteFile(String fileName) {
        if(fileName == null){
            return;
        }
        //썸네일이 있는지 확인하고 삭제
        Path filePath = Paths.get(uploadPath, fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }//end of deleteFile

}//end of CustomFileUtil
