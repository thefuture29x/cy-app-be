package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.ProjectModel;
import cy.services.project.IProjectService;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "project")
@RestController
public class ProjectResource {
    @Autowired
    IProjectService iProjectService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/findById")
    public ResponseDto findById(@RequestParam(name = "id") Long id) {
        return ResponseDto.of(iProjectService.findById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/create")
    public ResponseDto create(@ModelAttribute ProjectModel projectModel) {
        return ResponseDto.of(iProjectService.createProject(projectModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/update")
    public ResponseDto update(@ModelAttribute ProjectModel projectModel) {
        return ResponseDto.of(iProjectService.updateProject(projectModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete")
    public ResponseDto delete(@RequestParam(name = "id")Long id) {
        return ResponseDto.of(iProjectService.deleteProject(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/findBypage")
    public ResponseDto findBypage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize, @RequestBody ProjectModel projectModel) {
        return ResponseDto.of(iProjectService.findByPage(pageIndex,pageSize,projectModel));
    }
}
