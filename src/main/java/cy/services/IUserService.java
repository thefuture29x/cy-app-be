package cy.services;

import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.UserDto;
import cy.entities.UserEntity;
import cy.models.*;
import cy.models.UserModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService extends IBaseService<UserEntity, UserDto, UserModel, Long>{

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    boolean setPassword(PasswordModel model);

    boolean changePassword(String password);

    boolean changeMyAvatar(MultipartFile file);

    boolean updateMyProfile(UserProfileModel model);
}
