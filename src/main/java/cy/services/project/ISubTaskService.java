package cy.services.project;

import cy.dtos.project.SubTaskDto;
import cy.entities.project.SubTaskEntity;
import cy.models.project.SubTaskModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISubTaskService extends IBaseService<SubTaskEntity, SubTaskDto, SubTaskModel, Long> {
    boolean version2DeleteById(Long id);

    Page<SubTaskDto> searchAndFilter(SubTaskModel subTaskModel, Pageable pageable);
}
