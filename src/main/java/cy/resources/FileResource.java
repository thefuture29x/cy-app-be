package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.attendance.RequestOTModel;
import cy.models.project.FileModel;
import cy.services.project.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"file/")
public class FileResource {
    @Autowired
    private IFileService fileService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Upload new file")
    @PostMapping("upload")
    public ResponseDto uploadFile(@Valid @ModelAttribute FileModel fileModel) {
        return ResponseDto.of(fileService.add(fileModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Update file")
    @PostMapping("updateFile")
    public ResponseDto updateFile(@Valid @ModelAttribute FileModel fileModel) {
        return ResponseDto.of(fileService.update(fileModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Delete File")
    @DeleteMapping("delete")
    public ResponseDto deleteFile(Long id) {
        return ResponseDto.of(fileService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Find file by id")
    @GetMapping("find-by-id/{id}")
    public ResponseDto findFileById(@PathVariable("id") Long id){
        return ResponseDto.of(fileService.findById(id));
    }
}
