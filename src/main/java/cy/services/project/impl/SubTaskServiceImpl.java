package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.UserDto;
import cy.dtos.project.*;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.SubTaskModel;
import cy.models.project.SubTaskUpdateModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.IHistoryLogService;
import cy.services.project.IRequestBugService;
import cy.services.project.ISubTaskService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubTaskServiceImpl implements ISubTaskService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    ITagRepository tagRepository;
    @Autowired
    ITagRelationRepository tagRelationRepository;
    @Autowired
    ITaskRepository taskRepository;
    @Autowired
    ISubTaskRepository subTaskRepository;
    @Autowired
    IFileRepository fileRepository;
    @Autowired
    IUserProjectRepository userProjectRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IBugRepository bugRepository;
    @Autowired
    IRequestBugService bugService;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SubTaskDto> findAll() {
        return null;
    }

    @Override
    public Page<SubTaskDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<SubTaskDto> findAll(Specification<SubTaskEntity> specs) {
        return null;
    }

    @Override
    public Page<SubTaskDto> filter(Pageable page, Specification<SubTaskEntity> specs) {
        return null;
    }

    @Override
    public SubTaskDto findById(Long id) {
        SubTaskEntity subTaskEntity = this.subTaskRepository.findByIdAndIsDeletedFalse(id);
        SubTaskDto subTaskDto = new SubTaskDto();
        if (subTaskEntity != null) {
            subTaskDto = SubTaskDto.toDto(subTaskEntity);
            // Get tag list
            List<TagRelationEntity> tagRelationEntityList = tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), subTaskDto.getId());
            List<TagDto> tagDtoList = new ArrayList<>();
            // Convert TagRelationEntity to TagDto
            for (TagRelationEntity tagRelationEntity : tagRelationEntityList) {
                TagDto tagDto = tagRepository.findById(tagRelationEntity.getIdTag()).map(TagDto::toDto).orElse(null);
                if (tagDto != null) {
                    tagDtoList.add(tagDto);
                }
            }
            subTaskDto.setTagList(tagDtoList);

            // Get file list
            List<FileEntity> fileEntityList = fileRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), subTaskDto.getId());
            List<FileDto> fileDtoList = new ArrayList<>();
            // Convert Entity to Dto
            for (FileEntity fileEntity : fileEntityList) {
                fileDtoList.add(FileDto.toDto(fileEntity));
            }
            subTaskDto.setAttachFileUrls(fileDtoList);

            // Set bug list
            setBugList(subTaskDto);

            // Set following user list then set watching user list, developer user list and reviewer user list
            setFollowingUserList(subTaskDto);
        }
        return subTaskDto;
    }

    private void setBugList(SubTaskDto subTaskDto) {
        List<BugEntity> bugEntityList = bugRepository.getAllBugBySubTaskId(subTaskDto.getId());
        List<BugDto> bugDtoList = new ArrayList<>();
        // Convert Entity to Dto
        for (BugEntity bugEntity : bugEntityList) {
            bugDtoList.add(new BugDto(bugEntity));
        }
        subTaskDto.setBugList(bugDtoList);
    }

    private void setFollowingUserList(SubTaskDto subTaskDto) {
        Long projectIdBySubTaskId = subTaskRepository.getProjectIdBySubTaskId(subTaskDto.getId());
        List<UserDto> userFollowingDtoList = new ArrayList<>();
        if (subTaskDto.getId() != null) {
            // Collect id of user following
            List<Long> userFollowingIdList = userProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskDto.getId(), Const.type.TYPE_FOLLOWER.name());
            // Find user entity by id
            List<UserEntity> userFollowingEntityList = userRepository.findAllById(userFollowingIdList);
            // Convert Entity to Dto
            for (UserEntity userFollowingEntity : userFollowingEntityList) {
                userFollowingDtoList.add(UserDto.toDto(userFollowingEntity));
            }
        }
        subTaskDto.setFollowingUserList(userFollowingDtoList);

        // Set watching (viewer) user list
        setWatchingUserList(subTaskDto, projectIdBySubTaskId);

        // Set developer user list
        setDeveloperUserList(subTaskDto);

        // Set reviewer user list
        setReviewerUserList(subTaskDto);
    }

    private void setWatchingUserList(SubTaskDto subTaskDto, Long projectIdBySubTaskId) {
        List<UserDto> userWatchingDtoList = new ArrayList<>();
        if (projectIdBySubTaskId != null) {
            // Collect id of user watching (viewer)
            List<Long> userWatchingIdList = userProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectIdBySubTaskId, Const.type.TYPE_VIEWER.name());
            // Find user entity by id
            List<UserEntity> userFollowingEntityList = userRepository.findAllById(userWatchingIdList);
            // Convert Entity to Dto
            for (UserEntity userWatchingEntity : userFollowingEntityList) {
                userWatchingDtoList.add(UserDto.toDto(userWatchingEntity));
            }
        }
        subTaskDto.setWatchingUserList(userWatchingDtoList);
    }

    private void setDeveloperUserList(SubTaskDto subTaskDto) {
        List<UserDto> userAssigningDtoList = new ArrayList<>();
        // Collect id of user assigning
        List<Long> userAssigningIdList = userProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskDto.getId(), Const.type.TYPE_DEV.name());
        // Find user entity by id
        List<UserEntity> userFollowingEntityList = userRepository.findAllById(userAssigningIdList);
        // Convert Entity to Dto
        for (UserEntity userWatchingEntity : userFollowingEntityList) {
            userAssigningDtoList.add(UserDto.toDto(userWatchingEntity));
        }
        subTaskDto.setDeveloperUserList(userAssigningDtoList);
    }

    private void setReviewerUserList(SubTaskDto subTaskDto) {
        List<UserDto> userReviewerDtoList = new ArrayList<>();
        // Collect id of user reviewer
        List<Long> userReviewerIdList = userProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskDto.getId(), Const.type.TYPE_REVIEWER.name());
        // Find user entity by id
        List<UserEntity> userReviewerEntityList = userRepository.findAllById(userReviewerIdList);
        // Convert Entity to Dto
        for (UserEntity userReviewerEntity : userReviewerEntityList) {
            userReviewerDtoList.add(UserDto.toDto(userReviewerEntity));
        }
        subTaskDto.setReviewerUserList(userReviewerDtoList);
    }

    @Override
    public SubTaskEntity getById(Long id) {
        return null;
    }

    @Override
    public SubTaskDto add(SubTaskModel model) {
        List<Object> objectList = this.checkIdAndDate(model);
        TaskEntity taskEntityChecked = (TaskEntity) objectList.get(0);
        List<UserEntity> userEntitiesAssigned = (List<UserEntity>) objectList.get(1);
        boolean isSaveFollowingUser = (boolean) objectList.get(2);

        // Validate file type allow
        // Allowed image and video file
        // Allowed document file: .xlxs (Excel), .docx (Word), .pptx (Powerpoint), .pdf, .xml
        List<MultipartFile> attachFiles = model.getAttachFiles();
        List<FileEntity> fileEntityList = new ArrayList<>();
        if (attachFiles != null && !attachFiles.isEmpty()) {
            fileEntityList = this.validateFileTypeAllowed(attachFiles);
        }

        SubTaskEntity subTaskEntity = new SubTaskEntity();
        subTaskEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
        subTaskEntity.setStartDate(model.getStartDate());
        subTaskEntity.setEndDate(model.getEndDate());
        subTaskEntity.setStatus(Const.status.TO_DO.name());
        subTaskEntity.setName(model.getName());
        subTaskEntity.setDescription(model.getDescription());
        subTaskEntity.setPriority(model.getPriority().name()); // Default value: MEDIUM
        subTaskEntity.setTask(taskEntityChecked);
        subTaskEntity.setAttachFiles(fileEntityList);
        subTaskEntity.setAssignTo(null); // Default value: null
        subTaskEntity.setIsDefault(model.getIsDefault());
        SubTaskEntity saveSubTask = this.subTaskRepository.save(subTaskEntity);

        // Save assigned users to UserProject
        List<UserProjectEntity> userProjectEntityList = this.saveAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Save assigned users for Task -> Feature -> Project
        this.saveAssignedUsersForTask(userEntitiesAssigned, taskEntityChecked.getId());
        this.saveAssignedUsersForFeature(userEntitiesAssigned, taskEntityChecked);
        this.saveAssignedUsersForProject(userEntitiesAssigned, saveSubTask.getId());

        // Save following users to UserProject
        if (isSaveFollowingUser) {
            boolean saveFollowingUsersResult = this.saveFollowingUsers(model.getFollowingUserIdList(), saveSubTask.getId());
            if (!saveFollowingUsersResult) {
                throw new CustomHandleException(203);
            }
        }

        // Update object id value for file
        this.updateObjectId(fileEntityList, saveSubTask.getId());

        // Split tag list
        List<TagDto> tagListSplit = this.saveTagList(model.getTagList(), saveSubTask.getId());
        SubTaskDto subTaskDto = SubTaskDto.toDto(saveSubTask);
        subTaskDto.setTagList(tagListSplit);
        subTaskDto.setAssignedUser(userProjectEntityList.stream().map(u -> UserProjectDto.toDto(u)).collect(Collectors.toList()));

        iHistoryLogService.logCreate(saveSubTask.getId(), saveSubTask, Const.tableName.SUBTASK, saveSubTask.getName());
        return subTaskDto;
    }

    @Override
    public SubTaskDto update(SubTaskModel modelUpdate) {
        SubTaskEntity subTaskExisted = this.subTaskRepository.findByIdAndIsDeletedFalse(modelUpdate.getId());
        List<FileEntity> fileOriginal = fileRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), modelUpdate.getId());

        if (subTaskExisted == null) {
            throw new CustomHandleException(197);
        }

        // Check fileUrlsKeeping
        if (modelUpdate.getFileUrlsKeeping() == null) {
            throw new CustomHandleException(201);
        }

        // Copy current subTask to compare with new subTask
        SubTaskEntity subTaskEntityOriginal = (SubTaskEntity) Const.copy(subTaskExisted);
        List<UserEntity> userDevOriginal = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.SUBTASK.name(), Const.type.TYPE_DEV.name(), modelUpdate.getId());
        List<UserEntity> userFollowOriginal = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.SUBTASK.name(), Const.type.TYPE_FOLLOWER.name(), modelUpdate.getId());
        List<TagEntity> listTagEntityOriginal = tagRepository.getAllByObjectIdAndCategory(modelUpdate.getId(), Const.tableName.SUBTASK.name());

        subTaskEntityOriginal.setDevTeam(userDevOriginal);
        subTaskEntityOriginal.setFollowerTeam(userFollowOriginal);
        subTaskEntityOriginal.setTagList(listTagEntityOriginal);
        subTaskEntityOriginal.setAttachFiles(fileOriginal);


        List<Object> objUpdateList = this.checkIdAndDate(modelUpdate);
        List<FileEntity> fileEntityList = new ArrayList<>();
        List<UserEntity> userEntitiesAssigned = (List<UserEntity>) objUpdateList.get(1);

        // Deleted attached file
