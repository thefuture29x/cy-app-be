package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.UserDto;
import cy.dtos.project.*;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.*;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.*;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements ITaskService {
    private final String PROJECT_DETAIL_URL = "http://3.34.98.33/detail-project/";
    private final String FEATURE_DETAIL_URL = "http://3.34.98.33/list_detail_feature?id=";
    private final String TASK_DETAIL_URL = "http://3.34.98.33/detail-task/";
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
    private final ITaskRepository iTaskRepository;
    private final IProjectRepository iProjectRepository;

    @Autowired
    BugServiceImpl bugService;

    @Autowired
    EntityManager manager;

    public TaskServiceImpl(ITaskRepository repository, IFileService fileService, IFileRepository fileRepository, IFeatureRepository featureRepository, IUserProjectRepository userProjectRepository, IUserRepository userRepository, ITagRelationService tagRelationService, ITagRelationRepository tagRelationRepository, ITagService tagService, ITagRepository tagRepository, IHistoryLogService iHistoryLogService, ISubTaskService subTaskService, ISubTaskRepository subTaskRepository, ITaskRepository iTaskRepository, IProjectRepository iProjectRepository) {
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
        this.iTaskRepository = iTaskRepository;
        this.iProjectRepository = iProjectRepository;
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
        if (iTaskRepository.checkIsDeleted(id)) throw new CustomHandleException(491);
        TaskEntity taskEntity = this.getById(id);

        // set Tag
        List<TagRelationEntity> tagRelationEntities = this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), id);
        List<Long> idTags = tagRelationEntities.stream().map(TagRelationEntity::getIdTag).collect(Collectors.toList());
        List<TagEntity> tagEntities = new ArrayList<>();
        if (idTags != null) {
            for (Long idTag : idTags) {
                TagEntity tag = this.tagRepository.findById(idTag).orElseThrow(() -> new RuntimeException("Tag not exist !!!"));
                tagEntities.add(tag);
            }
        }
        taskEntity.setTagList(tagEntities);

        // set devTeam
        List<UserProjectEntity> userProjectEntitiesDev = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), id, Const.type.TYPE_DEV.name());
        List<Long> idUsersDev = userProjectEntitiesDev.stream().map(UserProjectEntity::getIdUser).collect(Collectors.toList());
        List<UserEntity> userEntitiesDev = new ArrayList<>();
        if (idUsersDev != null) {
            for (Long idUser : idUsersDev) {
                UserEntity user = this.userRepository.findById(idUser).orElseThrow(() -> new CustomHandleException(11));
                userEntitiesDev.add(user);
            }
        }
        taskEntity.setDevTeam(userEntitiesDev);

        // set followerTeam
        List<UserProjectEntity> userProjectEntitiesFollower = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), id, Const.type.TYPE_FOLLOWER.name());
        List<Long> idUsersFollower = userProjectEntitiesFollower.stream().map(UserProjectEntity::getIdUser).collect(Collectors.toList());
        List<UserEntity> userEntitiesFollower = new ArrayList<>();
        if (idUsersFollower != null) {
            for (Long idUser : idUsersFollower) {
                UserEntity user = this.userRepository.findById(idUser).orElseThrow(() -> new CustomHandleException(11));
                userEntitiesFollower.add(user);
            }
        }
        taskEntity.setFollowerTeam(userEntitiesFollower);

        // set viewerTeam
        List<UserProjectEntity> userProjectEntitiesViewer = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), id, Const.type.TYPE_VIEWER.name());
        List<Long> idUsersViewer = userProjectEntitiesViewer.stream().map(UserProjectEntity::getIdUser).collect(Collectors.toList());
        List<UserEntity> userEntitiesViewer = new ArrayList<>();
        if (idUsersViewer != null) {
            for (Long idUser : idUsersViewer) {
                UserEntity user = this.userRepository.findById(idUser).orElseThrow(() -> new CustomHandleException(11));
                userEntitiesViewer.add(user);
            }
        }
        taskEntity.setViewerTeam(userEntitiesViewer);

        // set reViewerTeam
        List<UserProjectEntity> userProjectEntitiesReViewer = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(),
                id, Const.type.TYPE_REVIEWER.name());
        List<Long> idUsersReViewer = userProjectEntitiesReViewer.stream().map(UserProjectEntity::getIdUser).collect(Collectors.toList());
        List<UserEntity> userEntitiesReViewer = new ArrayList<>();
        if (idUsersReViewer != null) {
            for (Long idUser : idUsersReViewer) {
                UserEntity user = this.userRepository.findById(idUser).orElseThrow(() -> new CustomHandleException(11));
                userEntitiesReViewer.add(user);
            }
        }
        taskEntity.setReViewerTeam(userEntitiesReViewer);

        TaskDto taskDtoResult = TaskDto.toDto(taskEntity);
        // Set dev list in project
        this.setDevListInProject(taskDtoResult);
        // Set projectId
        taskDtoResult.setProjectId(taskEntity.getFeature().getProject().getId());
        // Set bugList
        this.setBugList(taskDtoResult);
        // Set breadcrumb element list & breadcrumb url list
        this.setBreadcrumb(taskDtoResult, taskEntity.getFeature());

        return taskDtoResult;
    }

    @Override
    public TaskEntity getById(Long id) {
        if (iTaskRepository.checkIsDeleted(id)) throw new CustomHandleException(491);
        return this.repository.findById(id).orElseThrow(() -> new CustomHandleException(251));
    }

    @Override
    public TaskDto add(TaskModel model) {
        // Check task exist by name
        checkTaskExistByName(model.getName(), model.getFeatureId());

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(model.getFeatureId(),listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }

        TaskEntity taskEntity = TaskModel.toEntity(model);

        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        taskEntity.setCreateBy(userEntity);

        // feature save
        taskEntity.setFeature(this.featureRepository.findById(model.getFeatureId()).orElseThrow(() -> new RuntimeException("Feature not exist !!!")));

        taskEntity.setStatus(Const.status.TO_DO.name());
        taskEntity = this.repository.saveAndFlush(taskEntity);

        // add tag
        List<String> tagList = new ArrayList<>();
        if (model.getTagNames() != null && model.getTagNames().size() > 0) {
            for (String tagName : model.getTagNames()) {
                TagEntity tagEntity = this.tagRepository.findByName(tagName);
                if (tagEntity == null) {
                    TagModel newTagModel = TagModel.builder().name(tagName).build();
                    TagDto newTagDto = this.tagService.add(newTagModel);
                    TagRelationModel tagRelationModel = TagRelationModel.builder().idTag(newTagDto.getId()).objectId(taskEntity.getId()).category(Const.tableName.TASK.name()).build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(newTagDto.getName());
                } else {
                    TagRelationModel tagRelationModel = TagRelationModel.builder().idTag(tagEntity.getId()).objectId(taskEntity.getId()).category(Const.tableName.TASK.name()).build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(tagEntity.getName());
                }

            }
        }

        // add dev
        List<UserDto> devList = new ArrayList<>();
        FeatureEntity featureEntity = featureRepository.findById(taskEntity.getFeature().getId()).get();
        Long idProjectEntity = iProjectRepository.findById(featureEntity.getProject().getId()).get().getId();
        if (model.getDevIds() != null && model.getDevIds().size() > 0) {
            for (Long devId : model.getDevIds()) {
                // add dev to task
                UserProjectEntity userProject = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.TASK.name());
                userProject.setObjectId(taskEntity.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity1 = this.userRepository.findById(devId).orElseThrow(() -> new CustomHandleException(11));
                devList.add(UserDto.toDto(userEntity1));

                // add dev to feature
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.FEATURE.name(), featureEntity.getId(), Const.type.TYPE_DEV.name(), devId).size() == 0) {
                    UserProjectEntity userProjectFeature = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.FEATURE.name());
                    userProjectFeature.setObjectId(featureEntity.getId());
                    this.userProjectRepository.saveAndFlush(userProjectFeature);
                }

                // add dev to project
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.PROJECT.name(), idProjectEntity, Const.type.TYPE_DEV.name(), devId).size() == 0) {
                    UserProjectEntity userProjectPro = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.PROJECT.name());
                    userProjectPro.setObjectId(idProjectEntity);
                    this.userProjectRepository.saveAndFlush(userProjectPro);
                }

            }
        }

        // add follower
        List<UserDto> followerList = new ArrayList<>();
        if (model.getFollowerIds() != null && model.getFollowerIds().size() > 0) {
            for (Long followerId : model.getFollowerIds()) {
                UserProjectEntity userProject = this.addFollower(followerId);
                userProject.setObjectId(taskEntity.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity2 = this.userRepository.findById(followerId).orElseThrow(() -> new CustomHandleException(11));
                followerList.add(UserDto.toDto(userEntity2));
            }
        }

        // add viewer
        List<UserDto> viewerList = new ArrayList<>();
        if (model.getViewerIds() != null && model.getViewerIds().size() > 0) {
            for (Long viewerId : model.getViewerIds()) {
                UserProjectEntity userProject = this.addViewer(viewerId);
                userProject.setObjectId(taskEntity.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity3 = this.userRepository.findById(viewerId).orElseThrow(() -> new CustomHandleException(11));
                viewerList.add(UserDto.toDto(userEntity3));
            }
        }

        // save file
//        List<String> fileAfterSave = new ArrayList<>();
        if (model.getFiles() != null) {
            for (MultipartFile file : model.getFiles()) {
                FileModel fileModel = new FileModel();
                fileModel.setFile(file);
                fileModel.setObjectId(taskEntity.getId());
                fileModel.setCategory(Const.tableName.TASK.name());
                FileDto fileAfterSaveZ = fileService.add(fileModel);
//                fileAfterSave.add(fileAfterSaveZ.getLink());
            }
        }

        TaskDto result = TaskDto.toDto(this.repository.saveAndFlush(taskEntity));
//        result.setFiles(fileAfterSave);
        result.setTagName(tagList);
        result.setDevList(devList);
        result.setFollowerList(followerList);
        result.setViewerList(viewerList);
        iHistoryLogService.logCreate(taskEntity.getId(), taskEntity, Const.tableName.TASK, taskEntity.getName());
        return result;
    }

    @Override
    public List<TaskDto> add(List<TaskModel> model) {
        return null;
    }

    @Override
    public TaskDto update(TaskModel model) {
        if (iTaskRepository.checkIsDeleted(model.getId())) throw new CustomHandleException(491);

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByTaskIdInThisProject(model.getId(),listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }

        List<FileEntity> fileOriginalExist = fileRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), model.getId());
        if (model.getFileUrlsKeeping() != null) {
            fileRepository.deleteFileExistInObject(model.getFileUrlsKeeping(), Const.tableName.TASK.name(), model.getId());
        } else {
            fileRepository.deleteAllByCategoryAndObjectId(Const.tableName.TASK.name(), model.getId());
        }

        TaskEntity taskExist = (TaskEntity) Const.copy(this.getById(model.getId()));

        // Check task exist by name
        if (!taskExist.getName().equals(model.getName())){
            checkTaskExistByName(model.getName(), model.getFeatureId());
        }

        List<UserEntity> listUserDevExist = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_DEV.name(), model.getId());
        List<UserEntity> listUserFollowExist = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_FOLLOWER.name(), model.getId());
        List<TagEntity> listTagExist = tagRepository.getAllByObjectIdAndCategory(model.getId(), Const.tableName.TASK.name());

        taskExist.setDevTeam(listUserDevExist);
        taskExist.setFollowerTeam(listUserFollowExist);
        taskExist.setTagList(listTagExist);
        taskExist.setAttachFiles(fileOriginalExist);


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
                    TagRelationModel tagRelationModel = TagRelationModel.builder().idTag(newTagDto.getId()).objectId(taskupdate.getId()).category(Const.tableName.TASK.name()).build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(newTagDto.getName());
                } else {
                    TagRelationModel tagRelationModel = TagRelationModel.builder().idTag(tagEntity.getId()).objectId(taskupdate.getId()).category(Const.tableName.TASK.name()).build();
                    this.tagRelationService.add(tagRelationModel);
                    tagList.add(tagEntity.getName());
                }

            }
        }

        // delete dev old
        List<UserProjectEntity> oldUserProjects = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskOld.getId(), Const.type.TYPE_DEV.name());
        oldUserProjects.stream().forEach(oldUserProject -> this.userProjectRepository.delete(oldUserProject));
        // add dev
        List<UserDto> devList = new ArrayList<>();
        FeatureEntity featureEntity = featureRepository.findById(taskOld.getFeature().getId()).get();
        Long idProjectEntity = iProjectRepository.findById(featureEntity.getProject().getId()).get().getId();
        if (model.getDevIds() != null && model.getDevIds().size() > 0) {
            for (Long devId : model.getDevIds()) {
                UserProjectEntity userProject = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.TASK.name());
                userProject.setObjectId(taskupdate.getId());
                this.userProjectRepository.saveAndFlush(userProject);
                UserEntity userEntity1 = this.userRepository.findById(devId).orElseThrow(() -> new CustomHandleException(11));
                devList.add(UserDto.toDto(userEntity1));

                // add dev to feature
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.FEATURE.name(), featureEntity.getId(), Const.type.TYPE_DEV.name(), devId).size() == 0) {
                    UserProjectEntity userProjectFeature = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.FEATURE.name());
                    userProjectFeature.setObjectId(featureEntity.getId());
                    this.userProjectRepository.saveAndFlush(userProjectFeature);
                }

                // add dev to project
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.PROJECT.name(), idProjectEntity, Const.type.TYPE_DEV.name(), devId).size() == 0) {
                    UserProjectEntity userProjectPro = this.addDev(devId, Const.type.TYPE_DEV.name(), Const.tableName.PROJECT.name());
                    userProjectPro.setObjectId(idProjectEntity);
                    this.userProjectRepository.saveAndFlush(userProjectPro);
                }
            }
        }

        // save file
        List<String> fileAfterSave = new ArrayList<>();
        if (model.getFiles() != null) {
            for (MultipartFile file : model.getFiles()) {
                FileModel fileModel = new FileModel();
                fileModel.setFile(file);
                fileModel.setObjectId(taskupdate.getId());
                fileModel.setCategory(Const.tableName.TASK.name());
                FileDto fileAfterSaveZ = fileService.add(fileModel);
                fileAfterSave.add(fileAfterSaveZ.getLink());
            }
        }
        // add follower
        // delete dev old
        List<UserProjectEntity> oldUserFollowerProject = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskOld.getId(), Const.type.TYPE_FOLLOWER.name());
        if (oldUserFollowerProject.size() != 0){
            oldUserFollowerProject.stream().forEach(oldUserProject -> this.userProjectRepository.delete(oldUserProject));
        }
