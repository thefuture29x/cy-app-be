package cy.services.project;

import cy.dtos.project.SubTaskDto;
import cy.entities.project.SubTaskEntity;
import cy.models.project.SubTaskModel;
import cy.services.IBaseService;

public interface ISubTaskService extends IBaseService<SubTaskEntity, SubTaskDto, SubTaskModel, Long> {
    boolean changIsDeleteById(Long id);
}
