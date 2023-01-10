package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.BugHistoryModel;
import cy.services.project.impl.BugHistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "bug-history")
public class BugHistoryResource {
    @Autowired
    BugHistoryServiceImpl bugHistoryService;

    @PostMapping("/create")
    public ResponseDto createBugHistory(BugHistoryModel bugHistoryModel) {
        return ResponseDto.of(bugHistoryService.add(bugHistoryModel));
    }
    @PostMapping("/update")
    public ResponseDto updateBugHistory(BugHistoryModel bugHistoryModel) {
        return ResponseDto.of(bugHistoryService.update(bugHistoryModel));
    }


}
