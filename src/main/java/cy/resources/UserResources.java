package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.entities.RoleEntity_;
import cy.entities.UserEntity;
import cy.entities.UserEntity_;
import cy.models.PasswordModel;
import cy.models.UserModel;
import cy.models.UserProfileModel;
import cy.services.IUserService;
import cy.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Join;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;


@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "users")
public class UserResources {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final IUserService userService;

    public UserResources(IUserService userService) {
        this.userService = userService;
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @GetMapping("{id}")
    public ResponseDto getUserById(@PathVariable Long id) {
        return ResponseDto.of(this.userService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseDto findAll(@RequestParam(name = "isEnable", defaultValue = "1") Boolean isEnable, Pageable page) {
        Specification<UserEntity> specs;
        Specification<UserEntity> statusCheck = ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(UserEntity_.STATUS), isEnable);
        });
        Specification<UserEntity> isRoot = ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.notEqual(root.get(UserEntity_.USER_ID), 1);
        });
        specs = Specification.where(statusCheck).and(isRoot);
        return ResponseDto.of(this.userService.filter(page, specs));
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseDto addUser(@RequestBody @Valid UserModel model) throws IOException {
        model.setId(null);
        return ResponseDto.of(this.userService.add(model));
    }

    @PostMapping("/register")
    public ResponseDto registerUser(@RequestBody @Valid UserModel model) throws IOException {
        model.setId(null);
        return ResponseDto.of(this.userService.add(model));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseDto updateUser(@PathVariable Long id, @RequestBody @Valid UserModel model) throws IOException {
        model.setId(id);
        return ResponseDto.of(this.userService.update(model));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseDto deleteUser(@PathVariable Long id) {
        return ResponseDto.of(this.userService.deleteById(id));
    }


    @PostMapping("login")
    public ResponseDto loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        log.info("{} is logging in system", model.getUsername());
        JwtLoginResponse jwtUserLoginModel = this.userService.logIn(model);
        return ResponseDto.of(jwtUserLoginModel);
    }

    @GetMapping("search")
    public ResponseDto search(@RequestParam @Valid @NotBlank String q, @RequestParam(name = "isEmp", defaultValue = "1") Boolean isEmp, Pageable pageable) {
        Specification<UserEntity> specs;
        Specification<UserEntity> isEnable = ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserEntity_.STATUS), true));
        Specification<UserEntity> excludeAdministrator = ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(UserEntity_.USER_ID), 1L));

        Specification<UserEntity> likeSpec = ((root, query, criteriaBuilder) -> {
            String s = "%" + q + "%";
            return criteriaBuilder.or(criteriaBuilder.like(root.get(UserEntity_.USER_NAME), s), criteriaBuilder.like(root.get(UserEntity_.FULL_NAME), s));
        });

        if (!isEmp)
            specs = Specification.where(likeSpec)
                    .and(excludeAdministrator)
                    .and(((root, query, criteriaBuilder) -> {
                Join<UserEntity, RoleEntity> join = root.join(UserEntity_.ROLE_ENTITY);
                return criteriaBuilder.equal(join.get(RoleEntity_.ROLE_NAME), RoleEntity.EMPLOYEE).not();
            })).and(isEnable);
        else
            specs = Specification.where(likeSpec).and(isEnable).and(excludeAdministrator);

        return ResponseDto.of(this.userService.filter(pageable, specs));
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @PostMapping("set_password")
    public ResponseDto setPassword(@RequestBody @Valid PasswordModel model) {
        return ResponseDto.of(this.userService.setPassword(model));

    }

    @GetMapping("get_managers")
    public ResponseDto getManagers(Pageable page) {
        return ResponseDto.of(this.userService.filter(page,
                Specification.where(
                        ((root, query, criteriaBuilder) -> {
                            Join<UserEntity, RoleEntity> join = root.join(UserEntity_.ROLE_ENTITY);
                            return criteriaBuilder.equal(join.get(RoleEntity_.ROLE_NAME), RoleEntity.EMPLOYEE).not();
                        })
                )));
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @PatchMapping("change_status/{id}")
    public ResponseDto changeStatus(@PathVariable Long id) {
        return ResponseDto.of(this.userService.changeStatus(id));
    }


    @GetMapping("my_profile")
    public ResponseDto getMyProfile() {
        return ResponseDto.of(this.userService.findById(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("change_password")
    public ResponseDto changePassword(@RequestBody String password) {
        return ResponseDto.of(this.userService.changePassword(password));
    }

    @PatchMapping("update_my_profile")
    public ResponseDto updateMyProfile(@RequestBody UserProfileModel model) {
        return ResponseDto.of(this.userService.updateMyProfile(model));
    }

    @PatchMapping("change_my_avatar")
    public ResponseDto changeMyAvatar(MultipartFile file) {
        return ResponseDto.of(this.userService.changeMyAvatar(file));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN','ROLE_MANAGER','ROLE_LEADER')")
    @Operation(summary = "Get all request sent to me")
    @GetMapping("get_request_sent_to_me")
    public ResponseDto getAllRequestSendMe(@RequestParam(value = "id") Long id, Pageable pageable) {
        return ResponseDto.of(this.userService.getAllRequestSendMe(id, pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE','ROLE_LEADER')")
    @Operation(summary = "Get all request create by me")
    @GetMapping("get_request_create_by_me")
    public ResponseDto getAllRequestCreateByMe(@RequestParam(value = "id") Long id, Pageable pageable) {
        return ResponseDto.of(this.userService.getAllRequestCreateByMe(id, pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN','ROLE_MANAGER','ROLE_LEADER','ROLE_EMPLOYEE')")
    @Operation(summary = "Get request by id and type")
    @GetMapping("get_request_by_id_and_type")
    public ResponseDto findRequestByIdAndType(@RequestParam(value = "id") Long id, @RequestParam(value = "type") String type) {
        return ResponseDto.of(this.userService.getRequestByIdAndType(id, type));
    }

    @PostMapping("get_user_by_role_name")
    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    public ResponseDto getUserByRoleName(@RequestParam String roleName) {
        return ResponseDto.of(this.userService.getUserByRoleName(roleName));
    }

    @PostMapping("getAll_user_by_role_name")
    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    public ResponseDto getAllUserByRoleName(@RequestParam String roleName) {
        return ResponseDto.of(this.userService.getAllUserByRoleName(roleName));
    }

    @GetMapping("findAll")
    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    public ResponseDto findAll() {
        return ResponseDto.of(this.userService.findAll());
    }

}
