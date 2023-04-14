package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.mission.AssignModel;
import cy.models.mission.MissionModel;
import cy.services.mission.IAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "assign")
@RestController
public class AssignResource {
    @Autowired
    IAssignService iAssignService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id) {
        return ResponseDto.of(iAssignService.findById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/create")
    public ResponseDto create(AssignModel assignModel) throws IOException {
        return ResponseDto.of(iAssignService.createAssign(assignModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/update")
    public ResponseDto update(AssignModel assignModel) throws IOException, ParseException {
        return ResponseDto.of(iAssignService.updateAssign(assignModel));
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete")
    public ResponseDto delete(@RequestParam(name = "id")Long id) {
        return ResponseDto.of(iAssignService.changIsDeleteById(id));
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/findBypage")
    public ResponseDto findByPage(@RequestParam(name = "pageIndex") Integer pageIndex,
                                  @RequestParam(name = "pageSize") Integer pageSize,
                                  @RequestParam(name = "sortBy") String sortBy,
                                  @RequestParam(name = "sortType") String sortType,
                                  @RequestBody AssignModel assignModel) {
        return ResponseDto.of(iAssignService.findByPage(pageIndex,pageSize,sortBy,sortType,assignModel));

    }

}