//        if (modelUpdate.getFileNameDeletes() != null && modelUpdate.getFileNameDeletes().size() > 0) {
//            this.deleteAttachFile(modelUpdate.getFileNameDeletes(), subTaskExisted.get().getId(), subTaskExisted.get().getAttachFiles());
//        }

        // Delete attach file if it's url do not exist in fileUrlsKeeping
        List<FileEntity> currentAttachedFiles = fileRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), subTaskExisted.getId());
        if (currentAttachedFiles != null && !currentAttachedFiles.isEmpty()) {
            for (FileEntity fileEntity : currentAttachedFiles) {
                if (!modelUpdate.getFileUrlsKeeping().contains(fileEntity.getLink())) {
                    this.fileRepository.deleteByIdNative(fileEntity.getId());
                    subTaskExisted.getAttachFiles().remove(fileEntity);
                }
            }
        }

        // Validate file type allow
        // Allowed image and video file
        // Allowed document file: .xlxs (Excel), .docx (Word), .pptx (Powerpoint), .pdf, .xml
        List<MultipartFile> attachFiles = modelUpdate.getAttachFiles();
        if (attachFiles != null && !attachFiles.isEmpty()) {
            fileEntityList = this.validateFileTypeAllowed(attachFiles);
        }

        subTaskExisted.setName(modelUpdate.getName());
        subTaskExisted.setDescription(modelUpdate.getDescription());
        subTaskExisted.setStartDate(modelUpdate.getStartDate());
        subTaskExisted.setEndDate(modelUpdate.getEndDate());
        subTaskExisted.setPriority(modelUpdate.getPriority().name());

        // Update default sub task
        if (modelUpdate.getIsDefault() != null && modelUpdate.getIsDefault()) {
            unsetDefaultSubTask(subTaskExisted.getTask().getId());
            subTaskExisted.setIsDefault(true);
        }

        SubTaskEntity saveSubTask = this.subTaskRepository.saveAndFlush(subTaskExisted);

        // Update object id value for file
        this.updateObjectId(fileEntityList, saveSubTask.getId());

        // Clear old assigned users in UserProject
        this.clearAssignedUsers(saveSubTask.getId());

        // Save assigned users to UserProject
        List<UserProjectEntity> userProjectEntityList = this.saveAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Add assigned users for Task -> Feature -> Project (NOT clear old assigned users)
        this.saveAssignedUsersForTask(userEntitiesAssigned, subTaskExisted.getTask().getId());
        this.saveAssignedUsersForFeature(userEntitiesAssigned, subTaskExisted.getTask());
        this.saveAssignedUsersForProject(userEntitiesAssigned, saveSubTask.getId());

        // Clear old following users in UserProject
        this.clearFollowingUsers(saveSubTask.getId());

        // Save following users to UserProject
        if (modelUpdate.getFollowingUserIdList() != null && modelUpdate.getFollowingUserIdList().size() > 0) {
            boolean saveFollowingUsersResult = this.saveFollowingUsers(modelUpdate.getFollowingUserIdList(), saveSubTask.getId());
            if (!saveFollowingUsersResult) {
                throw new CustomHandleException(203);
            }
        }

        // Clear old tag list
        this.clearTagList(saveSubTask.getId());

        // Split tag list
        List<TagDto> tagListSplit = this.saveTagList(modelUpdate.getTagList(), saveSubTask.getId());

        saveSubTask.setTask(subTaskExisted.getTask());
        // Join 2 file lists
        saveSubTask.getAttachFiles().addAll(fileEntityList); // If save attach file, it will be null

        SubTaskDto subTaskDto = SubTaskDto.toDto(saveSubTask);
        subTaskDto.setTagList(tagListSplit);

        List<UserEntity> userDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.SUBTASK.name(), Const.type.TYPE_DEV.name(), modelUpdate.getId());
        List<UserEntity> userFollow = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.SUBTASK.name(), Const.type.TYPE_FOLLOWER.name(), modelUpdate.getId());
        List<TagEntity> listTagEntity = tagRepository.getAllByObjectIdAndCategory(modelUpdate.getId(), Const.tableName.SUBTASK.name());
        List<FileEntity> fileAfterSave = fileRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), modelUpdate.getId());

        saveSubTask.setDevTeam(userDev);
        saveSubTask.setFollowerTeam(userFollow);
        saveSubTask.setTagList(listTagEntity);
        saveSubTask.setAttachFiles(fileAfterSave);

