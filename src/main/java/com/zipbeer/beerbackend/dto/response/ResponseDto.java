package com.zipbeer.beerbackend.dto.response;


import com.zipbeer.beerbackend.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private String message;
    public static ResponseEntity<ResponseDto> success(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);
    }
    public static ResponseEntity<ResponseDto> validationFail(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.VALIDATION_FAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> duplicateId(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.DUPLICATE_ID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
    public static ResponseEntity<ResponseDto> certificationFail(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.CERTIFICATION_FAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
    public static ResponseEntity<ResponseDto> notExistMail(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.NOT_EXIST_MAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> databaseError(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.DATABASE_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
    public static ResponseEntity<ResponseDto> mailFail(){
        ResponseDto responseBody = new ResponseDto(ResponseMessage.MAIL_FAIL);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
    public static ResponseEntity<?> notExistUser() {
        return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
    }

}
