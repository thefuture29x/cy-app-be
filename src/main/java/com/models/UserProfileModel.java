package com.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileModel {
    @ApiModelProperty(notes = "User full name", dataType = "String", example = "Nguyen Van A")
    private String fullName;

    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    private String password;

    @ApiModelProperty(notes = "User birthdate", dataType = "Date", example = "2002-04-29")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthDate;

    @ApiModelProperty(notes = "receiver's phone, phone must format follow vietnam", dataType = "String", example = "0952888888")
    private String phone;

    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    private String sex;

    @ApiModelProperty(notes = "user's avatar")
    private MultipartFile avatar;

}
