package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.dtos.RoleDto;
import cy.repositories.IRoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "roles")
public class RoleResources {
    private final IRoleRepository roleRepository;

    public RoleResources(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN')")
    @GetMapping
    @Transactional
    public ResponseDto getAll() {
        return ResponseDto.of(this.roleRepository.findAll()
                .stream()
                .map(r -> RoleDto.builder()
                        .roleId(r.getRoleId())
                        .roleName(r.getRoleName())
                        .build())
                .collect(Collectors.toList()));
    }
}
