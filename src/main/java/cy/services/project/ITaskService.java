package cy.services.project;

import cy.dtos.project.ProjectDto;
import cy.dtos.project.TaskDto;
import cy.entities.project.TaskEntity;
import cy.models.project.ProjectModel;
import cy.models.project.TaskModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITaskService extends IBaseService<TaskEntity, TaskDto, TaskModel, Long> {
    boolean changIsDeleteById(Long id);
    Page<TaskDto> findByPage(Integer pageIndex, Integer pageSize, TaskModel taskModel);
    Page<TaskDto> findAllByProjectId(Long id, Pageable pageable);
}
