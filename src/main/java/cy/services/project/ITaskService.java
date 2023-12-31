package cy.services.project;

import cy.dtos.project.TaskDto;
import cy.entities.project.TaskEntity;
import cy.models.project.SubTaskUpdateModel;
import cy.models.project.TaskModel;
import cy.models.project.TaskSearchModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService extends IBaseService<TaskEntity, TaskDto, TaskModel, Long> {
    boolean changIsDeleteById(Long id);
    Page<TaskDto> findByPage(Integer pageIndex, Integer pageSize, TaskModel taskModel);
    Page<TaskDto> findAllByProjectId(Long id, Pageable pageable);
    boolean updateStatusTask(Long subTaskId, SubTaskUpdateModel subTaskUpdateModel);
    List<TaskDto> searchTask(TaskSearchModel taskSearchModel);
}
