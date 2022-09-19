package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.TagDto;
import cy.dtos.UserDto;
import cy.dtos.project.FileDto;
import cy.dtos.project.TaskDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.FileModel;
import cy.models.project.TagModel;
import cy.models.project.TagRelationModel;
import cy.models.project.TaskModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.*;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements ITaskService {
    private final ITaskRepository repository;
    private final IFileService fileService;
    private final IFileRepository fileRepository;
    private final IFeatureRepository featureRepository;
    private final IUserProjectRepository userProjectRepository;
    private final IUserRepository userRepository;
    private final ITagRelationService tagRelationService;
    private final ITagRelationRepository tagRelationRepository;
    private final ITagService tagService;
    private final ITagRepository tagRepository;
    private final IHistoryLogService iHistoryLogService;
    private final ISubTaskService subTaskService;
    private final ISubTaskRepository subTaskRepository;

    public TaskServiceImpl(ITaskRepository repository, IFileService fileService, IFileRepository fileRepository, IFeatureRepository featureRepository, IUserProjectRepository userProjectRepository, IUserRepository userRepository, ITagRelationService tagRelationService, ITagRelationRepository tagRelationRepository, ITagService tagService, ITagRepository tagRepository, IHistoryLogService iHistoryLogService, ISubTaskService subTaskService, ISubTaskRepository subTaskRepository) {
        this.repository = repository;
        this.fileService = fileService;
        this.fileRepository = fileRepository;
        this.featureRepository = featureRepository;
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.tagRelationService = tagRelationService;
        this.tagRelationRepository = tagRelationRepository;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.iHistoryLogService = iHistoryLogService;
        this.subTaskService = subTaskService;
        this.subTaskRepository = subTaskRepository;
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
        taskEntity.setFeature(this.featureRepository.findById(model.getFeatureId()).orElseThrow(() -> new RuntimeException("Feature not exist !!!")));

        // set status if startDate before currentDate status = progress, or currentDate before startDate => status = to-do
        Date currentDate = new Date();
        if (model.getStartDate().before(currentDate)) {
            taskEntity.setStatus(Const.status.IN_PROGRESS.name());
        } else {
            taskEntity.setStatus(Const.status.TO_DO.name());
        }

        taskEntity = this.repository.saveAndFlush(taskEntity);

        // add tag
        List<String> tagList = new ArrayList<>();
        if (model.getTagNames() != null && model.getTagNames().size() > 0) {
            for (String tagName : model.getTagNames()) {
                TagEntity tagEntity = this.tagRepository.findByName(tagName);
                if (tagEntity == null) {
                    TagModel newTagModel = TagModel.builder().name(tagName).build();
                    TagDto newTagDto = this.tagService.add(newTagModel);
                    TagRelationModel tagRelationModel = TagRelationModel.builder()
                            .idTag(newTagDto.getId())
                            .objectId(taskEntity.getId())
                            .category(Const.tableName.TASK.name())
                            .build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(newTagDto.getName());
                } else {
                    TagRelationModel tagRelationModel = TagRelationModel.builder()
                            .idTag(tagEntity.getId())
                            .objectId(taskEntity.getId())
                            .category(Const.tableName.TASK.name())
                            .build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(tagEntity.getName());
                }

            }
        }

        // add dev
        List<UserDto> devList = new ArrayList<>();
        if (model.getDevIds() != null && model.getDevIds().size() > 0) {
            for (Long devId : model.getDevIds()) {
                UserProjectEntity userProject = this.addDev(devId);
                userProject.setObjectId(taskEntity.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity1 = this.userRepository.findById(devId).orElseThrow(() -> new CustomHandleException(11));
                devList.add(UserDto.toDto(userEntity1));
            }
        }

        // save file
        List<String> fileAfterSave = new ArrayList<>();
        for (MultipartFile file : model.getFiles()) {
            FileModel fileModel = new FileModel();
            fileModel.setFile(file);
            fileModel.setObjectId(taskEntity.getId());
            fileModel.setCategory(Const.tableName.TASK.name());
            FileDto fileAfterSaveZ = fileService.add(fileModel);
            fileAfterSave.add(fileAfterSaveZ.getLink());
        }
        TaskDto result = TaskDto.toDto(this.repository.saveAndFlush(taskEntity));
        result.setFiles(fileAfterSave);
        result.setTagName(tagList);
        result.setDevList(devList);
        iHistoryLogService.logCreate(taskEntity.getId(), taskEntity, Const.tableName.TASK);
        return result;
    }

    @Override
    public List<TaskDto> add(List<TaskModel> model) {
        return null;
    }

    @Override
    public TaskDto update(TaskModel model) {
        TaskEntity taskExist = (TaskEntity) Const.copy(this.getById(model.getId()));
        TaskEntity taskOld = this.getById(model.getId());

        taskOld.setStartDate(model.getStartDate());
        taskOld.setEndDate(model.getEndDate());
        taskOld.setName(model.getName());
        taskOld.setIsDeleted(false);
        taskOld.setIsDefault(model.getIsDefault());
        taskOld.setDescription(model.getDescription());
        taskOld.setPriority(model.getPriority());

        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        taskOld.setCreateBy(userEntity);

        // feature save
        taskOld.setFeature(this.featureRepository.findById(model.getFeatureId()).orElseThrow(() -> new RuntimeException("Feature not exist !!!")));

        // set status if startDate before currentDate status = progress, or currentDate before startDate => status = to-do
        Date currentDate = new Date();
        if (model.getStartDate().before(currentDate)) {
            taskOld.setStatus(Const.status.IN_PROGRESS.name());
        } else {
            taskOld.setStatus(Const.status.TO_DO.name());
        }

        TaskEntity taskupdate = this.repository.saveAndFlush(taskOld);

        // add tag
        List<String> tagList = new ArrayList<>();
        if (model.getTagNames() != null && model.getTagNames().size() > 0) {
            // delete tag relation old
            List<TagRelationEntity> tagRelationEntities = this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), model.getId());
            tagRelationEntities.stream().forEach(tagRelationEntity -> this.tagRelationRepository.delete(tagRelationEntity));

            for (String tagName : model.getTagNames()) {
                TagEntity tagEntity = this.tagRepository.findByName(tagName);
                if (tagEntity == null) {
                    TagModel newTagModel = TagModel.builder().name(tagName).build();
                    TagDto newTagDto = this.tagService.add(newTagModel);
                    TagRelationModel tagRelationModel = TagRelationModel.builder()
                            .idTag(newTagDto.getId())
                            .objectId(taskupdate.getId())
                            .category(Const.tableName.TASK.name())
                            .build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(newTagDto.getName());
                } else {
                    TagRelationModel tagRelationModel = TagRelationModel.builder()
                            .idTag(tagEntity.getId())
                            .objectId(taskupdate.getId())
                            .category(Const.tableName.TASK.name())
                            .build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(tagEntity.getName());
                }

            }
        }

        // delete dev old
        List<UserProjectEntity> oldUserProjects = this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), taskOld.getId());
        oldUserProjects.stream().forEach(oldUserProject -> this.userProjectRepository.delete(oldUserProject));
        // add dev
        List<UserDto> devList = new ArrayList<>();
        if (model.getDevIds() != null && model.getDevIds().size() > 0) {
            for (Long devId : model.getDevIds()) {
                UserProjectEntity userProject = this.addDev(devId);
                userProject.setObjectId(taskupdate.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity1 = this.userRepository.findById(devId).orElseThrow(() -> new CustomHandleException(11));
                devList.add(UserDto.toDto(userEntity1));
            }
        }

        // delete old file
        List<FileEntity> fileEntities = this.fileRepository.getByCategoryAndObjectId(Const.tableName.TAG.name(), model.getId());
        if(!fileEntities.isEmpty()){
            fileEntities.forEach(file -> this.fileRepository.deleteById(file.getId()));
        }

        // save file
        List<String> fileAfterSave = new ArrayList<>();
        for (MultipartFile file : model.getFiles()) {
            FileModel fileModel = new FileModel();
            fileModel.setFile(file);
            fileModel.setObjectId(taskupdate.getId());
            fileModel.setCategory(Const.tableName.TASK.name());
            FileDto fileAfterSaveZ = fileService.add(fileModel);
            fileAfterSave.add(fileAfterSaveZ.getLink());
        }
        TaskDto result = TaskDto.toDto(this.repository.saveAndFlush(taskupdate));
        result.setFiles(fileAfterSave);
        result.setTagName(tagList);
        result.setDevList(devList);

//        iHistoryLogService.logUpdate(taskupdate.getId(),taskExist,taskupdate, Const.tableName.TASK);

        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            // delete subTask
            List<SubTaskEntity> subTaskEntities = this.subTaskRepository.findByTaskId(id);
            subTaskEntities.forEach(subTaskEntity -> this.subTaskService.deleteById(subTaskEntity.getId()));

            // delete userProject
            List<UserProjectEntity> userProjectEntities = this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), id);
            for (UserProjectEntity userProjectEntity : userProjectEntities) {
                this.userProjectRepository.delete(userProjectEntity);
            }
            //delete tag_relation
            List<TagRelationEntity> tagRelationEntities = this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), id);
            for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
                this.tagRelationRepository.delete(tagRelationEntity);
            }
            // delete file
            fileRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), id).stream().forEach(fileEntity -> this.fileService.deleteById(fileEntity.getId()));

            // delete Task
            this.repository.deleteById(id);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    // add dev
    public UserProjectEntity addDev(Long id) {
        // objectId not save yet => be will add task
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        UserProjectEntity userProject = UserProjectEntity.builder()
                .idUser(userEntity.getUserId())
                .type(Const.type.TYPE_DEV.name())
                .category(Const.tableName.TASK.name())
                .build();

        return this.userProjectRepository.saveAndFlush(userProject);
    }

    @Override
    public boolean changIsDeleteById(Long id) {
        TaskEntity oldTask = this.getById(id);
        oldTask.setIsDeleted(true);
        this.repository.saveAndFlush(oldTask);
        iHistoryLogService.logDelete(id, oldTask, Const.tableName.TASK);
        return true;
    }
}
