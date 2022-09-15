package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.SubTaskModel;
import cy.services.project.ISubTaskService;
import cy.services.project.impl.SubTaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "/subtask")
public class SubTaskResources {
    @Autowired
    ISubTaskService iSubTaskService;
    @PostMapping(value = "/add")
    public Object add(@RequestBody SubTaskModel subTaskModel) {
        return ResponseDto.of(iSubTaskService.add(subTaskModel));
    }
}
