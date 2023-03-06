package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.ProjectModel;
import cy.services.project.IProjectService;
import cy.services.project.IUserViewProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "project")
@RestController
public class ProjectResource {
    @Autowired
    IProjectService iProjectService;
    @Autowired
    IUserViewProjectService iUserViewProjectService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id) {
        return ResponseDto.of(iProjectService.findById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/create")
    public ResponseDto create(@ModelAttribute ProjectModel projectModel) throws IOException {
        return ResponseDto.of(iProjectService.createProject(projectModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/update")
    public ResponseDto update(@ModelAttribute ProjectModel projectModel) throws IOException, ParseException {
        return ResponseDto.of(iProjectService.updateProject(projectModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete")
    public ResponseDto delete(@RequestParam(name = "id")Long id) {
        return ResponseDto.of(iProjectService.changIsDeleteById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/findBypage")
    public ResponseDto findByPage(@RequestParam(name = "pageIndex") Integer pageIndex,
                                  @RequestParam(name = "pageSize") Integer pageSize,
                                  @RequestParam(name = "sortBy") String sortBy,
                                  @RequestParam(name = "sortType") String sortType,
                                  @RequestBody ProjectModel projectModel) {
        return ResponseDto.of(iProjectService.findByPage(pageIndex,pageSize,sortBy,sortType,projectModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findProjectRecentlyViewed")
    public ResponseDto findProjectRecentlyViewed() {
        return ResponseDto.of(iUserViewProjectService.findProjectRecentlyViewed());
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/getAllUserInProject")
    public ResponseDto getAllUserInProject(@RequestParam(name = "category") String category,
                                           @RequestParam(name = "type") String type,
                                           @RequestParam(name = "idObject") Long idObject) {
        return ResponseDto.of(iProjectService.getAllUserInProject(category,type,idObject));
    }


}
