package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.RequestDeviceDto;
import cy.dtos.ResponseDto;
import cy.models.RequestDeviceModel;
import cy.models.RequestDeviceUpdateStatusModel;
import cy.services.impl.RequestDeviceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "request-device")
public class RequestDeviceResource {
    @Autowired
    RequestDeviceServiceImpl requestDeviceService;
    /*
    *@author:HieuMM_Cy
    *@since:8/16/2022-2:13 PM
    *@description:create request device
    *@update:
    **/
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping("/create")
    public Object add(RequestDeviceModel requestDeviceModel) throws IOException {
        RequestDeviceDto requestDeviceDto=requestDeviceService.add(requestDeviceModel);
        if(requestDeviceDto==null){
            return ResponseDto.of(130, "CREATE");
        }else {
            return ResponseDto.of(requestDeviceDto);
        }
    }
    /*
    *@author:HieuMM_Cy
    *@since:8/16/2022-2:14 PM
    *@description:update request device
    *@update:
    **/
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PutMapping("/update")
    public Object update(RequestDeviceModel requestDeviceModel) throws IOException {
        RequestDeviceDto requestDeviceDto=requestDeviceService.update(requestDeviceModel);
        if(requestDeviceDto==null){
            return ResponseDto.of(130, "UPDATE");

        }else {
            return ResponseDto.of(requestDeviceDto);
        }
    }
    /*
    *@author:HieuMM_Cy
    *@since:8/16/2022-2:14 PM
    *@description:get all
    *@update:
    **/
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping("/getAll")
    public Object findAll(Pageable page){
        return ResponseDto.of(requestDeviceService.findAll(page));
    }/*
    *@author:HieuMM_Cy
    *@since:8/16/2022-3:19 PM
    *@description:delete
    *@update:
    **/
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping("/delete/{id}")
    public Object delete(@PathVariable(value = "id") Long id){
        return ResponseDto.of(requestDeviceService.deleteById(id));
    }
    /*
    *@author:HieuMM_Cy
    *@since:8/16/2022-2:21 PM
    *@description:find one by id
    *@update:
    **/
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping("/getOne/{id}")
    public Object getOne(@PathVariable(value = "id") Long id){
        return ResponseDto.of(requestDeviceService.findById(id));
    }
    /*
    *@author:HieuMM_Cy
    *@since:8/17/2022-10:21 AM
    *@description:Xác nhận yêu cầu
    *@update:
    **/
    @Secured({"ROLE_ADMIN","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PutMapping("/acceptRequestDevice")
    public Object acceptRequestDevice(RequestDeviceUpdateStatusModel requestDeviceUpdateStatusModel){
        return ResponseDto.of(requestDeviceService.updateStatus(requestDeviceUpdateStatusModel));
    }
    /*
    *@author:HieuMM_Cy
    *@since:8/17/2022-10:22 AM
    *@description:Cancel yêu cầu
    *@update:
    **/
   /* @Secured({"ROLE_ADMIN","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PutMapping("/cancelRequestDevice")
    public Object cancelRequestDevice(@RequestParam(value = "id") Long id,@RequestParam(value = "reason") String reason){
        return ResponseDto.of(requestDeviceService.updateStatusCancle(id,reason));
    }*/
}
