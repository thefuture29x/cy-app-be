package cy.services.project;

import cy.dtos.project.TaskDto;
import cy.entities.project.TaskEntity;
import cy.models.project.TaskModel;
import cy.services.IBaseService;

public interface ITaskService extends IBaseService<TaskEntity, TaskDto, TaskModel, Long> {
    boolean changIsDeleteById(Long id);
}
