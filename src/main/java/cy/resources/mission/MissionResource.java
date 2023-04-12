package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.mission.MissionModel;
import cy.models.project.ProjectModel;
import cy.services.mission.IMissionService;
import cy.services.mission.IUserViewMissionService;
import cy.services.project.IProjectService;
import cy.services.project.IUserViewProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "mission")
@RestController
public class MissionResource {
    @Autowired
    IMissionService iMissionService;
    @Autowired
    IUserViewMissionService iUserViewMissionService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id,@RequestParam(name = "view") boolean view) {
        return ResponseDto.of(iMissionService.findById(id,view));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/create")
    public ResponseDto create(@ModelAttribute MissionModel missionModel) throws IOException {
        return ResponseDto.of(iMissionService.createMission(missionModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/update")
    public ResponseDto update(@ModelAttribute MissionModel missionModel) throws IOException, ParseException {
        return ResponseDto.of(iMissionService.updateMission(missionModel));
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete")
    public ResponseDto delete(@RequestParam(name = "id")Long id) {
        return ResponseDto.of(iMissionService.changIsDeleteById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/findBypage")
    public ResponseDto findByPage(@RequestParam(name = "pageIndex") Integer pageIndex,
                                  @RequestParam(name = "pageSize") Integer pageSize,
                                  @RequestParam(name = "sortBy") String sortBy,
                                  @RequestParam(name = "sortType") String sortType,
                                  @RequestBody MissionModel missionModel) {
        return ResponseDto.of(iMissionService.findByPage(pageIndex,pageSize,sortBy,sortType,missionModel));

    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/getAllUserInProject")
    public ResponseDto getAllUserInProject(@RequestParam(name = "category") String category,
                                           @RequestParam(name = "type") String type,
                                           @RequestParam(name = "idObject") Long idObject) {
        return ResponseDto.of(iMissionService.getAllUserInMission(category,type,idObject));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findMissionRecentlyViewed")
    public ResponseDto findMissionRecentlyViewed() {
        return ResponseDto.of(iUserViewMissionService.findProjectRecentlyViewed());
    }
}