//        List<UserDto> followerList = new ArrayList<>();
        if (model.getFollowerIds() != null && model.getFollowerIds().size() > 0) {
            for (Long followerId : model.getFollowerIds()) {
                // add follower to task
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.TASK.name(), taskupdate.getId(), Const.type.TYPE_FOLLOWER.name(), followerId).size() == 0) {
                    UserProjectEntity userProjectTask = this.addFollower(followerId);
                    userProjectTask.setObjectId(taskupdate.getId());
                    this.userProjectRepository.saveAndFlush(userProjectTask);
                }
            }
        }

        TaskDto result = TaskDto.toDto(this.repository.saveAndFlush(taskupdate));
//        result.setFiles(fileAfterSave);
        result.setTagName(tagList);
        result.setDevList(devList);

        List<UserEntity> listUserDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_DEV.name(), model.getId());
        List<UserEntity> listUserFollow = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_FOLLOWER.name(), model.getId());
        List<TagEntity> listTag = tagRepository.getAllByObjectIdAndCategory(model.getId(), Const.tableName.TASK.name());
        List<FileEntity> fileOriginal = fileRepository.getByCategoryAndObjectId(Const.tableName.TASK.name(), model.getId());

        taskupdate.setDevTeam(listUserDev);
        taskupdate.setFollowerTeam(listUserFollow);
        taskupdate.setTagList(listTag);
        taskupdate.setAttachFiles(fileOriginal);

        iHistoryLogService.logUpdate(taskupdate.getId(), taskExist, taskupdate, Const.tableName.TASK);

        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            // check the user is on the project's dev list
            Long idUser = SecurityUtils.getCurrentUserId();
            List<String> listType = new ArrayList<>();
            listType.add(Const.type.TYPE_DEV.toString());
            List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByTaskIdInThisProject(id,listType);
            if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
                throw new CustomHandleException(5);
            }
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
    public UserProjectEntity addDev(Long id, String type, String category) {
        // objectId not save yet => be will add task
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        UserProjectEntity userProject = UserProjectEntity.builder().idUser(userEntity.getUserId()).type(type).category(category).build();

        return this.userProjectRepository.saveAndFlush(userProject);
    }

    public UserProjectEntity addFollower(Long id) {
        // objectId not save yet => be will add task
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        UserProjectEntity userProject = UserProjectEntity.builder().idUser(userEntity.getUserId()).type(Const.type.TYPE_FOLLOWER.name()).category(Const.tableName.TASK.name()).build();
        return userProject;
    }

    public UserProjectEntity addViewer(Long id) {
        // objectId not save yet => be will add task
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        UserProjectEntity userProject = UserProjectEntity.builder().idUser(userEntity.getUserId()).type(Const.type.TYPE_VIEWER.name()).category(Const.tableName.TASK.name()).build();
        return this.userProjectRepository.saveAndFlush(userProject);
    }

    @Override
    public boolean changIsDeleteById(Long id) {
        TaskEntity oldTask = this.getById(id);
        oldTask.setIsDeleted(true);
        this.repository.saveAndFlush(oldTask);
//        changeStatusFeature(id);
        iHistoryLogService.logDelete(id, oldTask, Const.tableName.TASK,oldTask.getName());
        return true;
    }

    @Override
    public Page<TaskDto> findByPage(Integer pageIndex, Integer pageSize, TaskModel taskModel) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String sql = "SELECT distinct new cy.dtos.project.TaskDto(task) FROM TaskEntity task ";
        String countSQL = "select count(distinct(task)) from TaskEntity task  ";
        if (taskModel.getTextSearch() != null && taskModel.getTextSearch().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = task.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = task.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE 1=1 ";
        countSQL += " WHERE 1=1 ";
        if (taskModel.getStatus() != null) {
            sql += " AND task.status = :status ";
            countSQL += " AND task.status = :status ";
        }
        if (taskModel.getFeatureId() != null) {
            sql += " AND task.feature.id = :featureId ";
            countSQL += " AND task.feature.id = :featureId ";
        }
        if (taskModel.getStartDate() != null) {
            sql += " AND task.startDate >= :startDate ";
            countSQL += "AND task.startDate >= :startDate ";
        }
        if (taskModel.getEndDate() != null) {
            sql += " AND task.endDate <= :endDate ";
            countSQL += "AND task.endDate <= :endDate ";
        }
        if (taskModel.getTextSearch() != null) {
            if (taskModel.getTextSearch().charAt(0) == '#') {
                sql += " AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'TASK') ";
                countSQL += "AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'TASK') ";
            } else {
                sql += " AND (task.name LIKE :textSearch or task.createBy.fullName LIKE :textSearch ) ";
                countSQL += "AND (task.name LIKE :textSearch or task.createBy.fullName LIKE :textSearch ) ";
            }
        }
        sql += "order by task.createdDate desc";

        Query q = manager.createQuery(sql, TaskDto.class);
        Query qCount = manager.createQuery(countSQL);

        if (taskModel.getStatus() != null) {
            q.setParameter("status", taskModel.getStatus());
            qCount.setParameter("status", taskModel.getStatus());
        }
        if (taskModel.getFeatureId() != null) {
            q.setParameter("featureId", taskModel.getFeatureId());
            qCount.setParameter("featureId", taskModel.getFeatureId());
        }
        if (taskModel.getStartDate() != null) {
            q.setParameter("startDate", taskModel.getStartDate() +"00:00:00");
            qCount.setParameter("startDate", taskModel.getStartDate() +"00:00:00");
        }
        if (taskModel.getEndDate() != null) {
            q.setParameter("endDate", taskModel.getEndDate() +"23:59:59");
            qCount.setParameter("endDate", taskModel.getEndDate() +"23:59:59");
        }
        if (taskModel.getTextSearch() != null) {
            q.setParameter("textSearch", "%" + taskModel.getTextSearch() + "%");
            qCount.setParameter("textSearch", "%" + taskModel.getTextSearch() + "%");
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<TaskDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);
        return result;
    }

    @Override
    public List<TaskDto> searchTask(TaskSearchModel taskSearchModel) {
        String sql = "SELECT distinct new cy.dtos.project.TaskDto(task) FROM TaskEntity task ";
        sql += "inner join UserProjectEntity uspr ON task.id = uspr.objectId ";

        if (taskSearchModel.getName() != null && taskSearchModel.getName().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = task.id inner join TagEntity t on t.id = tr.idTag ";
        }
        if (taskSearchModel.getUserId() != null) {
            sql += " AND uspr.idUser = :userId AND uspr.type = 'TYPE_DEV' AND uspr.category = 'TASK' ";
        }
        if (taskSearchModel.getFeatureId() != null) {
            sql += " AND task.feature.id = :featureId";
        }
        sql += " WHERE 1=1 ";

        if (taskSearchModel.getStartDate() != null && taskSearchModel.getEndDate() != null){
            sql += " AND task.startDate >= :startDate AND task.endDate <= :endDate";
        }else {
            if (taskSearchModel.getStartDate() != null) {
                sql += " AND task.startDate >= :startDate ";
            }
            if (taskSearchModel.getEndDate() != null) {
                sql += " AND task.endDate >= :endDate ";
            }
        }

        if (taskSearchModel.getName() != null) {
            if (taskSearchModel.getName().charAt(0) == '#') {
                sql += " AND (t.name = :textSearch ) AND (tr.category LIKE 'TASK') ";
            } else {
                sql += " AND (task.name LIKE :textSearch ) ";
            }
        }


        sql += " AND task.isDeleted = FALSE";
        Query q = manager.createQuery(sql, TaskDto.class);

        if (taskSearchModel.getUserId() != null) {
            q.setParameter("userId", taskSearchModel.getUserId());
        }
        if (taskSearchModel.getFeatureId() != null) {
            q.setParameter("featureId", taskSearchModel.getFeatureId());
        }
        if (taskSearchModel.getStartDate() != null) {
            q.setParameter("startDate", taskSearchModel.getStartDate());
        }
        if (taskSearchModel.getEndDate() != null) {
            q.setParameter("endDate", taskSearchModel.getEndDate());
        }
        if (taskSearchModel.getName() != null) {
            String textSearch = taskSearchModel.getName();
            if (taskSearchModel.getName().charAt(0) == '#') {
                q.setParameter("textSearch", textSearch);
            } else {
                q.setParameter("textSearch", "%" + textSearch + "%");
            }
        }

        List<TaskDto> queryResult = q.getResultList();
        queryResult.stream().forEach(data -> {
            data.setCountSubtask(iTaskRepository.countSubtask(data.getId()));
            data.setCountSubtaskDone(iTaskRepository.countSubtaskDone(data.getId()));
            List<UserDto> listUserDev= userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_DEV.name(), data.getId()).stream().map(e -> UserDto.toDto(e)).collect(Collectors.toList());

            data.setDevList(listUserDev);
        });
        return queryResult;
    }

    @Override
    public Page<TaskDto> findAllByProjectId(Long id, Pageable pageable) {
        Page<TaskDto> listTask = repository.findAllByProjectId(id, pageable).map(task -> TaskDto.toDto(task));
        listTask.stream().forEach(data -> {
            List<UserDto> listUserReviewer = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.TASK.name(), Const.type.TYPE_REVIEWER.name(), data.getId()).stream().map(e -> UserDto.toDto(e)).collect(Collectors.toList());
            data.setReviewerList(listUserReviewer);
        });
        return listTask;
    }

    public boolean updateStatusTask(Long taskId, SubTaskUpdateModel subTaskUpdateModel) {
        TaskEntity taskEntityExist = iTaskRepository.findById(taskId).orElseThrow(() -> new CustomHandleException(253));

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByTaskIdInThisProject(taskId,listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }

        if (taskEntityExist.getStatus().equals(subTaskUpdateModel.getNewStatus().name())) {
            throw new CustomHandleException(205);
        }

        // If Task have Subtask -> do not change status of task manually
        List<SubTaskEntity> getAllSubTask = subTaskRepository.findByTaskId(taskId);
        if (getAllSubTask.size() > 0) {
            throw new CustomHandleException(252);
        }
        // check only reviewer can change status to done
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.DONE.name())){
            Set<Long> idReviewer = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskId,Const.type.TYPE_REVIEWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
            if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(idReviewer::contains)) {
                throw new CustomHandleException(254);
            }
        }

        // If current status is in review -> delete reviewer
        if(taskEntityExist.getStatus().equals(Const.status.IN_REVIEW.name())){
            for (UserProjectEntity userProjectEntity : userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskId, Const.type.TYPE_REVIEWER.name())) {
                userProjectRepository.deleteByIdNative(userProjectEntity.getId());
            }
        }

        taskEntityExist.setStatus(subTaskUpdateModel.getNewStatus().name());
        TaskEntity saveResult = iTaskRepository.save(taskEntityExist);
        if (saveResult == null) {
            return false;
        }

        // If new status is IN_REVIEW -> add reviewer
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.IN_REVIEW.name())) {
            if (subTaskUpdateModel.getReviewerIdList() == null) {
                throw new CustomHandleException(206);
            }
            // Delete old reviewer
            for (UserProjectEntity userProjectEntity : userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskId, Const.type.TYPE_REVIEWER.name())) {
                userProjectRepository.deleteByIdNative(userProjectEntity.getId());
            }
            for (Long reviewerId : subTaskUpdateModel.getReviewerIdList()) {
                // Check if reviewer user is not existed
                userRepository.findById(reviewerId).orElseThrow(() -> new CustomHandleException(207));
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.TASK.name());
                userProjectEntity.setObjectId(taskId);
                userProjectEntity.setIdUser(reviewerId);
                userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                userProjectRepository.save(userProjectEntity);
            }
        }

        return true;
    }

    public void changeStatusFeature(Long idParent) {
        List<String> allStatus = iTaskRepository.getAllStatusTaskByFeatureId(idParent);
        int countStatus = allStatus.size();
        if (countStatus == 1) {
            featureRepository.updateStatusFeature(idParent, allStatus.get(0));
        } else if (countStatus == 2 && allStatus.stream().anyMatch(Const.status.IN_REVIEW.name()::contains) && allStatus.stream().anyMatch(Const.status.DONE.name()::contains)) {
            featureRepository.updateStatusFeature(idParent, Const.status.IN_REVIEW.name());
        } else if (countStatus != 0){
            featureRepository.updateStatusFeature(idParent, Const.status.IN_PROGRESS.name());
        }
    }

    private void setDevListInProject(TaskDto taskDto) {
        // Get projectId by taskId
        String sqlQueryGetProjectId = "SELECT p.id FROM tbl_projects p INNER JOIN tbl_features f ON p.id = f.project_id INNER JOIN tbl_tasks t ON f.id = t.feature_id WHERE t.id = " + taskDto.getId();
        Query nativeQueryGetProjectId = manager.createNativeQuery(sqlQueryGetProjectId);
        BigInteger projectId = (BigInteger) nativeQueryGetProjectId.getSingleResult();
        if(projectId == null) {
            throw new CustomHandleException(209);
        }else {
            String sqlQueryGetDevList = "SELECT * FROM tbl_user WHERE user_id IN (SELECT user_id FROM tbl_user_projects WHERE object_id = "
                    + projectId + " AND type = '" + Const.type.TYPE_DEV.name() + "'"
                    + " AND category = '" + Const.tableName.PROJECT.name() + "')";
            Query nativeQuery = manager.createNativeQuery(sqlQueryGetDevList, UserEntity.class);
            List<UserEntity> listUserDev = nativeQuery.getResultList();
            List<UserDto> listUserDto = new ArrayList<>();
            for (UserEntity userEntity : listUserDev) {
                listUserDto.add(UserDto.toDto(userEntity));
            }
            taskDto.setDevListInProject(listUserDto);
        }
    }

    private void checkTaskExistByName(String name, Long featureId) {
        // MySQL query is NOT case-sensitive
        String sqlQuery = "SELECT COUNT(*) FROM tbl_tasks WHERE name = '" + name + "' and is_deleted = 0 and feature_id = " +featureId;
        Query nativeQuery = manager.createNativeQuery(sqlQuery);
        BigInteger count = (BigInteger) nativeQuery.getSingleResult();
        if(count.intValue() > 0) {
            throw new CustomHandleException(210);
        }
    }

    private void setBugList(TaskDto taskDto) {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<BugDto> allBugOfTask = bugService.findAllBugOfTask(taskDto.getId(), pageable);
        List<BugDto> listBugDto = allBugOfTask.getContent();
        taskDto.setBugList(listBugDto);
    }

    private void setBreadcrumb(TaskDto taskDto, FeatureEntity featureEntity){
        // Project name / Feature name / Task name
        List<String> breadcrumbElementList = new ArrayList<>();
        ProjectEntity projectEntity = featureEntity.getProject();

        String taskName = "";
        String featureName = "";
        String projectName = "";

        Long taskId = 0L;
        Long featureId = 0L;
        Long projectId = 0L;

        if(taskDto != null){
            taskName = taskDto.getName();
            taskId = taskDto.getId();
        }

        if(featureEntity != null){
            featureName = featureEntity.getName();
            featureId = featureEntity.getId();
        }

        if(projectEntity != null){
            projectName = projectEntity.getName();
            projectId = projectEntity.getId();
        }

        breadcrumbElementList.add(projectName);
        breadcrumbElementList.add(featureName);
        breadcrumbElementList.add(taskName);

        taskDto.setBreadcrumbElementList(breadcrumbElementList);

        List<Long> breadcrumbIdList = new ArrayList<>();
        breadcrumbIdList.add(projectId);
        breadcrumbIdList.add(featureId);
        breadcrumbIdList.add(taskId);

        this.setBreadcrumbUrlList(taskDto, breadcrumbIdList);
    }

    private void setBreadcrumbUrlList(TaskDto taskDto, List<Long> breadcrumbIdList){
        List<String> breadcrumbUrlList = new ArrayList<>();
        String fullProjectUrl = PROJECT_DETAIL_URL + breadcrumbIdList.get(0);
        String fullFeatureUrl = FEATURE_DETAIL_URL + breadcrumbIdList.get(1) + "&projectId=" + breadcrumbIdList.get(0);
        String fullTaskUrl = TASK_DETAIL_URL + breadcrumbIdList.get(2);

        breadcrumbUrlList.add(fullProjectUrl);
        breadcrumbUrlList.add(fullFeatureUrl);
        breadcrumbUrlList.add(fullTaskUrl);

        taskDto.setBreadcrumbUrlList(breadcrumbUrlList);
    }
}
