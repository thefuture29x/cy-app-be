package com.config.jwt;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserLoginModel {  //Authentication login model
    @ApiModelProperty(notes = "User name", dataType = "String", example = "user1")
    @NotNull
    @NotBlank
    private String username;
    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    @NotNull
    @NotBlank
    private String password;
    @ApiModelProperty(notes = "Remember Me", dataType = "Boolean", example = "true")
    private boolean remember;
}
