package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.entities.UserEntity_;
import cy.models.PasswordModel;
import cy.models.UserModel;
import cy.models.UserProfileModel;
import cy.services.IUserService;
import cy.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "users")
public class UserResources {

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
        return ResponseDto.of(this.userService.filter(page, Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserEntity_.STATUS), isEnable)))));
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseDto addUser(@RequestBody @Valid UserModel model) {
        model.setId(null);
        return ResponseDto.of(this.userService.add(model));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseDto updateUser(@PathVariable Long id, @RequestBody @Valid UserModel model) {
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
        JwtLoginResponse jwtUserLoginModel = this.userService.logIn(model);
        return ResponseDto.of(jwtUserLoginModel);
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @GetMapping("search")
    public ResponseDto search(@RequestParam @Valid @NotBlank String q, Pageable pageable) {
        return ResponseDto.of(this.userService.filter(pageable, Specification.where(((root, query, criteriaBuilder) -> {
            String s = "%" + q + "%";
            return criteriaBuilder.or(criteriaBuilder.like(root.get(UserEntity_.USER_NAME), s), criteriaBuilder.like(root.get(UserEntity_.FULL_NAME), s));
        }))));
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @PostMapping("set_password")
    public ResponseDto setPassword(@RequestBody @Valid PasswordModel model) {
        return ResponseDto.of(this.userService.setPassword(model));

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

}
