package com.services;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.dtos.UserDto;
import com.entities.UserEntity;
import com.models.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public interface IUserService extends IBaseService<UserEntity, UserDto,UserModel, Long>{

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    boolean updateAvatar(MultipartFile avatar);

    boolean changeStatus(Long userId);

    boolean changeLockStatus(Long userId);

    String updateAvatar1(MultipartFile avatar);
}
