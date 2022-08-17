package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.UserModel;
import cy.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
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
    public ResponseDto findAll(Pageable page) {
        return ResponseDto.of(this.userService.findAll(page));
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseDto addUser(@RequestBody @Valid UserModel model) throws IOException {
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
}
