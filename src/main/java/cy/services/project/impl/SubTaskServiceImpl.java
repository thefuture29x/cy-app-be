package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.TagDto;
import cy.dtos.UserDto;
import cy.dtos.project.SubTaskDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.SubTaskModel;
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

    @Autowired
    EntityManager entityManager;

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
        SubTaskEntity subTaskEntity = this.subTaskRepository.findById(id).orElseThrow(() -> new CustomHandleException(197));
        SubTaskDto subTaskDto = SubTaskDto.toDto(subTaskEntity);
        this.getTagListAndDevTeam(subTaskDto);
        return subTaskDto;
    }

    public void getTagListAndDevTeam(SubTaskDto subTaskDto) {
        List<Long> tagIds = this.tagRelationRepository.getAllTagIds(Const.tableName.SUBTASK + "",
                subTaskDto.getId());
        List<TagDto> tagDtos = new ArrayList<>();
        for (Long id : tagIds) {
            this.tagRepository.findById(id).ifPresent(tagEntity -> tagDtos.add(TagDto.toDto(tagEntity)));
        }
        subTaskDto.setTagList(tagDtos);

        List<Long> devIds = this.userProjectRepository.getAllDevIds(Const.tableName.SUBTASK + "",
                subTaskDto.getId());
        List<UserDto> assignedUsers = new ArrayList<>();
        for(Long id : devIds) {
            this.userRepository.findById(id).ifPresent(userEntity -> assignedUsers.add(UserDto.toDto(userEntity)));
        }
        subTaskDto.setAssignedUser(assignedUsers);
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
        List<MultipartFile> attachFiles = model.getAttachFiles() != null ? model.getAttachFiles() : new ArrayList<>();
        List<FileEntity> fileEntityList = new ArrayList<>();
        if(!attachFiles.isEmpty() && attachFiles.get(0).getOriginalFilename() != ""){
            fileEntityList = this.validateFileTypeAllowed(attachFiles);
        }

        SubTaskEntity subTaskEntity = new SubTaskEntity();
        subTaskEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
        subTaskEntity.setStartDate(model.getStartDate());
        subTaskEntity.setEndDate(model.getEndDate());
        if (model.getStartDate().before(new Date())) {
            subTaskEntity.setStatus(Const.status.IN_PROGRESS + "");
        } else {
            subTaskEntity.setStatus(Const.status.TO_DO + "");
        }
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
        iHistoryLogService.logCreate(saveSubTask.getId(), saveSubTask, Const.tableName.SUBTASK);
        return subTaskDto;
    }

    @Override
    public SubTaskDto update(SubTaskModel modelUpdate) {
        SubTaskEntity subTaskExisted = this.subTaskRepository.findById(modelUpdate.getId()).orElseThrow(() -> new CustomHandleException(197));
        SubTaskEntity subTaskEntityOriginal = (SubTaskEntity) Const.copy(subTaskExisted);

        List<Object> objUpdateList = this.checkIdAndDate(modelUpdate);
        List<FileEntity> fileEntityList = new ArrayList<>();
        List<UserEntity> userEntitiesAssigned = (List<UserEntity>) objUpdateList.get(1);

        // Deleted attached file
        if (modelUpdate.getFileNameDeletes() != null && modelUpdate.getFileNameDeletes().size() > 0) {
            this.deleteAttachFile(modelUpdate.getFileNameDeletes(), subTaskExisted.getId());
        }

        // Validate file type allow
        // Allowed image and video file
        // Allowed document file: .xlxs (Excel), .docx (Word), .pptx (Powerpoint), .pdf, .xml
        List<MultipartFile> attachFiles = modelUpdate.getAttachFiles() != null ? modelUpdate.getAttachFiles() :
                new ArrayList<>();
        if (!attachFiles.isEmpty() && attachFiles.get(0).getOriginalFilename() != "") {
            fileEntityList = this.validateFileTypeAllowed(attachFiles);
        }

        subTaskExisted.setName(modelUpdate.getName());
        subTaskExisted.setDescription(modelUpdate.getDescription());
        subTaskExisted.setStartDate(modelUpdate.getStartDate());
        subTaskExisted.setEndDate(modelUpdate.getEndDate());

        SubTaskEntity saveSubTask = this.subTaskRepository.save(subTaskExisted);

        // Update object id value for file
        this.updateObjectId(fileEntityList, saveSubTask.getId());

        // Clear old assigned users in UserProject
        this.clearAssignedUsers(saveSubTask.getId());

        // Save assigned users to UserProject
        this.saveAssignedUsers(userEntitiesAssigned, saveSubTask.getId());

        // Clear old tag list
        this.clearTagList(saveSubTask.getId());

        // Split tag list
        List<TagDto> tagListSplit = this.saveTagList(modelUpdate.getTagList(), saveSubTask.getId());

        saveSubTask.setTask(subTaskExisted.getTask());
        // Join 2 file lists
        saveSubTask.getAttachFiles().addAll(fileEntityList); // If save attach file, it will be null
        SubTaskDto subTaskDto = SubTaskDto.toDto(saveSubTask);
        //subTaskDto.setTagList(tagListSplit);
        this.getTagListAndDevTeam(subTaskDto);
        iHistoryLogService.logUpdate(saveSubTask.getId(),subTaskEntityOriginal,saveSubTask, Const.tableName.SUBTASK);
        return subTaskDto;
    }

    public void clearTagList(Long subTaskId) {
        this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK + "", subTaskId).forEach(tagRelationEntity -> {
            this.tagRelationRepository.deleteByIdNative(tagRelationEntity.getId());
        });
    }

    public void deleteAttachFile(List<String> fileNameDeletes, Long subTaskId) {
        for (String fileNameDelete : fileNameDeletes) {
            FileEntity fileEntityDeleted = this.fileRepository.findByFileNameAndObjectId(fileNameDelete, subTaskId);
            if (fileEntityDeleted != null) {
                this.fileRepository.deleteByIdNative(fileEntityDeleted.getId());
                //fileUploadProvider.deleteFile(fileEntityDeleted.getLink());
            } else {
                throw new CustomHandleException(198);
            }
        }

    }

    public void clearAssignedUsers(Long subTaskId) {
        List<UserProjectEntity> oldAssignedUsers = this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK + "", subTaskId);
        for (UserProjectEntity oldUserProject : oldAssignedUsers) {
            this.userProjectRepository.deleteByIdNative(oldUserProject.getId());
        }
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
            this.subTaskRepository.findById(id).orElseThrow(() -> new CustomHandleException(163));
        try{
            SubTaskEntity sb = this.subTaskRepository.findById(id).orElseThrow(() -> new CustomHandleException(163));

            // delete bug
            this.bugRepository.getAllBugBySubTaskId(id).forEach(bugEntity -> this.bugService.deleteBug(bugEntity.getId()));

            for (UserProjectEntity userProjectEntity : this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.userProjectRepository.delete(userProjectEntity);
            }

            for (TagRelationEntity tagRelationEntity : this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.SUBTASK.name(), id)) {
                this.tagRelationRepository.delete(tagRelationEntity);
            }

            // delete file
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
    public boolean version2DeleteById(Long id) {
        Optional<SubTaskEntity> subTaskDeleted = this.subTaskRepository.findById(id);
        if (subTaskDeleted.isEmpty()) {
            throw new CustomHandleException(199);
        } else {
            subTaskDeleted.get().setIsDeleted(true);
            this.subTaskRepository.save(subTaskDeleted.get());
            iHistoryLogService.logDelete(id, subTaskDeleted.get(), Const.tableName.SUBTASK);
        }
        return true;
    }

    @Override
    public Page<SubTaskDto> searchAndFilter(SubTaskModel subTaskModel, Pageable pageable) {
        String sqlQuery = "SELECT DISTINCT NEW cy.dtos.project.SubTaskDto(st) FROM SubTaskEntity st";
        StringBuilder sb = new StringBuilder(sqlQuery);

        // Search with hashtag
        if (subTaskModel.getKeyword() != null && subTaskModel.getKeyword().charAt(0) == '#') {
            sb.append(" INNER JOIN TagRelationEntity tr ON st.id = tr.objectId INNER JOIN TagEntity t ON tr.idTag = t.id");
        }

        sb.append(" WHERE 1 = 1");

        if(subTaskModel.getId() != null) {
            sb.append(" AND st.id = :id");
        }

        if(subTaskModel.getName() != null) {
            sb.append(" AND st.name LIKE :name");
        }

        if(subTaskModel.getTaskId() != null) {
            sb.append(" AND st.task.id = :taskId");
        }

        if(subTaskModel.getDescription() != null) {
            sb.append(" AND st.description LIKE :description");
        }

        if(subTaskModel.getPriority() != null) {
            sb.append(" AND st.priority = :priority");
        }

        if(subTaskModel.getStartDate() != null) {
            sb.append(" AND st.startDate >= :startDate");
        }

        if(subTaskModel.getEndDate() != null) {
            sb.append(" AND st.endDate <= :endDate");
        }

//        if(subTaskModel.getAssignedUserIdList() != null && subTaskModel.getAssignedUserIdList().size() > 0){
//            sb.append(" AND st.assignedUserId IN :assignedUserIdList ");
//        }

        if(subTaskModel.getKeyword() != null) {
            if (subTaskModel.getKeyword().charAt(0) == '#') {
                sb.append(" AND t.name LIKE :keyword AND tr.category = 'SUBTASK'");
            } else {
                sb.append(" AND (st.createBy.fullName LIKE :keyword)");
            }
        }

        sb.append(" ORDER BY st.updatedDate DESC");

        Query query = entityManager.createQuery(sb.toString(), SubTaskDto.class);

        if(subTaskModel.getId() != null) {
            query.setParameter("id", subTaskModel.getId());
        }

        if(subTaskModel.getName() != null) {
            query.setParameter("name", "%" + subTaskModel.getName() + "%");
        }

        if(subTaskModel.getTaskId() != null) {
            query.setParameter("taskId", subTaskModel.getTaskId());
        }

        if(subTaskModel.getDescription() != null) {
            query.setParameter("description", "%" + subTaskModel.getDescription() + "%");
        }

        if(subTaskModel.getPriority() != null) {
            query.setParameter("priority", subTaskModel.getPriority().name());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(subTaskModel.getStartDate() != null) {
            query.setParameter("startDate", subTaskModel.getStartDate());
        }

        if(subTaskModel.getEndDate() != null) {
            query.setParameter("endDate", subTaskModel.getEndDate());
        }

        if(subTaskModel.getKeyword() != null) {
            query.setParameter("keyword", "%" + subTaskModel.getKeyword() + "%");
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List resultList = query.getResultList();

        for(Object obj : resultList) {
            SubTaskDto subTaskDto = (SubTaskDto) obj;
            this.getTagListForSearch(subTaskDto);
            this.getAssignedUserForSearch(subTaskDto);
        }

        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    public void getTagListForSearch(SubTaskDto subTaskDto){
        String sqlQuery = "SELECT DISTINCT NEW cy.dtos.project.TagDto(t) FROM TagEntity t" +
                " INNER JOIN TagRelationEntity tr ON t.id = tr.idTag WHERE tr.category = 'SUBTASK' AND tr.objectId = :objectId";
        List<TagDto> resultList = entityManager.createQuery(sqlQuery, TagDto.class)
                .setParameter("objectId", subTaskDto.getId())
                .getResultList();
        if(resultList != null && resultList.size() > 0){
            resultList.forEach(tagDto -> subTaskDto.getTagList().add(tagDto));
        }
    }

    public void getAssignedUserForSearch(SubTaskDto subTaskDto){
        String sqlQuery = "SELECT DISTINCT NEW cy.dtos.UserDto(u) FROM UserEntity u" +
                " INNER JOIN UserProjectEntity up ON u.userId = up.idUser WHERE up.category = 'SUBTASK' AND up.objectId = :objectId";
        List<UserDto> resultList = entityManager.createQuery(sqlQuery, UserDto.class)
                .setParameter("objectId", subTaskDto.getId())
                .getResultList();
        if(resultList != null && resultList.size() > 0){
            resultList.forEach(userDto -> subTaskDto.getAssignedUser().add(userDto));
        }
    }
}
