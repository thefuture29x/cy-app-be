package cy.resources.attendance;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestDayOffDto;
import cy.dtos.common.ResponseDto;
import cy.models.attendance.GetRequestDayOffModel;
import cy.models.attendance.RequestDayOffModel;
import cy.repositories.attendance.IRequestDayOffRepository;
import cy.services.attendance.IRequestDayOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_day_off/")
public class RequestDayOffResource {
    @Autowired
    IRequestDayOffRepository iRequestDayOffRepository;
    @Autowired
    IRequestDayOffService iRequestDayOffService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "findById")
    @Transactional
    public ResponseDto findById(@RequestParam(name = "id") Long id){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCode(200);
        responseDto.setData(RequestDayOffDto.toDto(iRequestDayOffRepository.findById(id).orElseThrow( () -> new CustomHandleException(11))));
        return responseDto;
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "create")
    public ResponseDto Create(@ModelAttribute RequestDayOffModel requestDayOffModel) throws IOException {
        return ResponseDto.of(iRequestDayOffService.add(requestDayOffModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "update")
    public ResponseDto Update(@ModelAttribute RequestDayOffModel requestDayOffModel) throws IOException {
        return ResponseDto.of(iRequestDayOffService.update(requestDayOffModel));
    }


    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "getPageBySearch")
    public ResponseDto getPageBySearch(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        Pageable page = PageRequest.of(pageIndex,pageSize);
        return ResponseDto.of(iRequestDayOffService.findAll(page));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "delete")
    public ResponseDto deleteRequestDayOff(@RequestParam(name = "id") Long id){
        return ResponseDto.of(iRequestDayOffService.deleteById(id));
    }

    @Secured({"ROLE_ADMIN","ROLE_LEADER","ROLE_ADMINISTRATOR","ROLE_MANAGER"})
    @PostMapping(value = "change-status")
    public ResponseDto changeRequestStatus(@Valid Long id , String reasonCancel, @Valid boolean status){
        RequestDayOffDto requestDayOffDto = this.iRequestDayOffService.changeRequestStatus(id,reasonCancel,status);
        if(requestDayOffDto==null){
            throw new CustomHandleException(51);
        }
        if(requestDayOffDto.getReasonCancel()!=null){
            if (requestDayOffDto.getReasonCancel().equals("2")) {
                throw new CustomHandleException(52);
            }
                return ResponseDto.of(requestDayOffDto);
        }
        else if(requestDayOffDto.getId()!=null){
            return ResponseDto.of(requestDayOffDto);
        }
            throw new CustomHandleException(53);
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "get_list_day_off_by_month_of_user")
    public ResponseDto getListDayOffByMonthOfUser(@Valid GetRequestDayOffModel requestDayOffModel, Pageable pageable){
        return ResponseDto.of(iRequestDayOffService.getTotalDayOffByMonthOfUser(requestDayOffModel.getDateStart() +" 00:00:00", requestDayOffModel.getDateEnd()+" 00:00:00", requestDayOffModel.getUid(), requestDayOffModel.getIsLegit(), requestDayOffModel.getStatus(), pageable));
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "get_day_off_by_month_of_user")
    public ResponseDto getDayOffByMonthOfUser(@Valid GetRequestDayOffModel requestDayOffModel, Pageable pageable){
        if(requestDayOffModel.getIsLegit()){
            return ResponseDto.of("Tổng ngày nghỉ có lương của nhân viên số "+ requestDayOffModel.getUid() +" là : "+iRequestDayOffService.getTotalDayOffByMonthOfUser(requestDayOffModel.getDateStart() +" 00:00:00", requestDayOffModel.getDateEnd()+" 00:00:00", requestDayOffModel.getUid(), requestDayOffModel.getIsLegit(), requestDayOffModel.getStatus(), pageable).size());
        }
        else {
            return ResponseDto.of("Tổng ngày nghỉ không lương của nhân viên số "+ requestDayOffModel.getUid() +" là : "+iRequestDayOffService.getTotalDayOffByMonthOfUser(requestDayOffModel.getDateStart() +" 00:00:00", requestDayOffModel.getDateEnd()+" 00:00:00", requestDayOffModel.getUid(), requestDayOffModel.getIsLegit(), requestDayOffModel.getStatus(), pageable).size());
        }
    }


}
