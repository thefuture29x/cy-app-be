package cy.services.attendance.impl;

import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtProvider;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.attendance.*;
import cy.dtos.common.CustomHandleException;
import cy.dtos.common.UserDto;
import cy.entities.attendance.*;
import cy.entities.common.RoleEntity;
import cy.entities.common.UserEntity;
import cy.models.common.PasswordModel;
import cy.models.common.UserModel;
import cy.models.common.UserProfileModel;
import cy.repositories.common.IRoleRepository;
import cy.repositories.common.IUserRepository;
import cy.repositories.attendance.*;
import cy.services.common.CustomUserDetail;
import cy.services.common.IUserService;
import cy.services.common.MailService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    IRequestModifiRepository iRequestModifiRepository;
    @Autowired
    IRequestDayOffRepository iRequestDayOffRepository;
    @Autowired
    IRequestDeviceRepository iRequestDeviceRepository;
    @Autowired
    IRequestOTRepository iRequestOTRepository;
    @Autowired
    IRequestAttendRepository iRequestAttendRepository;

    @Value("${timeKeepingDate}")
    int timeKeepingDate;


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
        List<UserEntity> userEntities = this.userRepository.findAll();
        return userEntities.stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> findAll(Pageable page) {
        logger.info("{} is finding all users", SecurityUtils.getCurrentUsername());
        return this.userRepository.findAll(page).map(UserDto::toDto);
    }

    @Override
    public List<UserDto> findAll(Specification<UserEntity> specs) {
        logger.info("{} is finding all users", SecurityUtils.getCurrentUsername());
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
//        logger.info("{} is adding user", SecurityUtils.getCurrentUsername());
        // check user has existed with email
        UserEntity checkUser = this.userRepository.findByEmail(model.getEmail());
        if (checkUser != null)
            throw new CustomHandleException(12);

        // check user has existed with username
        checkUser = this.userRepository.findByUserName(model.getUserName());
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
        } else
            userEntity.setManager(this.getById(1L));

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
        if (model.getId().equals(1L))
            throw new CustomHandleException(19);
        logger.info("{} is updating user id: {}", SecurityUtils.getCurrentUsername(), model.getId());

        UserEntity original = this.getById(model.getId());

        // check user has existed if user update their email
        if (!model.getEmail().equals(original.getEmail())) {
            UserEntity checkUser = this.userRepository.findByEmail(model.getEmail());
            if (checkUser != null && !checkUser.getUserId().equals(original.getUserId()))
                throw new CustomHandleException(12);
        }

        // check user has existed if user update their phone
        if (!model.getPhone().equals(original.getPhone())) {
            UserEntity checkUser = this.userRepository.findByPhone(model.getPhone());
            if (checkUser != null && !checkUser.getUserId().equals(original.getUserId()))
                throw new CustomHandleException(14);
        }

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
        this.setRoles(original,model.getRoles());
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
        if (id.equals(1L))
            throw new CustomHandleException(18);
        logger.info("{} is deleting user id: {}", SecurityUtils.getCurrentUsername(), id);
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
                .id(user.getUserId())
                .token(this.jwtProvider.generateToken(userDetail.getUsername(), timeValid))
                .type("Bearer").authorities(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .timeValid(timeValid)
                .avatar(user.getAvatar())
                .userName(userDetail.getUsername())
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
    public boolean changeMyAvatar(MultipartFile file) {
        logger.info("{} is updating avatar", SecurityUtils.getCurrentUsername());

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
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
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

    @Override
    public boolean changePassword(String password) {
        logger.info("{} is changing password", SecurityUtils.getCurrentUsername());
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setPassword(this.passwordEncoder.encode(password));
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean setPassword(PasswordModel model) {
        logger.info("{} is setting password for user id: {}", SecurityUtils.getCurrentUsername(), model.getUserId());
        UserEntity userEntity = this.getById(model.getUserId());
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setPassword(this.passwordEncoder.encode(model.getPassword()));
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean changeStatus(Long id) {
        logger.info("{} is changing status", SecurityUtils.getCurrentUsername());
        UserEntity userEntity = this.getById(id);
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setStatus(!userEntity.getStatus());
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public Object getRequestByIdAndType(Long id, String type) {
        switch (type){
            case "Modifi":
                return RequestModifiDto.toDto(iRequestModifiRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
            case "Device":
                return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
            case "DayOff":
                return RequestDayOffDto.toDto(iRequestDayOffRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
            case "OT":
                return RequestOTDto.toDto(iRequestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
            case "Attend":
                return RequestAttendDto.entityToDto(iRequestAttendRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
        }
        return null;
    }

    @Override
    public List<UserDto> getUserByRoleName(String roleName) {
        List<String> roles = new ArrayList<>();
        if (roleName.equals("ROLE_ADMINISTRATOR")){
            roles.add("ROLE_ADMINISTRATOR");
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else if (roleName.equals("ROLE_ADMIN")){
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else if (roleName.equals("ROLE_MANAGER")){
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        } else if (roleName.equals("ROLE_LEADER")){
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else {
            roles.add("ROLE_EMPLOYEE");
        }
        List<UserEntity> userEntities = this.userRepository.findAllByRoleName(roles);
        UserEntity userLogin = SecurityUtils.getCurrentUser().getUser();
        for (UserEntity user: userEntities) {
            if(user.getUserId().equals(userLogin.getUserId())){
                userEntities.remove(user);
                break;
            }
        }
        if (userEntities != null && userEntities.size() > 0){
            return userEntities.stream().map(UserDto::toDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserDto> getAllUserByRoleName(String roleName) {
        List<String> roles = new ArrayList<>();
        if (roleName.equals("ROLE_ADMINISTRATOR")){
            roles.add("ROLE_ADMINISTRATOR");
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else if (roleName.equals("ROLE_ADMIN")){
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else if (roleName.equals("ROLE_MANAGER")){
            roles.add("ROLE_MANAGER");
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        } else if (roleName.equals("ROLE_LEADER")){
            roles.add("ROLE_LEADER");
            roles.add("ROLE_EMPLOYEE");
        }else {
            roles.add("ROLE_EMPLOYEE");
        }
        List<UserEntity> userEntities = this.userRepository.findAllByRoleName(roles);
        if (userEntities != null && userEntities.size() > 0){
            return userEntities.stream().map(UserDto::toDto).collect(Collectors.toList());
        }
        return  new ArrayList<>();
    }


    @Override
    public List<PayRollDto> calculatePayRoll(Pageable pageable, int endMonth, int endYear) {
        int startMonth = 0;
        int startYear = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (endMonth ==1){
            startMonth = 12;
            startYear = endYear - 1;
        }else {
            startMonth = endMonth - 1;
            startYear = endYear;
        }
        String timeStartWorking = startYear +"-"+ startMonth+"-"+(timeKeepingDate + 1);
        String timeEndWorking = endYear +"-"+ endMonth+"-"+timeKeepingDate;

        return userRepository.calculatePayRoll(timeStartWorking, timeEndWorking);
    }

    @Override
    public List<PayRollDto> searchUserPayRoll(Pageable pageable, int endMonth, int endYear, String keyword) {
        int startMonth = 0;
        int startYear = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (endMonth ==1){
            startMonth = 12;
            startYear = endYear - 1;
        }else {
            startMonth = endMonth - 1;
            startYear = endYear;
        }

        String timeStartWorking = startYear +"-"+ startMonth+"-"+(timeKeepingDate + 1);
        String timeEndWorking = endYear +"-"+ endMonth+"-"+timeKeepingDate;

        return userRepository.searchUserPayRoll(timeStartWorking, timeEndWorking,keyword);
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
    @Override
    public List<RequestSendMeDto> getAllRequestSendMe(Long id,Pageable pageable) {
        LocalDate date = LocalDate.now();
        String startTime = date.toString().concat(" 00:00:00");
        String endTime = date.toString().concat(" 23:59:59");
        List<RequestSendMeDto> requestSendMeDtoList = new ArrayList<>();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Long userAuthent = SecurityUtils.getCurrentUserId();
        if(userAuthent != id)
            throw new CustomHandleException(21);
        // Get all request modifi send to leader on this day
        for (RequestModifiEntity entity:iRequestModifiRepository.getAllRequestSendMe(id,startTime,endTime,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Modifi")
                    .build());
        }
        // Get all request day off send to leader on this day
        for (RequestDayOffEntity entity: iRequestDayOffRepository.getAllRequestSendMe(id,startTime,endTime,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("DayOff")
                    .build());
        }

        // Get all request device send to leader on this day
        for (RequestDeviceEntity entity: iRequestDeviceRepository.getAllRequestSendMe(id,startTime,endTime,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Device")
                    .build());
        }

        // Get all request OT send to leader on this day
        for (RequestOTEntity entity: iRequestOTRepository.getAllRequestSendMe(id,startTime,endTime,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("OT")
                    .build());
        }

        for (RequestAttendEntity entity: iRequestAttendRepository.getAllRequestSendMe(id,startTime,endTime,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getUpdatedDate()))
                    .status(entity.getStatus())
                    .description(null)
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Attend")
                    .build());
        }



        return requestSendMeDtoList.stream().sorted(((o1, o2) -> o2.getTimeCreateTypeDate().compareTo(o1.getTimeCreateTypeDate()))).collect(Collectors.toList());
    }

    @Override
    public List<RequestSendMeDto> getAllRequestCreateByMe(Long id, Pageable pageable) {
        List<RequestSendMeDto> requestSendMeDtoList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        // Get all request modifi create by me
        Long userAuthent = SecurityUtils.getCurrentUserId();
        if(userAuthent != id)
            throw new CustomHandleException(21);
        for (RequestModifiEntity entity:iRequestModifiRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .idUserAssign(entity.getAssignTo().getUserId())
                    .nameUserAssign(entity.getAssignTo().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Modifi")
                    .build());
        }
        // Get all request day off create by me
        for (RequestDayOffEntity entity: iRequestDayOffRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .idUserAssign(entity.getAssignTo().getUserId())
                    .nameUserAssign(entity.getAssignTo().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("DayOff")
                    .build());
        }

        // Get all request device create by me
        for (RequestDeviceEntity entity: iRequestDeviceRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .idUserAssign(entity.getAssignTo().getUserId())
                    .nameUserAssign(entity.getAssignTo().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Device")
                    .build());
        }

        // Get all request OT create by me
        for (RequestOTEntity entity: iRequestOTRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getCreatedDate()))
                    .status(entity.getStatus())
                    .description(entity.getDescription())
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .idUserAssign(entity.getAssignTo().getUserId())
                    .nameUserAssign(entity.getAssignTo().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("OT")
                    .build());
        }
        // Get all request attend create by me
        for (RequestAttendEntity entity: iRequestAttendRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getUpdatedDate()))
                    .status(entity.getStatus())
                    .description(null)
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Attend")
                    .build());
        }

        for (RequestAttendEntity entity: iRequestAttendRepository.getAllRequestCreateByMe(id,pageable)) {
            requestSendMeDtoList.add(RequestSendMeDto
                    .builder()
                    .idRequest(entity.getId())
                    .timeCreate(simpleDateFormat.format(entity.getUpdatedDate()))
                    .status(entity.getStatus())
                    .description(null)
                    .idUserCreate(entity.getCreateBy().getUserId())
                    .nameUserCreate(entity.getCreateBy().getFullName())
                    .timeCreateTypeDate(entity.getCreatedDate())
                    .type("Attend")
                    .build());
        }


        return requestSendMeDtoList.stream().sorted(((o1, o2) -> o2.getTimeCreateTypeDate().compareTo(o1.getTimeCreateTypeDate()))).collect(Collectors.toList());
    }
}
