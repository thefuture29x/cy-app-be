package cy.services.project;

import cy.dtos.project.AllBugDto;
import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.models.project.BugModel;
import cy.models.project.SubTaskUpdateModel;
import cy.services.common.IBaseService;

public interface IRequestBugService extends IBaseService<BugEntity, BugDto, BugModel,Long> {
    void deleteBug(Long id);
    BugDto updateStatusBugToSubTask(Long id, int status);
    BugDto updateStatusSubTaskToBug(Long id, int status);
    BugDto updateStatusBugToTask(Long id, int status);
    BugDto updateStatusTaskToBug(Long id, int status);
    AllBugDto getAllBug(Long idProject);
    BugDto updateStatusBugOfSubtask(Long idSubtask, String newStatusOfBug);
    BugDto updateStatusBugOfTask(Long idTask, String newStatusOfBug);
    void addReviewerToBug(Long idBug, SubTaskUpdateModel subTaskUpdateModel);
}
