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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @GetMapping(value = "findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCode(200);
        responseDto.setData(RequestDayOffDto.toDto(iRequestDayOffRepository.findById(id).get()));
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


}
