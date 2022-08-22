package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.ResponseDto;
import cy.entities.RequestAttendEntity;
import cy.entities.RoleEntity;
import cy.models.CreateUpdateRequestAttend;
import cy.models.RequestAttendModel;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.sql.Date;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "request_attend")
@RestController
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @GetMapping(value = "/{id}")
    public ResponseDto getById(@PathVariable("id") Long id) {
        RequestAttendEntity requestAttend = requestAttendService.getById(id);
        RequestAttendDto requestAttendDto = RequestAttendDto.entityToDto(requestAttend);
        return ResponseDto.of(requestAttendDto);
    }

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
        if (!result) {
            throw new CustomHandleException(36);
        }
        return ResponseDto.of("Delete request attend by id " + id + " success");
    }

    @GetMapping(value = "/find-by-id/{id}")
    public ResponseDto findById(@PathVariable Long id) {
        RequestAttendDto result = this.requestAttendService.findById(id);
        return ResponseDto.of(result);
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity
            .MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping(value = "/find-by-user-id/{userId}")
    public ResponseDto findByUserId(@PathVariable(value = "userId") Long id, Pageable pageable) {
        Page<RequestAttendDto> result = this.requestAttendService.findByUserId(id, pageable);
        return ResponseDto.of(result);
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
    @PostMapping(value = "/check_request_not_exist")
    public ResponseDto checkRequestExist(@RequestParam String day) {
        boolean result = this.requestAttendService.checkRequestAttendNotExist(day);
        return ResponseDto.of(result);
    }

    @GetMapping(value = "/check_request_day_exist")
    public ResponseDto checkRequestExist(@RequestParam Date day) {
        boolean result = this.requestAttendService.checkRequestAttendExist(day);
        return ResponseDto.of(result);
    }
}
