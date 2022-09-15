package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.attendance.OptionDto;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.services.attendance.IOptionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "options")
public class OptionResource {

    private final IOptionService optionService;

    public OptionResource(IOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("find_by_key/{key}")
    public ResponseDto findByKey(@PathVariable @Valid @NotBlank String key) {
        return ResponseDto.of(this.optionService.findByKey(key));
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @DeleteMapping("{id}")
    public ResponseDto deleteByKey(@PathVariable Long id) {
        return ResponseDto.of(this.optionService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @PostMapping
    public ResponseDto saveOption(@RequestBody OptionDto model) {
        return ResponseDto.of(this.optionService.add(model));
    }

}
