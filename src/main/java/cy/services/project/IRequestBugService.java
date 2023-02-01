package cy.services.project;

import cy.dtos.project.AllBugDto;
import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.models.project.BugModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRequestBugService extends IBaseService<BugEntity, BugDto, BugModel,Long> {
    void deleteBug(Long id);
    Page<BugDto> findAllBugOfProject(Long idProject, Pageable pageable);
    BugDto updateStatusBugToSubTask(Long id, int status);
    BugDto updateStatusSubTaskToBug(Long id, int status);
    BugDto updateStatusBugToTask(Long id, int status);
    BugDto updateStatusTaskToBug(Long id, int status);
    AllBugDto getAllBug(Long idProject);
}
