package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.RequestModifiModel;
import cy.services.IResquestModifiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_modifi/")
public class RequesModifiResouce {
    @Autowired
    IResquestModifiService iResquestModifiService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all request modifi with Pageable")
    @GetMapping
    public ResponseDto getAllRequestModifi(Pageable pageable){
        return ResponseDto.of(iResquestModifiService.findAll(pageable));
    }

    @RolesAllowed({RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Add new request modifi")
    @PostMapping
    public ResponseDto addRequestModifi(RequestModifiModel model) throws IOException {
        return ResponseDto.of(iResquestModifiService.add(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Update request modifi")
    @PutMapping
    public ResponseDto updateRequestModifi(RequestModifiModel model) throws IOException {
        return ResponseDto.of(iResquestModifiService.update(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Delete request modifi")
    @DeleteMapping
    public ResponseDto deleteRequestModifi(Long id){
        return ResponseDto.of(iResquestModifiService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Find request modifi by id")
    @GetMapping("find-by-id")
    public ResponseDto findRequestModifi(Long id){
        return ResponseDto.of(iResquestModifiService.findById(id));
    }

}
