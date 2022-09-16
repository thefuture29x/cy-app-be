package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.TaskDto;
import cy.entities.UserEntity;
import cy.entities.project.TaskEntity;
import cy.models.project.FileModel;
import cy.models.project.TaskModel;
import cy.repositories.project.ITaskRepository;
import cy.services.project.IFileService;
import cy.services.project.ITaskService;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TaskServiceImpl implements ITaskService {
    private final ITaskRepository repository;
    private final IFileService fileService;

    public TaskServiceImpl(ITaskRepository repository, IFileService fileService) {
        this.repository = repository;
        this.fileService = fileService;
    }

    @Override
    public List<TaskDto> findAll() {
        return null;
    }

    @Override
    public Page<TaskDto> findAll(Pageable page) {
        return this.repository.findAll(page).map(task -> TaskDto.toDto(task));
    }

    @Override
    public List<TaskDto> findAll(Specification<TaskEntity> specs) {
        return null;
    }

    @Override
    public Page<TaskDto> filter(Pageable page, Specification<TaskEntity> specs) {
        return null;
    }

    @Override
    public TaskDto findById(Long id) {
        return TaskDto.toDto(this.getById(id));
    }

    @Override
    public TaskEntity getById(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new CustomHandleException(251));
    }

    @Override
    public TaskDto add(TaskModel model) {
        TaskEntity taskEntity = TaskModel.toEntity(model);

        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        taskEntity.setCreateBy(userEntity);

        // feature save

        // logic set status and auto set status of task DONE when status of subtask DONE


        taskEntity = this.repository.saveAndFlush(taskEntity);
        // save file
        for (MultipartFile file : model.getFiles()) {
            FileModel fileModel = new FileModel();
            fileModel.setFile(file);
            fileModel.setObjectId(taskEntity.getId());
            fileModel.setCategory(Const.tableName.TASK.name());
            fileService.add(fileModel);
        }


        return TaskDto.toDto(taskEntity);
    }

    @Override
    public List<TaskDto> add(List<TaskModel> model) {
        return null;
    }

    @Override
    public TaskDto update(TaskModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
