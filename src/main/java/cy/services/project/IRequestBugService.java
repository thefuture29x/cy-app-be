package cy.services.project;

import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.models.project.BugModel;
import cy.services.IBaseService;

public interface IRequestBugService extends IBaseService<BugEntity, BugDto, BugModel,Long> {
    void deleteBug(Long id);
}
