package cy.services;

import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.RequestSendMeDto;
import cy.dtos.UserDto;
import cy.entities.UserEntity;
import cy.models.*;
import cy.models.UserModel;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IUserService extends IBaseService<UserEntity, UserDto, UserModel, Long>{

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    boolean changeMyAvatar(MultipartFile file);

    boolean updateMyProfile(UserProfileModel model);

    boolean changePassword(String password);


    boolean setPassword(PasswordModel model);

    List<RequestSendMeDto> getAllRequestSendMe(Long id, Pageable pageable);

    List<RequestSendMeDto> getAllRequestCreateByMe(Long id, Pageable pageable);

    boolean changeStatus(Long id);
}
