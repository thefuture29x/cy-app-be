package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.RequestDayOffDto;
import cy.dtos.ResponseDto;
import cy.entities.RequestDayOffEntity;
import cy.models.RequestDayOffModel;
import cy.repositories.IRequestDayOffRepository;
import cy.services.IRequestDayOffService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_day_off/")
public class RequestDayOffResource {
    @Autowired
    IRequestDayOffRepository iRequestDayOffRepository;
    @Autowired
    IRequestDayOffService iRequestDayOffService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(name = "findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCode(200);
        responseDto.setData(RequestDayOffDto.toDto(iRequestDayOffRepository.findById(id).get()));
        return responseDto;
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(name = "createOrUpdate")
    public ResponseDto CreateOrUpdate(@ModelAttribute RequestDayOffModel requestDayOffModel) throws IOException {
        return ResponseDto.of(iRequestDayOffService.createOrUpdate(requestDayOffModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(name = "delete")
    public ResponseDto deleteRequestDayOff(@RequestParam(name = "id") Long id){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCode(200);
        iRequestDayOffRepository.deleteById(id);
        return responseDto;
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(name = "findByPage")
    public ResponseDto findByPage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        return ResponseDto.of(iRequestDayOffService.getByPage(pageIndex, pageSize));
    }

}