//        subTaskDto.setAssignedUser(userProjectEntityList.stream().map(u -> UserProjectDto.toDto(u)).collect(Collectors.toList()));
        iHistoryLogService.logUpdate(saveSubTask.getId(), subTaskEntityOriginal, saveSubTask, Const.tableName.SUBTASK);
        return subTaskDto;
    }

    private void unsetDefaultSubTask(Long taskId) {
        List<SubTaskEntity> subTaskEntityList = subTaskRepository.getByTaskId(taskId);
        if (subTaskEntityList != null && !subTaskEntityList.isEmpty()) {
            for (SubTaskEntity subTaskEntity : subTaskEntityList) {
                if (subTaskEntity.getIsDefault() || subTaskEntity.getIsDefault() == null) {
                    subTaskEntity.setIsDefault(false);
                    subTaskRepository.save(subTaskEntity);
                }
            }
        }
    }

    private void clearTagList(Long subTaskId) {
        this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), subTaskId).forEach(tagRelationEntity -> {
            this.tagRelationRepository.deleteByIdNative(tagRelationEntity.getId());
        });
    }

    public void deleteAttachFile(List<String> fileNameDeletes, Long subTaskId, List<FileEntity> currentFileList) {
        Set<Long> fileIdDeleted = new HashSet<>();
        for (String fileNameDelete : fileNameDeletes) {
            FileEntity fileEntityDeleted = this.fileRepository.findByFileNameAndObjectId(fileNameDelete, subTaskId);
            if (fileEntityDeleted != null) {
                this.fileRepository.deleteByIdNative(fileEntityDeleted.getId());
                fileIdDeleted.add(fileEntityDeleted.getId());
                //fileUploadProvider.deleteFile(fileEntityDeleted.getLink());
            } else {
                throw new CustomHandleException(198);
            }
        }

        // Remove deleted file from current file list
        currentFileList.removeIf(fileEntity -> fileIdDeleted.contains(fileEntity.getId()));
    }

    private void clearAssignedUsers(Long subTaskId) {
        this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskId, Const.type.TYPE_DEV.name()).forEach(userProject -> {
            userProjectRepository.deleteByIdNative(userProject.getId());
        });
    }

    private void saveAssignedUsersForProject(List<UserEntity> userEntitiesAssigned, Long subTaskId) {
        Long projectId = subTaskRepository.getProjectIdBySubTaskId(subTaskId);
        for (UserEntity userEntity : userEntitiesAssigned) {
            if (userProjectRepository.getByAllAttrs(Const.tableName.PROJECT.name(), userEntity.getUserId(), projectId, Const.type.TYPE_DEV.name()).size() > 0) {
                continue;
            }
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setObjectId(projectId);
            userProjectEntity.setIdUser(userEntity.getUserId());
            userProjectEntity.setType(Const.type.TYPE_DEV.name());
            userProjectEntity.setCategory(Const.tableName.PROJECT.name());
            this.userProjectRepository.save(userProjectEntity);
        }
    }

    private void saveAssignedUsersForFeature(List<UserEntity> userEntitiesAssigned, TaskEntity taskEntity) {
        if (taskEntity.getFeature() != null) {
            Long featureId = taskEntity.getFeature().getId();
            for (UserEntity userEntity : userEntitiesAssigned) {
                if (userProjectRepository.getByAllAttrs(Const.tableName.FEATURE.name(), userEntity.getUserId(), featureId, Const.type.TYPE_DEV.name()).size() > 0) {
                    continue;
                }
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setObjectId(featureId);
                userProjectEntity.setIdUser(userEntity.getUserId());
                userProjectEntity.setType(Const.type.TYPE_DEV.name());
                userProjectEntity.setCategory(Const.tableName.FEATURE.name());
                this.userProjectRepository.save(userProjectEntity);
            }
        }
    }

    private void saveAssignedUsersForTask(List<UserEntity> userEntitiesAssigned, Long taskId) {
        // taskEntity always not null
        for (UserEntity userEntity : userEntitiesAssigned) {
            if (userProjectRepository.getByAllAttrs(Const.tableName.TASK.name(), userEntity.getUserId(), taskId, Const.type.TYPE_DEV.name()).size() > 0) {
                continue;
            }
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setObjectId(taskId);
            userProjectEntity.setIdUser(userEntity.getUserId());
            userProjectEntity.setType(Const.type.TYPE_DEV.name());
            userProjectEntity.setCategory(Const.tableName.TASK.name());
            this.userProjectRepository.save(userProjectEntity);
        }
    }

    private List<UserProjectEntity> saveAssignedUsers(List<UserEntity> userEntitiesAssigned, Long subTaskId) {
        List<UserProjectEntity> userProjectEntityList = new ArrayList<>();
        for (UserEntity userEntity : userEntitiesAssigned) {
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setObjectId(subTaskId);
            userProjectEntity.setIdUser(userEntity.getUserId());
            userProjectEntity.setType(Const.type.TYPE_DEV.name());
            userProjectEntity.setCategory(Const.tableName.SUBTASK.name());
            UserProjectEntity userProjectEntitySave = this.userProjectRepository.save(userProjectEntity);
            userProjectEntityList.add(userProjectEntitySave);
        }
        return userProjectEntityList;
    }

    private void clearFollowingUsers(Long subTaskId) {
        this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskId, Const.type.TYPE_FOLLOWER.name()).forEach(userProject -> {
            userProjectRepository.deleteByIdNative(userProject.getId());
        });
    }

    private boolean saveFollowingUsers(List<Long> userIdList, Long subTaskId) {
        for (Long userId : userIdList) {
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setObjectId(subTaskId);
            userProjectEntity.setIdUser(userId);
            userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
            userProjectEntity.setCategory(Const.tableName.SUBTASK.name());
            UserProjectEntity userProjectEntitySave = this.userProjectRepository.save(userProjectEntity);
            if (userProjectEntitySave == null) {
                return false;
            }
        }
        return true;
    }

    private List<Object> checkIdAndDate(SubTaskModel model) {
        List<Object> objectList = new ArrayList<>();
        List<UserEntity> userEntitiesAssigned = new ArrayList<>();
        TaskEntity taskEntityExisted = taskRepository.findByIdAndIsDeletedFalse(model.getTaskId());
        // If task do not exist -> throw exception
        if (taskEntityExisted == null) {
            throw new CustomHandleException(192);
        }
        objectList.add(taskEntityExisted);

        // End date can not be earlier than start date
        if (model.getEndDate().before(model.getStartDate())) {
            throw new CustomHandleException(193);
        }

        // Check assigned user id list
        for (Long userId : model.getAssignedUserIdList()) {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new CustomHandleException(194));
            userEntitiesAssigned.add(userEntity);
        }
        objectList.add(userEntitiesAssigned);

        // Check follow user id list
        if (model.getFollowingUserIdList() != null) {
            for (Long userId : model.getFollowingUserIdList()) {
                boolean isUserEntityExist = userRepository.existsById(userId);
                if (!isUserEntityExist) {
                    throw new CustomHandleException(202);
                }
            }
            objectList.add(true);
        } else {
            objectList.add(false);
        }
        return objectList;
    }

    public List<FileEntity> validateFileTypeAllowed(List<MultipartFile> fileList) {
        if (fileList == null || fileList.size() == 0) {
            return null;
        }
        List<FileEntity> fileEntityList = new ArrayList<>();
        // Allowed image and video file
        // Allowed document file: .xlxs, .docx, .pptx, .pdf, .xml
        for (MultipartFile file : fileList) {
            String fileName = file.getOriginalFilename();
            String fileType = "N/A";
            if (fileName != null) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            if (file.getContentType().equalsIgnoreCase("video/*") && !file.getContentType().equalsIgnoreCase("image/*") && !fileType.equalsIgnoreCase("xlxs") && !fileType.equalsIgnoreCase("docx") && !fileType.equalsIgnoreCase("pptx") && !fileType.equalsIgnoreCase("pdf") && !fileType.equalsIgnoreCase("xml")) {
                throw new CustomHandleException(195);
            } else {
                FileEntity fileEntity = new FileEntity();
                try {
                    fileEntity.setLink(fileUploadProvider.uploadFile(Const.tableName.SUBTASK + "/", file));
                } catch (Exception e) {
                    throw new CustomHandleException(196);
                }
                fileEntity.setFileType(fileType);
                fileEntity.setFileName(fileName + "_" + System.currentTimeMillis());
                fileEntity.setCategory(Const.tableName.SUBTASK.name());
                fileEntity.setUploadedBy(SecurityUtils.getCurrentUser().getUser());
                fileEntityList.add(fileEntity);
            }
        }
        return fileEntityList;
    }

    public List<TagDto> saveTagList(String tagList, Long subTaskId) {
        List<TagDto> tagDtoList = new ArrayList<>();
        List<String> tagListSplit = Arrays.stream(tagList.split(",")).collect(Collectors.toList());
        Long tagId = null;
        for (String tag : tagListSplit) {
            if (tag.length() == 0) {
                continue;
            }
            // Tag must be unique
            TagEntity isTagExist = tagRepository.findByName(tag);
            // If tag is not exist, create new tag
            if (isTagExist == null) {
                TagEntity tagEntity = new TagEntity();
                tagEntity.setName(tag);
                TagEntity tagEntitySaved = tagRepository.save(tagEntity);
                tagDtoList.add(TagDto.toDto(tagEntitySaved));
                tagId = tagEntitySaved.getId();
            } else {
                tagId = isTagExist.getId();
                tagDtoList.add(TagDto.toDto(isTagExist));
            }
            // Then save tag relation
            TagRelationEntity tagRelationEntity = new TagRelationEntity();
            tagRelationEntity.setObjectId(subTaskId);
            tagRelationEntity.setIdTag(tagId);
            tagRelationEntity.setCategory(Const.tableName.SUBTASK.name());
            tagRelationRepository.save(tagRelationEntity);
        }
        return tagDtoList;
    }

    public void updateObjectId(List<FileEntity> fileEntityList, Long objectId) {
        if (objectId != null) {
            for (FileEntity fileEntity : fileEntityList) {
                fileEntity.setObjectId(objectId);
                fileRepository.save(fileEntity);
            }
        }
    }

    @Override
    public List<SubTaskDto> add(List<SubTaskModel> model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            SubTaskEntity sb = this.subTaskRepository.findById(id).orElseThrow(() -> new CustomHandleException(163));

            // Delete bug
            this.bugRepository.getAllBugBySubTaskId(id).forEach(bugEntity -> this.bugService.deleteBug(bugEntity.getId()));

            for (UserProjectEntity userProjectEntity : this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.userProjectRepository.delete(userProjectEntity);
            }

            for (TagRelationEntity tagRelationEntity : this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.tagRelationRepository.delete(tagRelationEntity);
            }

            // Delete file
            fileRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id).stream().forEach(fileEntity -> this.fileRepository.deleteById(fileEntity.getId()));

            this.subTaskRepository.delete(sb);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public boolean softDeleteById(Long id) {
        SubTaskEntity subTaskDeleting = this.subTaskRepository.findByIdAndIsDeletedFalse(id);
        if (subTaskDeleting == null) {
            throw new CustomHandleException(199);
        } else {
            subTaskDeleting.setIsDeleted(true);
            // Unset default sub task (if it is default sub task)
            subTaskDeleting.setIsDefault(false);
            this.subTaskRepository.save(subTaskDeleting);
            iHistoryLogService.logDelete(id, subTaskDeleting, Const.tableName.SUBTASK);
        }
        return true;
    }

    @Override
    public Page<SubTaskDto> findAllByProjectId(Long id, Pageable pageable) {
        return subTaskRepository.findAllByProjectId(id, pageable).map(data -> SubTaskDto.toDto(data));
    }

    @Override
    public Page<SubTaskDto> findAllByTaskId(Long id, String keyword, Pageable pageable) {
        Page<SubTaskDto> result = new PageImpl<>(new ArrayList<>());
        Page<SubTaskEntity> findAllSubTask = subTaskRepository.findByTaskIdWithPaging(id, keyword, pageable);
        for(SubTaskEntity subTaskEntity : findAllSubTask) {
            SubTaskDto subTaskDto = SubTaskDto.toDto(subTaskEntity);
            setReviewerUserList(subTaskDto);
        }
        return result;
    }

    @Override
    public Page<SubTaskDto> filter(SubTaskModel subTaskModel, Pageable pageable) {
        if (subTaskModel.getTaskId() == null) {
            throw new CustomHandleException(200);
        }
        Long taskId = subTaskModel.getTaskId();
        Date startDate = subTaskModel.getStartDate();
        Date endDate = subTaskModel.getEndDate();
        String status = subTaskModel.getStatus() != null ? subTaskModel.getStatus().name() : null;
        String priority = subTaskModel.getPriority() != null ? subTaskModel.getPriority().name() : null;
        TaskEntity isTaskExist = taskRepository.findByIdAndIsDeletedFalse(taskId);
        if (isTaskExist == null) {
            throw new CustomHandleException(192);
        }

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Create sql query
        String sql = "SELECT * FROM tbl_sub_tasks WHERE task_id = " + taskId;
        if (startDate != null) {
            sql += " AND start_date >= '" + sdf.format(startDate) + "'";
        }
        if (endDate != null) {
            sql += " AND end_date <= '" + sdf.format(endDate) + "'";
        }
        if (status != null) {
            sql += " AND status = '" + status + "'";
        }
        if (priority != null) {
            sql += " AND priority = '" + priority + "'";
        }

        sql += " AND is_deleted = false";

        sql += " ORDER BY created_date DESC";

        // Paging
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        sql += " LIMIT " + start + ", " + size;

        // Execute query
        Query nativeQuery = entityManager.createNativeQuery(sql, SubTaskEntity.class);

        // Get result
        List<SubTaskEntity> subTaskEntityList = nativeQuery.getResultList();

        // Convert to dto
        List<SubTaskDto> subTaskDtoList = new ArrayList<>();
        for (SubTaskEntity subTaskEntity : subTaskEntityList) {
            subTaskDtoList.add(SubTaskDto.toDto(subTaskEntity));
        }
        return new PageImpl<>(subTaskDtoList, pageable, subTaskDtoList.size());
    }

    @Override
    public boolean changeStatus(Long subTaskId, SubTaskUpdateModel subTaskUpdateModel) {
        SubTaskEntity subTaskEntityExist = subTaskRepository.findById(subTaskId).orElseThrow(() -> new CustomHandleException(204));
        if (subTaskEntityExist.getStatus().equals(subTaskUpdateModel.getNewStatus().name())) {
            throw new CustomHandleException(205);
        }
        subTaskEntityExist.setStatus(subTaskUpdateModel.getNewStatus().name());
        SubTaskEntity saveResult = subTaskRepository.save(subTaskEntityExist);
        if (saveResult == null) {
            return false;
        }
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.IN_REVIEW.name())) {
            if (subTaskUpdateModel.getReviewerIdList() == null) {
                throw new CustomHandleException(206);
            }
            // Delete old reviewer
            for (UserProjectEntity userProjectEntity : userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.SUBTASK.name(), subTaskId, Const.type.TYPE_REVIEWER.name())) {
                userProjectRepository.deleteByIdNative(userProjectEntity.getId());
            }
            for (Long reviewerId : subTaskUpdateModel.getReviewerIdList()) {
                // Check if reviewer is existed
                userRepository.findById(reviewerId).orElseThrow(() -> new CustomHandleException(207));
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.SUBTASK.name());
                userProjectEntity.setObjectId(subTaskId);
                userProjectEntity.setIdUser(reviewerId);
                userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                userProjectRepository.save(userProjectEntity);
            }
        }

        // Update status of task
        updateStatusOfTask(subTaskEntityExist.getTask());
        return true;
    }

    private void updateStatusOfTask(TaskEntity taskEntity) {
        if (taskEntity == null) {
            throw new CustomHandleException(208);
        }
        List<String> getAllStatusOfSubTask = subTaskRepository.getAllStatusSubTaskByTaskId(taskEntity.getId());
        int countStatus = getAllStatusOfSubTask.size();
        if (countStatus == 1) {
            // All sub-task have same status
            taskEntity.setStatus(getAllStatusOfSubTask.get(0));
        } else if (countStatus == 2 && getAllStatusOfSubTask.stream().anyMatch(Const.status.DONE.name()::contains) && getAllStatusOfSubTask.stream().anyMatch(Const.status.IN_REVIEW.name()::contains)) {
            // All sub-task have status DONE OR IN_REVIEW
            taskEntity.setStatus(Const.status.IN_REVIEW.name());
        } else if(countStatus != 0){
            taskEntity.setStatus(Const.status.IN_PROGRESS.name());
        }
        taskRepository.save(taskEntity);
    }
}
