package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.CustomHandleException;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.CreateUpdateRequestAttend;
import cy.dtos.RequestAttendDto;
import cy.models.RequestAttendModel;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import java.util.List;
import java.util.Date;

@RequestMapping(value = FrontendConfiguration.PREFIX_API+"request_attend")
@RestController
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping(value = "/create")
    public ResponseDto create(CreateUpdateRequestAttend addAttendRequest) {
        RequestAttendModel requestAttendModel = requestAttendService.requestToModel(addAttendRequest, 1);
        RequestAttendDto result = this.requestAttendService.add(requestAttendModel);
        return ResponseDto.of(result);
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping(value = "/update")
    public ResponseDto update(CreateUpdateRequestAttend updateAttendRequest) {
        RequestAttendModel requestAttendModel = requestAttendService.requestToModel(updateAttendRequest, 2);
        RequestAttendDto result = this.requestAttendService.update(requestAttendModel);
        return ResponseDto.of(result);
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @DeleteMapping(value = "/delete/{id}")
    public ResponseDto delete(@PathVariable Long id) {
        boolean result = this.requestAttendService.deleteById(id);
        if(!result){
            throw new CustomHandleException(36);
        }
        return ResponseDto.of("Delete request attend by id " + id + " success");
    }

    @RolesAllowed({RoleEntity.LEADER, RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR})
    @PostMapping(value = "/change-status")
    public ResponseDto changeRequestStatus(@Valid Long id , String reasonCancel, @Valid boolean status){
        RequestAttendDto requestAttendDto = this.requestAttendService.changeRequestStatus(id,reasonCancel,status);
        if(requestAttendDto.getReasonCancel().equals("1"))
            throw new CustomHandleException(42);
        else if (requestAttendDto.getReasonCancel().equals("2")) {
            throw new CustomHandleException(43);
        } else if (requestAttendDto.getId()!=null) {
            return ResponseDto.of(requestAttendDto);
        }else
            throw new CustomHandleException(41);
    }
}
