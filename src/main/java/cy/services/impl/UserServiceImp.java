package cy.services.impl;

import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtProvider;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.CustomHandleException;
import cy.dtos.UserDto;
import cy.entities.RoleEntity;
import cy.entities.UserEntity;
import cy.models.UserModel;
import cy.models.UserProfileModel;
import cy.repositories.IRoleRepository;
import cy.repositories.IUserRepository;
import cy.services.CustomUserDetail;
import cy.services.IUserService;
import cy.services.MailService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Order(5)
@Transactional
public class UserServiceImp implements IUserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileUploadProvider fileUploadProvider;

    public UserServiceImp(IUserRepository userRepository,
                          IRoleRepository roleRepository,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          MailService mailService, FileUploadProvider fileUploadProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.fileUploadProvider = fileUploadProvider;


        // create default roles
        try {
            // for insert default roles
            if (this.roleRepository.findAllByRoleIdIn(List.of(1l, 2l, 3l, 4l, 5l)).size() < 5) {
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(1L).roleName(RoleEntity.ADMINISTRATOR).build());
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(2L).roleName(RoleEntity.ADMIN).build());
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(3L).roleName(RoleEntity.MANAGER).build());
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(4L).roleName(RoleEntity.EMPLOYEE).build());
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(5L).roleName(RoleEntity.LEADER).build());
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }


        try {
            // for insert default admin
            if (!this.userRepository.findById(1L).isPresent()) {
                UserEntity administrator = UserEntity.builder()
                        .userId(1l)
                        .fullName("administrator")
                        .status(true)
                        .userName("administrator")
                        .password(this.passwordEncoder.encode("123456"))
                        .build();

                Set<UserEntity> users = Set.of(administrator);
                administrator.setRoleEntity(Set.of(
                        RoleEntity.builder().roleId(1L).roleName(RoleEntity.ADMINISTRATOR).userEntitySet(users).build()
                ));

                this.userRepository.save(administrator);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

    @Override
    public Page<UserDto> findAll(Pageable page) {
        return this.userRepository.findAll(page).map(UserDto::toDto);
    }

    @Override
    public List<UserDto> findAll(Specification<UserEntity> specs) {
        return this.userRepository.findAll(specs).stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> filter(Pageable page, Specification<UserEntity> specs) {
        return this.userRepository.findAll(specs, page).map(UserDto::toDto);
    }

    @Override
    public UserDto findById(Long id) {
        logger.info("{} finding user id: {%d}", SecurityUtils.getCurrentUsername(), id);
        return UserDto.toDto(this.getById(id));
    }

    @Override
    public UserEntity getById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
    }

    @Override
    public UserDto add(UserModel model) {
        // check user has existed with email
        UserEntity checkUser = this.userRepository.findByEmail(model.getEmail());
        if (checkUser != null)
            throw new CustomHandleException(12);

        // check user has existed with username
        checkUser = this.userRepository.findByUserName(model.getUsername());
        if (checkUser != null)
            throw new CustomHandleException(13);

        // check user has existed with phone
        if (model.getPhone() != null) {
            checkUser = this.userRepository.findByPhone(model.getPhone());
            if (checkUser != null)
                throw new CustomHandleException(14);
        }

        if (model.getPassword() == null || model.getPassword().isEmpty()) {
            throw new CustomHandleException(16);
        }


        UserEntity userEntity = UserModel.toEntity(model);
        if (model.getManager() != null) {
            try {
                UserEntity userManager = this.getById(model.getManager());
                userEntity.setManager(userManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userEntity.setStatus(true);
        userEntity.setPassword(this.passwordEncoder.encode(model.getPassword()));
        this.setRoles(userEntity, model.getRoles());
        return UserDto.toDto(this.userRepository.saveAndFlush(userEntity));
    }

    @Override
    public List<UserDto> add(List<UserModel> model) {
        return null;
    }

    @Override
    public UserDto update(UserModel model) {
        logger.info("{} is updating userid: {%d}", SecurityUtils.getCurrentUsername(), model.getId());

        UserEntity original = this.getById(model.getId());

        this.checkUserInfoDuplicate(original, model.getEmail(), model.getPhone());

        if (model.getManager() != null) {
            try {
                UserEntity userManager = this.getById(model.getManager());
                original.setManager(userManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (model.getPassword() != null) {
            if (model.getPassword().isEmpty())
                throw new CustomHandleException(17);
            else
                original.setPassword(this.passwordEncoder.encode(model.getPassword()));
        }
        original.setEmail(model.getEmail());
        original.setBirthDate(model.getBirthDate());
        original.setFullName(model.getFullName());
        original.setSex(model.getSex());
        original.setPhone(model.getPhone());
        original.setAddress(model.getAddress());
        return UserDto.toDto(this.userRepository.saveAndFlush(original));
    }

    public void setRoles(UserEntity user, List<Long> roles) {
        if (roles == null || roles.isEmpty())
            user.setRoleEntity(Collections.singleton(this.roleRepository.findRoleEntityByRoleName(RoleEntity.EMPLOYEE)));
        else
            user.setRoleEntity(this.roleRepository.findAllByRoleIdIn(roles));
    }

    @Override
    public boolean deleteById(Long id) {
        UserEntity userEntity = this.getById(id);
        userEntity.setStatus(false);
        return this.userRepository.saveAndFlush(userEntity) != null;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(id -> this.deleteById(id));
        return true;
    }

    @Override
    public JwtLoginResponse logIn(JwtUserLoginModel userLogin) {
        UserEntity user = this.findByUsername(userLogin.getUsername());
        UserDetails userDetail = new CustomUserDetail(user);
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetail, userLogin.getPassword(), userDetail.getAuthorities()));
        } catch (Exception e) {
            throw e;
        }

        long timeValid = userLogin.isRemember() ? 86400 * 7 : 1800l;
        return JwtLoginResponse.builder()
                .token(this.jwtProvider.generateToken(userDetail.getUsername(), timeValid))
                .type("Bearer").authorities(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .timeValid(timeValid)
                .build();
    }

    public UserEntity findByUsername(String userName) {
        return userRepository.findUserEntityByUserNameOrEmail(userName, userName).orElseThrow(() -> new CustomHandleException(11));
    }

    // Token filter, check token is valid and set to context
    @Transactional
    public boolean tokenFilter(String token, HttpServletRequest req, HttpServletResponse res) {
        try {
            String username = this.jwtProvider.getUsernameFromToken(token);
            CustomUserDetail userDetail = new CustomUserDetail(this.findByUsername(username));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            req.getSession().setAttribute("object", usernamePasswordAuthenticationToken.getPrincipal());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updateAvatar(MultipartFile avatar) {
        if (avatar.isEmpty())
            throw new RuntimeException("Ảnh đại diện đang để trống!");
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        if (userEntity.getAvatar() != null)
            this.fileUploadProvider.deleteFile(userEntity.getAvatar());
        try {
            userEntity.setAvatar(this.fileUploadProvider.uploadFile(UserEntity.FOLDER + userEntity.getUserName() + "/", avatar));
        } catch (IOException e) {
            throw new RuntimeException("Tải ảnh đại diện lên thất bại!");
        }
        return this.userRepository.save(userEntity) != null;
    }

    @Override
    public boolean changeStatus(Long userId) {
        return false;
    }

        UserEntity userEntity = this.getById(SecurityUtils.getCurrentUserId());
        try {
            String folder = "users" + userEntity.getUserName() + "/";
            userEntity.setAvatar(this.fileUploadProvider.uploadFile(folder, file));
        } catch (IOException e) {
            throw new CustomHandleException(15);
        }
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean updateMyProfile(UserProfileModel model) {
        UserEntity userEntity = this.getById(SecurityUtils.getCurrentUserId());
        this.checkUserInfoDuplicate(userEntity, model.getEmail(), model.getPhone());
        userEntity.setFullName(model.getFullName());
        userEntity.setBirthDate(model.getBirthDate());
        userEntity.setSex(model.getSex());
        userEntity.setAddress(model.getAddress());
        userEntity.setPhone(model.getPhone());
        userEntity.setEmail(model.getEmail());
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    private void checkUserInfoDuplicate(UserEntity userEntity, String email, String phone) {
        // check user has existed if user update their email
        if (email != null)
            if (!email.equals(userEntity.getEmail())) {
                UserEntity checkUser = this.userRepository.findByEmail(phone);
                if (checkUser != null && !checkUser.getUserId().equals(userEntity.getUserId()))
                    throw new CustomHandleException(12);
            }

        // check user has existed if user update their phone
        if (phone != null)
            if (!phone.equals(userEntity.getPhone())) {
                UserEntity checkUser = this.userRepository.findByPhone(phone);
                if (checkUser != null && !checkUser.getUserId().equals(userEntity.getUserId()))
                    throw new CustomHandleException(14);
            }

    }
}
