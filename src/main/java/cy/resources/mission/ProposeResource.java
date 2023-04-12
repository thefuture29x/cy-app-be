package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.mission.ProposeModel;
import cy.models.project.ProjectModel;
import cy.services.mission.IProposeService;
import cy.services.mission.IUserViewMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "propose")
@RestController
public class ProposeResource {
    @Autowired
    IProposeService iProposeService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/create")
    public ResponseDto createPropose(@ModelAttribute ProposeModel proposeModel) throws IOException {
        return ResponseDto.of(iProposeService.createPropose(proposeModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/find-all-of-object")
    public ResponseDto findAllOfObject(@RequestParam(name = "id") Long id,
                                       @RequestParam(name = "category") String category){
        return ResponseDto.of(iProposeService.findAllOfObject(id, category));
    }

}
