package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.SubTaskModel;
import cy.services.project.ISubTaskService;
import cy.services.project.impl.SubTaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "/subtask")
public class SubTaskResources {
    @Autowired
    ISubTaskService iSubTaskService;
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/add")
    public Object add(@ModelAttribute SubTaskModel subTaskModel) {
        return ResponseDto.of(iSubTaskService.add(subTaskModel));
    }
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PutMapping(value = "/update")
    public Object update(@ModelAttribute SubTaskModel subTaskModel) {
        return ResponseDto.of(iSubTaskService.update(subTaskModel));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete/{id}")
    public Object delete(@PathVariable Long id) {
        return ResponseDto.of(iSubTaskService.version2DeleteById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/find-by-id/{id}")
    public Object findById(@PathVariable Long id) {
        return ResponseDto.of(iSubTaskService.findById(id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/search-filter")
    public Object search(@RequestBody SubTaskModel subTaskModel, Pageable pageable){
        return ResponseDto.of(iSubTaskService.searchAndFilter(subTaskModel, pageable));
    }
}
