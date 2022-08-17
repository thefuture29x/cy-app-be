package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.RequestOTModel;
import cy.services.IRequestOTService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_ot/")
public class RequestOTResource {
    @Autowired
    private IRequestOTService requestOTService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all request OT with Pageable")
    @GetMapping
    public ResponseDto getAllRequestOT(Pageable pageable){
        return ResponseDto.of(requestOTService.findAll(pageable));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Create new request OT")
    @PostMapping
    public ResponseDto addRequestOT(@ModelAttribute RequestOTModel requestOTModel) {
        return ResponseDto.of(requestOTService.add(requestOTModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Update request OT")
    @PutMapping
    public ResponseDto updateRequestOT(@ModelAttribute RequestOTModel requestOTModel) {
        return ResponseDto.of(requestOTService.update(requestOTModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Delete request OT")
    @DeleteMapping
    public ResponseDto deleteRequestOT(Long id) {
        return ResponseDto.of(requestOTService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Find request OT by id")
    @GetMapping("find-by-id")
    public ResponseDto findRequestOT(Long id){
        return ResponseDto.of(requestOTService.findById(id));
    }
}
