package cy.resources.common;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.repositories.project.specification.HistoryLogSpecification;
import cy.services.common.IHistoryLogService;
import cy.utils.Const;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "history-log")
public class HistoryLogResource {
    private final IHistoryLogService historyLogService;

    public HistoryLogResource(IHistoryLogService historyLogService) {
        this.historyLogService = historyLogService;
    }

    @GetMapping("{object_id}")
    public ResponseDto getHistoryOfProjects(@PathVariable(name = "object_id") Long objectId, @RequestParam Const.tableName category, Pageable page) {
//        return ResponseDto.of(this.historyLogService.filter(page, HistoryLogSpecification.byObjectAndCategory(objectId, category)));
        return ResponseDto.of(this.historyLogService.getAllHistory(objectId,category,page));
    }

    @GetMapping("/all-history")
    public ResponseDto getAllHistoryOfProject(@RequestParam Const.tableName category, Pageable page) {
        return ResponseDto.of(this.historyLogService.filter(page, HistoryLogSpecification.byCategoryAndContent(category)));
    }
    @GetMapping("/all-history-of-bug")
    public ResponseDto getAllHistoryOfBug(@RequestParam Long idProject, Pageable page) {
        return ResponseDto.of(this.historyLogService.getAllHistoryOfBug(idProject,page));
    }

}
