package com.zipbeer.beerbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("id")
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String profileImage;
    private String sns;
    private String role;
    @JsonIgnore
    private MultipartFile profileFile;

    private String isDelete;
}
