package cy.resources.attendance;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestAttendDto;
import cy.dtos.common.ResponseDto;
import cy.entities.attendance.RequestAttendEntity;
import cy.entities.common.RoleEntity;
import cy.models.attendance.CreateUpdateRequestAttend;
import cy.models.attendance.RequestAttendByNameAndYearMonth;
import cy.models.attendance.RequestAttendModel;
import cy.services.attendance.IPayRollService;
import cy.services.attendance.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "request_attend")
@RestController
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;
    @Autowired
    private IPayRollService iPayRollService;

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

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity
            .MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping(value = "/find-by-user-name-and-day")
    public ResponseDto findByUserName(RequestAttendByNameAndYearMonth data) throws ParseException {
        List<RequestAttendDto> result = this.requestAttendService.findByUsername(data);
        return ResponseDto.otherData(result,iPayRollService.totalWorkingDayEndWorked(data,null));
    }

    @RolesAllowed({RoleEntity.LEADER, RoleEntity.ADMIN, RoleEntity.ADMINISTRATOR, RoleEntity.MANAGER})
    @PostMapping(value = "/change-status")
    public ResponseDto changeRequestStatus(@Valid Long id , String reasonCancel, @Valid boolean status){
        RequestAttendDto requestAttendDto = this.requestAttendService.changeRequestStatus(id,reasonCancel,status);
        if(requestAttendDto==null){
            throw new CustomHandleException(50);
        }
        if(requestAttendDto.getReasonCancel()!=null){
            if (requestAttendDto.getReasonCancel().equals("2")) {
                throw new CustomHandleException(43);
            }
            return ResponseDto.of(requestAttendDto);
        }
         else if (requestAttendDto.getId()!=null) {
            return ResponseDto.of(requestAttendDto);
        }
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

    @GetMapping(value = "/find_by_day")
    public ResponseDto findByDay(@RequestParam Date day) {
        List<RequestAttendDto> result = this.requestAttendService.findByDay(day);
        return ResponseDto.of(result);
    }

    @GetMapping(value = "/find_by_day_and_user_id")
    public ResponseDto findByDayAndUserId(@RequestParam Date day) {
        List<RequestAttendDto> result = this.requestAttendService.findByMonthAndYear(day);
        return ResponseDto.of(result);
    }
}
