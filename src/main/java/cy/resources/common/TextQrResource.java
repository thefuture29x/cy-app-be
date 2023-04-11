package cy.resources.common;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.entities.common.RoleEntity;
import cy.models.attendance.TextQrModel;
import cy.services.attendance.ITextQrService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"text_qr/")
public class TextQrResource {
    @Autowired
    private ITextQrService textQrService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER, RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all text QR data")
    @GetMapping
    public ResponseDto getAll(Pageable pageable) {
        return ResponseDto.of(textQrService.findAll(pageable));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER, RoleEntity.EMPLOYEE})
    @Operation(summary = "Add new text QR data")
    @PostMapping("create")
    public ResponseDto create(@Valid @ModelAttribute TextQrModel textQrModel) {
        return ResponseDto.of(textQrService.add(textQrModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER, RoleEntity.EMPLOYEE})
    @Operation(summary = "Update text QR data")
    @PostMapping("update")
    public ResponseDto update(@Valid @ModelAttribute TextQrModel textQrModel) {
        return ResponseDto.of(textQrService.update(textQrModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER, RoleEntity.EMPLOYEE})
    @Operation(summary = "Delete text QR data")
    @DeleteMapping("delete")
    public ResponseDto delete(@RequestParam(name = "id") Long id) {
        return ResponseDto.of(textQrService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER, RoleEntity.EMPLOYEE})
    @Operation(summary = "Find text QR by id")
    @GetMapping("find-by-id/{id}")
    public ResponseDto findById(@PathVariable("id") Long id){
        return ResponseDto.of(textQrService.findById(id));
    }
}
