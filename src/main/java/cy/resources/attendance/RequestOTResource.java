package cy.resources.attendance;

import cy.configs.FrontendConfiguration;
import cy.dtos.CustomHandleException;
import cy.dtos.attendance.RequestOTDto;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.attendance.RequestOTModel;
import cy.services.attendance.IRequestOTService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

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
    @PostMapping("create")
    public ResponseDto addRequestOT(@Valid @ModelAttribute RequestOTModel requestOTModel) {
        return ResponseDto.of(requestOTService.add(requestOTModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Update request OT")
    @PutMapping("update")
    public ResponseDto updateRequestOT(@Valid @ModelAttribute RequestOTModel requestOTModel) {
        return ResponseDto.of(requestOTService.update(requestOTModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Delete request OT")
    @DeleteMapping("delete")
    public ResponseDto deleteRequestOT(Long id) {
        return ResponseDto.of(requestOTService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Find request OT by id")
    @GetMapping("find-by-id/{id}")
    public ResponseDto findRequestOT(@PathVariable("id") Long id){
        return ResponseDto.of(requestOTService.findById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Accept or cancel request OT")
    @PostMapping("response-request-ot")
    public ResponseDto responseRequestOT(@Valid Long id, String reasonCancel,@Valid Boolean status){
        RequestOTDto requestOTDto = requestOTService.responseOtRequest(id, reasonCancel, status);
        if (requestOTDto != null && requestOTDto.getReasonCancel() != null && !requestOTDto.getReasonCancel().isEmpty()) {
            if (requestOTDto.getReasonCancel().equals("113"))
                throw new CustomHandleException(113);
        }
        return ResponseDto.of(requestOTDto);
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Get total OT hours")
    @GetMapping("total-ot-hours")
    public ResponseDto getTotalOtHours(Long userId, Integer status, Integer typeOt, String startDate, String endDate){
        return ResponseDto.of(requestOTService.totalOTHours(userId, status, typeOt, startDate, endDate));
    }
}
