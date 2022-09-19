package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.TagDto;
import cy.dtos.UserDto;
import cy.dtos.project.SubTaskDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.SubTaskModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.IHistoryLogService;
import cy.services.project.ISubTaskService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    IHistoryLogService iHistoryLogService;

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
        return this.subTaskRepository.findById(id).map(SubTaskDto::toDto).orElse(null);
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

        // Validate file type allow
        // Allowed image and video file
        // Allowed document file: .xlxs (Excel), .docx (Word), .pptx (Powerpoint), .pdf, .xml
        List<MultipartFile> attachFiles = model.getAttachFiles();
        List<FileEntity> fileEntityList = this.validateFileTypeAllowed(attachFiles);

        SubTaskEntity subTaskEntity = new SubTaskEntity();
        subTaskEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
        subTaskEntity.setStartDate(model.getStartDate());
        subTaskEntity.setEndDate(model.getEndDate());
        subTaskEntity.setStatus(Const.status.TO_DO + "");
        subTaskEntity.setName(model.getName());
        subTaskEntity.setDescription(model.getDescription());
        subTaskEntity.setPriority(model.getPriority() + ""); // Default value: MEDIUM
        subTaskEntity.setTask(taskEntityChecked);
        subTaskEntity.setAttachFiles(fileEntityList);
        subTaskEntity.setAssignTo(null); // Default value: null
        SubTaskEntity saveSubTask = this.subTaskRepository.save(subTaskEntity);

        // Save assigned users to UserProject
        this.saveAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Update object id value for file
        this.updateObjectId(fileEntityList, saveSubTask.getId());

        // Split tag list
        List<TagDto> tagListSplit = this.saveTagList(model.getTagList(), saveSubTask.getId());
        SubTaskDto subTaskDto = SubTaskDto.toDto(saveSubTask);
        subTaskDto.setTagList(tagListSplit);
        subTaskDto.setAssignedUser(userEntitiesAssigned.stream().map(UserDto::toDto).collect(Collectors.toList()));

        iHistoryLogService.logCreate(saveSubTask.getId(),saveSubTask, Const.tableName.SUBTASK);
        return subTaskDto;
    }

    @Override
    public SubTaskDto update(SubTaskModel modelUpdate) {
        Optional<SubTaskEntity> subTaskExisted = this.subTaskRepository.findById(modelUpdate.getId());
        SubTaskEntity subTaskEntityOriginal = subTaskExisted.get();
        if (subTaskExisted.isEmpty()) {
            throw new CustomHandleException(197);
        }
        List<Object> objUpdateList = this.checkIdAndDate(modelUpdate);
        List<FileEntity> fileEntityList = new ArrayList<>();
        List<UserEntity> userEntitiesAssigned = (List<UserEntity>) objUpdateList.get(1);

        // Deleted attached file
        if (modelUpdate.getFileNameDeletes() != null && modelUpdate.getFileNameDeletes().size() > 0) {
            this.deleteAttachFile(modelUpdate.getFileNameDeletes(), subTaskExisted.get().getId());
        }

        // Validate file type allow
        // Allowed image and video file
        // Allowed document file: .xlxs (Excel), .docx (Word), .pptx (Powerpoint), .pdf, .xml
        List<MultipartFile> attachFiles = modelUpdate.getAttachFiles();
        if (attachFiles != null && !attachFiles.isEmpty()) {
            fileEntityList = this.validateFileTypeAllowed(attachFiles);
        }

        subTaskExisted.get().setName(modelUpdate.getName());
        subTaskExisted.get().setDescription(modelUpdate.getDescription());
        subTaskExisted.get().setStartDate(modelUpdate.getStartDate());
        subTaskExisted.get().setEndDate(modelUpdate.getEndDate());

        SubTaskEntity saveSubTask = this.subTaskRepository.save(subTaskExisted.get());

        // Update object id value for file
        this.updateObjectId(fileEntityList, saveSubTask.getId());

        // Clear old assigned users in UserProject
        this.clearAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Save assigned users to UserProject
        this.saveAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Clear old tag list
        this.clearTagList(saveSubTask.getId());

        // Split tag list
        List<TagDto> tagListSplit = this.saveTagList(modelUpdate.getTagList(), saveSubTask.getId());

        saveSubTask.setTask(subTaskExisted.get().getTask());
        // Join 2 file lists
        saveSubTask.getAttachFiles().addAll(fileEntityList); // If save attach file, it will be null
        SubTaskDto subTaskDto = SubTaskDto.toDto(saveSubTask);
        subTaskDto.setTagList(tagListSplit);

        iHistoryLogService.logUpdate(saveSubTask.getId(),subTaskEntityOriginal,saveSubTask, Const.tableName.SUBTASK);
        return subTaskDto;
    }

    public void clearTagList(Long subTaskId) {
        this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK + "", subTaskId).forEach(tagRelationEntity -> {
            this.tagRelationRepository.delete(tagRelationEntity);
        });
    }

    public void deleteAttachFile(List<String> fileNameDeletes, Long subTaskId) {
        for (String fileNameDelete : fileNameDeletes) {
            FileEntity fileEntityDeleted = this.fileRepository.findByFileNameAndObjectId(fileNameDelete, subTaskId);
            if (fileEntityDeleted != null) {
                this.fileRepository.delete(fileEntityDeleted);
                //fileUploadProvider.deleteFile(fileEntityDeleted.getLink());
            } else {
                throw new CustomHandleException(198);
            }
        }

    }

    public void clearAssignedUsers(List<UserEntity> userEntitiesAssigned, Long subTaskId) {
        this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK + "",
                subTaskId).forEach(userProjectEntity -> {
            this.userProjectRepository.delete(userProjectEntity);
        });
    }

    public void saveAssignedUsers(List<UserEntity> userEntitiesAssigned, Long subTaskId) {
        for (UserEntity userEntity : userEntitiesAssigned) {
            UserProjectEntity userProjectEntity = new UserProjectEntity();
            userProjectEntity.setObjectId(subTaskId);
            userProjectEntity.setIdUser(userEntity.getUserId());
            userProjectEntity.setType(Const.type.TYPE_DEV + "");
            userProjectEntity.setCategory(Const.tableName.SUBTASK + "");
            this.userProjectRepository.save(userProjectEntity);
        }
    }

    public List<Object> checkIdAndDate(SubTaskModel model) {
        List<Object> objectList = new ArrayList<>();
        List<UserEntity> userEntitiesAssigned = new ArrayList<>();
        Optional<TaskEntity> taskEntityOptional = taskRepository.findById(model.getTaskId());
        if (taskEntityOptional.isEmpty()) {
            throw new CustomHandleException(192);
        }
        objectList.add(taskEntityOptional.get());
        if (model.getEndDate().before(model.getStartDate())) {
            throw new CustomHandleException(193);
        }
        for (Long userId : model.getAssignedUserIdList()) {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new CustomHandleException(194));
            userEntitiesAssigned.add(userEntity);
        }
        objectList.add(userEntitiesAssigned);
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
                fileEntity.setCategory(Const.tableName.SUBTASK + "");
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
            }
            // Then save tag relation
            TagRelationEntity tagRelationEntity = new TagRelationEntity();
            tagRelationEntity.setObjectId(subTaskId);
            tagRelationEntity.setIdTag(tagId);
            tagRelationEntity.setCategory(Const.tableName.SUBTASK + "");
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
        try{
            this.subTaskRepository.findById(id).orElseThrow(() -> new CustomHandleException(163));

            for (UserProjectEntity userProjectEntity : this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.userProjectRepository.delete(userProjectEntity);
            }

            for (TagRelationEntity tagRelationEntity : this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.tagRelationRepository.delete(tagRelationEntity);
            }

            this.subTaskRepository.deleteById(id);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public boolean changIsDeleteById(Long id) {
        Optional<SubTaskEntity> subTaskDeleted = this.subTaskRepository.findById(id);
        if (subTaskDeleted.isEmpty()) {
            throw new CustomHandleException(199);
        } else {
            subTaskDeleted.get().setIsDeleted(true);
            this.subTaskRepository.save(subTaskDeleted.get());
            iHistoryLogService.logDelete(id,subTaskDeleted.get(), Const.tableName.SUBTASK);
        }
        return true;
    }
}
