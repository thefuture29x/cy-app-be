package cy.services.project;

import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.models.project.BugModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRequestBugService extends IBaseService<BugEntity, BugDto, BugModel,Long> {
    void deleteBug(Long id);
    Page<BugDto> findAllBugOfProject(Long idProject, Pageable pageable);
}
