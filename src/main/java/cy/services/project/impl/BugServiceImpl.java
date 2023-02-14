package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.AllBugDto;
import cy.dtos.project.BugDto;
import cy.dtos.project.TagDto;
import cy.dtos.project.UserMetaDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.BugModel;
import cy.models.project.TagModel;
import cy.models.project.UserProjectModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.IFileService;
import cy.services.project.IHistoryLogService;
import cy.services.project.IRequestBugService;
import cy.services.project.ITagService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
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
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BugServiceImpl implements IRequestBugService {
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IBugHistoryRepository iBugHistoryRepository;
    @Autowired
    ISubTaskRepository subTaskRepository;
    @Autowired
    IBugRepository iBugRepository;
    @Autowired
    ITagService iTagService;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    Date now = Date.from(Instant.now());
    @Autowired
    IUserProjectRepository userProjectRepository;
    @Autowired
    ITaskRepository iTaskRepository;

    @Override
    public List<BugDto> findAll() {
        return null;
    }

    @Override
    public Page<BugDto> findAll(Pageable page) {
        return iBugRepository.findAll(page).map(data -> BugDto.entityToDto(data));
    }

    @Override
    public List<BugDto> findAll(Specification<BugEntity> specs) {
        return null;
    }

    @Override
    public Page<BugDto> filter(Pageable page, Specification<BugEntity> specs) {
        return null;
    }

    @Override
    public BugDto findById(Long id) {
        List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), id);

        List<UserMetaDto> reviewerList = showListUserInBug(Const.type.TYPE_REVIEWER.name(), id);// userRepository.getByCategoryAndTypeAndObjectIdUserMetaDto(Const.tableName.BUG.name(), Const.type.TYPE_REVIEWER.name(), id);
        List<UserMetaDto> responsibleList = showListUserInBug(Const.type.TYPE_DEV.name(), id); //userRepository.getByCategoryAndTypeAndObjectIdUserMetaDto(Const.tableName.BUG.name(), Const.type.TYPE_DEV.name(), id);
        List<TagDto> tagEntityList = new ArrayList<>();
        for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
            TagEntity tagEntity = iTagRepository.findById(tagRelationEntity.getIdTag()).orElse(null);
            tagEntityList.add(TagDto.toDto(tagEntity));
        }
        BugDto bugDto = BugDto.entityToDto(iBugRepository.findById(id).get());
        bugDto.setReviewerList(reviewerList);
        bugDto.setResponsibleList(responsibleList);
        bugDto.setTagList(tagEntityList);
        return bugDto;
    }

    @Override
    public BugEntity getById(Long id) {
        return null;
    }

    public void saveDataInHistoryTable(Long bugEntity, Date startDate, Date endDate, List<FileEntity> files) {
        BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
        bugHistoryEntity.setBugId(bugEntity);
        bugHistoryEntity.setStartDate(startDate);
        bugHistoryEntity.setEndDate(endDate);
        bugHistoryEntity.setAttachFiles(files);
        iBugHistoryRepository.saveAndFlush(bugHistoryEntity);
    }

    @Override
    public BugDto add(BugModel model) {
        Date now = Date.from(Instant.now());
        try {
            BugEntity bugEntity = model.modelToEntity(model);
            bugEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
            bugEntity.setIsDeleted(false);
            //  if (bugEntity.getStartDate().compareTo(bugEntity.getCreatedDate()) != 0) {
            //nếu ngày tạo bug không phải ngày bắt đầu
            bugEntity.setStatus(Const.status.TO_DO.name());
          /*  }else if(bugEntity.getStartDate().compareTo(bugEntity.getCreatedDate()) == 0){
                //nếu ngày bắt đầu cũng là ngày tạo bug
                bugEntity.setStatus(Const.status.IN_PROGRESS.name());
            }*/
            if (model.getSubTask() != null) {
                SubTaskEntity subTaskEntity = subTaskRepository.findById(model.getSubTask()).orElseThrow(() -> new CustomHandleException(281));
                bugEntity.setSubTask(subTaskEntity);
//                chuyển trạng thái Subtask sang fixBug
                subTaskEntity.setStatus(Const.status.IN_PROGRESS.name());
                subTaskRepository.saveAndFlush(subTaskEntity);
            }

            if (model.getTask() != null) {
                TaskEntity taskEntity = iTaskRepository.findById(model.getTask()).orElseThrow(() -> new CustomHandleException(251));
                bugEntity.setTask(taskEntity);
                //chuyển trạng thái Task sang fixBug
                taskEntity.setStatus(Const.status.IN_PROGRESS.name());
                iTaskRepository.saveAndFlush(taskEntity);
            }

            bugEntity.setAssignTo(userRepository.findById(model.getUserAssign()).orElseThrow(() -> new CustomHandleException(11)));
            BugEntity entity = iBugRepository.saveAndFlush(bugEntity);


            //create file
            if (model.getFiles() != null && model.getFiles().length > 0) {
                for (MultipartFile m : model.getFiles()) {
                    if (!m.isEmpty()) {
                        String urlFile = null;
                        try {
                            urlFile = fileUploadProvider.uploadFile("bug", m);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        FileEntity fileEntity = new FileEntity();
                        String fileName = m.getOriginalFilename();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                        fileEntity.setCategory(Const.tableName.BUG.name());
                        fileEntity.setUploadedBy(SecurityUtils.getCurrentUser().getUser());
                        fileEntity.setObjectId(entity.getId());
//                        fileEntity.setCreatedDate(now);
                        iFileRepository.save(fileEntity);
                    }
                }
            }
            //create tag
            List<TagEntity> tagEntityList = new ArrayList<>();
            if (model.getTags() != null && model.getTags().size() > 0) {
                for (TagModel tagModel : model.getTags()) {
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if (tagEntity == null) {
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
                        tagEntity1 = iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(entity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    } else if (tagEntity != null) {
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(entity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                    tagEntityList.add(TagModel.toEntity(tagModel));
                }
            }
            //create reviewer list
            List<UserProjectEntity> reviewerList = new ArrayList<>();
            if (model.getReviewerList() != null && model.getReviewerList().size() >= 0) {
                for (UserProjectModel user : model.getReviewerList()) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setIdUser(user.getIdUser());
                    userProjectEntity.setCategory(Const.tableName.BUG.name());
                    userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                    userProjectEntity.setObjectId(entity.getId());
                    userProjectRepository.save(userProjectEntity);
                    reviewerList.add(UserProjectModel.toEntity(user));
                }
            }
            //create responsible list
            List<UserProjectEntity> responsibleList = new ArrayList<>();
            if (model.getResponsibleList() != null && model.getResponsibleList().size() >= 0) {
                for (UserProjectModel user : model.getResponsibleList()) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setIdUser(user.getIdUser());
                    userProjectEntity.setCategory(Const.tableName.BUG.name());
                    userProjectEntity.setType(Const.type.TYPE_DEV.name());
                    userProjectEntity.setObjectId(entity.getId());
                    userProjectRepository.save(userProjectEntity);
                    responsibleList.add(UserProjectModel.toEntity(user));
                }
            }
            bugEntity.setReviewerList(null);
            bugEntity.setResponsibleList(null);
            bugEntity.setTagList(tagEntityList);
            BugDto bugDto = BugDto.entityToDto(bugEntity);
            iHistoryLogService.logCreate(entity.getId(), entity, Const.tableName.BUG, entity.getName());
            return bugDto;
        } catch (Exception e) {
            throw new CustomHandleException(312);
        }
    }

    @Override
    public List<BugDto> add(List<BugModel> model) {
        return null;
    }

    @Override
    public BugDto update(BugModel model) {
        BugEntity bug = iBugRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11));
        List<FileEntity> fileOriginal = iFileRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), model.getId());

        if (bug.getCreateBy().getUserId() == SecurityUtils.getCurrentUserId()) {//người tạo bug mới có thể đổi trạng thái
            try {
                if (model.getFileUrlsKeeping() != null) {
                    iFileRepository.deleteFileExistInObject(model.getFileUrlsKeeping(), Const.tableName.BUG.name(), model.getId());
                } else {
                    iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.BUG.name(), model.getId());
                }
                BugEntity bugEntity = iBugRepository.findById(model.getId()).orElse(null);

                BugEntity bugEntityOriginal = (BugEntity) Const.copy(bugEntity);

                List<UserEntity> userDevOriginal = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.BUG.name(), Const.type.TYPE_DEV.name(), model.getId());
                List<UserEntity> userViewOriginal = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.BUG.name(), Const.type.TYPE_REVIEWER.name(), model.getId());
                List<TagEntity> listTagEntityOriginal = iTagRepository.getAllByObjectIdAndCategory(model.getId(),Const.tableName.BUG.name());

                bugEntityOriginal.setReviewerList(userViewOriginal);
                bugEntityOriginal.setResponsibleList(userDevOriginal);
                bugEntityOriginal.setTagList(listTagEntityOriginal);
                bugEntityOriginal.setAttachFiles(fileOriginal);

                if (bugEntity == null)
                    return null;
                Long userId = SecurityUtils.getCurrentUserId();
                if (userId == null)
                    return null;
                UserEntity userEntity = userRepository.findById(userId).orElse(null);
                bugEntity.setCreateBy(userEntity);
                Date currentDate = new Date();
                bugEntity.setCreatedDate(currentDate);
                bugEntity.setStartDate(model.getStartDate());
                bugEntity.setEndDate(model.getEndDate());
                bugEntity.setDescription(model.getDescription());
                bugEntity.setName(model.getNameBug());
                bugEntity.setIsDefault(model.getIsDefault());
                bugEntity.setReason(model.getReason());
                if (model.getSubTask() != null) {
                    SubTaskEntity subTaskEntity = subTaskRepository.findById(model.getSubTask()).orElseThrow(() -> new CustomHandleException(281));
                    bugEntity.setSubTask(subTaskEntity);
//                chuyển trạng thái Subtask sang IN_PROGRESS
                    subTaskEntity.setStatus(Const.status.IN_PROGRESS.name());
                    subTaskRepository.saveAndFlush(subTaskEntity);
                }

                if (model.getTask() != null) {
                    TaskEntity taskEntity = iTaskRepository.findById(model.getTask()).orElseThrow(() -> new CustomHandleException(251));
                    bugEntity.setTask(taskEntity);
                    //chuyển trạng thái Task sang IN_PROGRESS
                    taskEntity.setStatus(Const.status.IN_PROGRESS.name());
                    iTaskRepository.saveAndFlush(taskEntity);
                }
                bugEntity.setUpdatedDate(currentDate);
                //clear data in table relation user project
                List<UserProjectEntity> listReviewOld = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), model.getId(), Const.type.TYPE_REVIEWER.name());
                for (UserProjectEntity user : listReviewOld) {
                    userProjectRepository.deleteById(user.getId());
                    userProjectRepository.flush();
                }
                //create reviewer list
                if (model.getReviewerList() != null && model.getReviewerList().size() >= 0) {
                    for (UserProjectModel user : model.getReviewerList()) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setIdUser(user.getIdUser());
                        userProjectEntity.setCategory(Const.tableName.BUG.name());
                        userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                        userProjectEntity.setObjectId(bugEntity.getId());
                        userProjectRepository.save(userProjectEntity);
                    }
                }
                //clear data in table relation user project
                List<UserProjectEntity> listDevOld = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), model.getId(), Const.type.TYPE_DEV.name());
                for (UserProjectEntity user : listDevOld) {
                    userProjectRepository.deleteById(user.getId());
                    userProjectRepository.flush();
                }
                //create responsible list
                if (model.getResponsibleList() != null && model.getResponsibleList().size() >= 0) {
                    for (UserProjectModel user : model.getResponsibleList()) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setIdUser(user.getIdUser());
                        userProjectEntity.setCategory(Const.tableName.BUG.name());
                        userProjectEntity.setType(Const.type.TYPE_DEV.name());
                        userProjectEntity.setObjectId(bugEntity.getId());
                        userProjectRepository.save(userProjectEntity);
                    }
                }


                List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), model.getId());
                System.out.println(tagRelationEntities);
                //delete tag in tag_relations table
                if (tagRelationEntities != null && tagRelationEntities.size() > 0) {
                    for (TagRelationEntity tagRelation : tagRelationEntities) {
                        iTagRelationRepository.deleteById(tagRelation.getId());
                         iTagRelationRepository.deleteAllInBatch(tagRelationEntities);
                    }
                }
                if (model.getTags() != null && model.getTags().size() > 0) {
                    for (TagModel tagModel : model.getTags()) {
                        TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                        if (tagEntity == null) {
                            TagEntity tagEntity1 = new TagEntity();
                            tagEntity1.setName(tagModel.getName());
                            tagEntity1 = iTagRepository.save(tagEntity1);
                            TagRelationEntity tagRelationEntity = new TagRelationEntity();
                            tagRelationEntity.setCategory(Const.tableName.BUG.name());
                            tagRelationEntity.setIdTag(tagEntity1.getId());
                            tagRelationEntity.setObjectId(bugEntity.getId());
                            iTagRelationRepository.save(tagRelationEntity);
                        } else if (tagEntity != null) {
                            TagRelationEntity tagRelationEntity = new TagRelationEntity();
                            tagRelationEntity.setCategory(Const.tableName.BUG.name());
                            tagRelationEntity.setIdTag(tagEntity.getId());
                            tagRelationEntity.setObjectId(bugEntity.getId());
                            iTagRelationRepository.save(tagRelationEntity);
                        }
                    }
                }

//                bugEntity.getAttachFiles().clear();
                if (model.getFiles() != null && model.getFiles().length > 0) {
                    for (MultipartFile m : model.getFiles()) {
                        if (!m.isEmpty()) {
                            String urlFile = fileUploadProvider.uploadFile("bug", m);

                            FileEntity fileEntity = new FileEntity();
                            String fileName = m.getOriginalFilename();
                            fileEntity.setLink(urlFile);
                            fileEntity.setFileName(fileName);
                            fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                            fileEntity.setCategory(Const.tableName.BUG.name());
                            fileEntity.setUploadedBy(userEntity);
                            fileEntity.setObjectId(bugEntity.getId());
                            iFileRepository.saveAndFlush(fileEntity);
                            bugEntity.getAttachFiles().add(fileEntity);
                        }
                    }
                }

                bugEntity.setReviewerList(null);
                bugEntity.setResponsibleList(null);
                bugEntity.setTagList(null);
                bugEntity.setReason(null);
                iBugRepository.saveAndFlush(bugEntity);

                List<UserEntity> userDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.BUG.name(), Const.type.TYPE_DEV.name(), model.getId());
                List<UserEntity> userView = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.BUG.name(), Const.type.TYPE_REVIEWER.name(), model.getId());
                List<TagEntity> listTagEntity = iTagRepository.getAllByObjectIdAndCategory(model.getId(),Const.tableName.BUG.name());
                List<FileEntity> file = iFileRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), model.getId());

                bugEntity.setReviewerList(userView);
                bugEntity.setResponsibleList(userDev);
                bugEntity.setTagList(listTagEntity);
                bugEntity.setAttachFiles(file);
                iHistoryLogService.logUpdate(bugEntity.getId(), bugEntityOriginal, bugEntity, Const.tableName.BUG);
                return BugDto.entityToDto(bugEntity);
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        } else {
            throw new CustomHandleException(311);
        }

    }

    public BugDto deleteOnlyFile(Long idFile, Long idProject) {
        BugEntity bugEntity = iBugRepository.findById(idProject).orElseThrow(() -> new CustomHandleException(11));
        if (bugEntity.getCreateBy().getUserId() == SecurityUtils.getCurrentUserId()) {//người tạo bug mới có thể đổi trạng thái
            try {
                FileEntity file = iFileRepository.findById(idFile).get();
                file.setObjectId(null);
                iFileRepository.saveAndFlush(file);
                BugEntity newBug = iBugRepository.findById(idProject).get();
                return BugDto.entityToDto(newBug);
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        } else {
            throw new CustomHandleException(311);
        }
    }

    /*
     *@author:HieuMM_Cy
     *@since:9/16/2022-8:44 AM
     *@description:Bug Done
     *@update:
     **/
    @Override
    public BugDto updateStatusBugToSubTask(Long id, int status) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        SubTaskEntity subTaskEntity = subTaskRepository.findById(bugEntity.getSubTask().getId()).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask
        if (bugEntity.getCreateBy().getUserId() == SecurityUtils.getCurrentUserId()) {//người tạo bug mới có thể đổi trạng thái
            switch (status) {
                case 1:
                    //reviewer fail xong thì
                    subTaskEntity.setStatus(Const.status.IN_PROGRESS.name());
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    break;
                case 2:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
                    subTaskEntity.setStatus(Const.status.DONE.name());
                    break;

            }
        } else {
            throw new CustomHandleException(311);
        }

        subTaskRepository.saveAndFlush(subTaskEntity);
        iBugRepository.saveAndFlush(bugEntity);


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }

    @Override
    public BugDto updateStatusSubTaskToBug(Long id, int status) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(313));
        SubTaskEntity subTaskEntity = subTaskRepository.findById(bugEntity.getSubTask().getId()).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask

        Date now = Date.from(Instant.now());
        Set<Long> reviewerIdList = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), bugEntity.getId(), Const.type.TYPE_REVIEWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        Set<Long> responsibleIdList = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), bugEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        if (reviewerIdList.contains(SecurityUtils.getCurrentUserId()) == true || responsibleIdList.contains(SecurityUtils.getCurrentUserId()) == true) {//dev fix bug mới có thể đổi trạng thái bug để start
            switch (status) {
                case 1:
                    //dev bắt đầu fix bug
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    subTaskRepository.updateStatusSubTask(id, Const.status.IN_PROGRESS.name());
//                    subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                    List<FileEntity> files = bugEntity.getAttachFiles().stream().map(f -> {
                        return FileEntity.builder()
//                                .id(f.getId())
                                .fileName(f.getFileName())
                                .fileType(f.getFileType())
                                .link(f.getLink())
                                .uploadedBy(f.getUploadedBy())
                                .category("BUG_HISTORY")
                                .build();
                    }).collect(Collectors.toList());
                    iBugRepository.flush();
                    saveDataInHistoryTable(bugEntity.getId(), now, null, files);
                    changeStatusSubTask(bugEntity.getTask().getId());
                    break;
                case 2:
                    //dev kết thúc fix bug
                    subTaskRepository.updateStatusSubTask(id, Const.status.IN_REVIEW.name());
                    bugEntity.setStatus(Const.status.IN_REVIEW.name());
                    List<BugHistoryEntity> bugHistoryEntities = iBugHistoryRepository.findAllByBugId(bugEntity.getId());
                    for (BugHistoryEntity bugHistoryEntity : bugHistoryEntities) {
                        if (bugHistoryEntity.getEndDate() == null) {
                            bugHistoryEntity.setEndDate(now);
                            iBugHistoryRepository.save(bugHistoryEntity);
                        }
                    }
                    changeStatusSubTask(bugEntity.getTask().getId());
                    break;
                case 3:
                    //reviewer oke xong thì chuyển bug sang done
                    bugEntity.setStatus(Const.status.DONE.name());
//                    subTaskRepository.updateStatusSubTaskAfterAllBugDone(bugEntity.getSubTask().getId());
//                    subTaskEntity.setStatus(Const.status.DONE.name());
                    changeStatusSubTask(bugEntity.getTask().getId());
                    break;

                case 4:
                    // chuyển trạng thái sang pending
                    bugEntity.setStatus(Const.status.PENDING.name());
                    BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
                    bugHistoryEntity.setStartDate(Date.from(Instant.now()));
                    bugHistoryEntity.setIsPending(true);
                    iBugHistoryRepository.saveAndFlush(bugHistoryEntity);
                    changeStatusSubTask(bugEntity.getTask().getId());
                    break;

            }
        } else {
            throw new CustomHandleException(311);
        }

        iBugRepository.save(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }

    @Override
    public BugDto updateStatusBugToTask(Long id, int status) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(313));
        TaskEntity taskEntity = iTaskRepository.findById(bugEntity.getTask().getId()).orElseThrow(() -> new CustomHandleException(251));
        //chuyển trạng thái Subtask
        if (bugEntity.getCreateBy().getUserId() == SecurityUtils.getCurrentUserId()) {//người tạo bug mới có thể đổi trạng thái
            switch (status) {
                case 1:
                    //reviewer fail xong thì
                    taskEntity.setStatus(Const.status.IN_PROGRESS.name());
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    break;
                case 2:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
                    taskEntity.setStatus(Const.status.DONE.name());
                    break;

            }
        }

        iTaskRepository.saveAndFlush(taskEntity);
        iBugRepository.saveAndFlush(bugEntity);


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }

    @Override
    public BugDto updateStatusTaskToBug(Long id, int status) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(313));
        TaskEntity taskEntity = iTaskRepository.findById(bugEntity.getTask().getId()).orElseThrow(() -> new CustomHandleException(251));
        //chuyển trạng thái Subtask
        Set<Long> reviewerIdList = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), bugEntity.getId(), Const.type.TYPE_REVIEWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        Set<Long> responsibleIdList = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.BUG.name(), bugEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());

        Date now = Date.from(Instant.now());
        if (reviewerIdList.contains(SecurityUtils.getCurrentUserId()) == true || responsibleIdList.contains(SecurityUtils.getCurrentUserId()) == true) {//dev fix bug mới có thể đổi trạng thái bug để start
            switch (status) {
                case 1:
                    //dev bắt đầu fix bug
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    iTaskRepository.updateStatusTask(id, Const.status.IN_PROGRESS.name());
//                    subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                    List<FileEntity> files = bugEntity.getAttachFiles().stream().map(f -> {
                        return FileEntity.builder()
//                                .id(f.getId())
                                .fileName(f.getFileName())
                                .fileType(f.getFileType())
                                .link(f.getLink())
                                .uploadedBy(f.getUploadedBy())
                                .category("BUG_HISTORY")
                                .build();
                    }).collect(Collectors.toList());
                    iBugRepository.flush();
                    saveDataInHistoryTable(bugEntity.getId(), now, null, files);
                    changeStatusTask(bugEntity.getTask().getId());
                    break;
                case 2:
                    //dev kết thúc fix bug
                    iTaskRepository.updateStatusTask(id, Const.status.IN_REVIEW.name());
                    bugEntity.setStatus(Const.status.IN_REVIEW.name());
                    List<BugHistoryEntity> bugHistoryEntities = iBugHistoryRepository.findAllByBugId(bugEntity.getId());
                    for (BugHistoryEntity bugHistoryEntity : bugHistoryEntities) {
                        if (bugHistoryEntity.getEndDate() == null) {
                            bugHistoryEntity.setEndDate(now);
                            iBugHistoryRepository.save(bugHistoryEntity);
                        }
                    }
                    changeStatusTask(bugEntity.getTask().getId());
                    break;
                case 3:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
//                    iTaskRepository.updateStatusTaskAfterAllBugDone(bugEntity.getTask().getId());
                    changeStatusTask(bugEntity.getTask().getId());
//                    taskEntity.setStatus(Const.status.DONE.name());
                    break;

            }
        } else {
            throw new CustomHandleException(311);
        }

        iBugRepository.save(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }

    @Autowired
    IProjectRepository iProjectRepository;
    @Autowired
    IFeatureRepository iFeatureRepository;
    @Autowired
    ISubTaskRepository iSubTaskRepository;

    @Override
    public AllBugDto getAllBug(Long idProject) {
        AllBugDto projectBugDto = new AllBugDto();
        List<AllBugDto> allBugDtos = new ArrayList<>();
        for (FeatureEntity feature : iFeatureRepository.findByProjectId(idProject)) {
            AllBugDto featureBugDto = new AllBugDto();
            featureBugDto.setIdObject(feature.getId());
            featureBugDto.setName(feature.getName());
            featureBugDto.setCategory(Const.tableName.FEATURE.name());

            List<TaskEntity> taskEntityList = iTaskRepository.findByFeatureId(feature.getId());
            List<AllBugDto> listFeatureDto = new ArrayList<>();
            for (TaskEntity task : taskEntityList) {
                AllBugDto taskBugDto = new AllBugDto();
                taskBugDto.setIdObject(task.getId());
                taskBugDto.setName(task.getName());
                taskBugDto.setCategory(Const.tableName.TASK.name());

                List<SubTaskEntity> subTaskEntityList = iSubTaskRepository.getByTaskId(task.getId());
                List<AllBugDto> listTaskDto = new ArrayList<>();
                for (SubTaskEntity subTaskEntity : subTaskEntityList) {
                    AllBugDto subTaskBugDto = new AllBugDto();
                    subTaskBugDto.setIdObject(subTaskEntity.getId());
                    subTaskBugDto.setName(subTaskEntity.getName());
                    subTaskBugDto.setCategory(Const.tableName.SUBTASK.name());
                    subTaskBugDto.setCountBug(iBugRepository.countAllBySubTask_IdAndIsDeleted(subTaskEntity.getId(), false));
                    listTaskDto.add(subTaskBugDto);
                }
                taskBugDto.setCountBug(iBugRepository.countAllBugOfTaskByTaskId(task.getId()));
                taskBugDto.setChildDto(listTaskDto);
                listFeatureDto.add(taskBugDto);
            }
            featureBugDto.setCountBug(iBugRepository.countAllBugOfFeatureByFeatureId(feature.getId()));
            featureBugDto.setChildDto(listFeatureDto);
            allBugDtos.add(featureBugDto);
        }
        projectBugDto.setIdObject(idProject);
        projectBugDto.setCategory(Const.tableName.PROJECT.name());
        projectBugDto.setChildDto(allBugDtos);
        projectBugDto.setCountBug(iBugRepository.countAllBugOfProjectByProjectId(idProject));
        return projectBugDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            BugEntity bugEntity = iBugRepository.findById(id).get();
            bugEntity.setIsDeleted(true);
            iBugRepository.save(bugEntity);
            iHistoryLogService.logDelete(id, bugEntity, Const.tableName.BUG);
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
    public void deleteBug(Long id) {

    }

    public List<UserMetaDto> showListUserInBug(String type, Long id) {
        List<UserMetaDto> userList = userRepository.getByCategoryAndTypeAndObjectIdUserMetaDto(Const.tableName.BUG.name(), type, id);
        return userList;
    }

    @Override
    public Page<BugDto> findAllBugOfProject(Long idProject, Pageable pageable) {
        return iBugRepository.findAllBugOfProject(idProject, pageable).map(data -> BugDto.entityToDtoInProject(data, showListUserInBug(Const.type.TYPE_DEV.name(), data.getId()), showListUserInBug(Const.type.TYPE_REVIEWER.name(), data.getId())));
    }

    public Page<BugDto> findAllBugOfFeature(Long id, Pageable pageable) {
        return iBugRepository.findAllBugOfFeature(id, pageable).map(data -> BugDto.entityToDtoInProject(data, showListUserInBug(Const.type.TYPE_DEV.name(), data.getId()), showListUserInBug(Const.type.TYPE_REVIEWER.name(), data.getId())));
    }

    public Page<BugDto> findAllBugOfTask(Long id, Pageable pageable) {
        return iBugRepository.findAllByTaskId(id, pageable).map(data -> BugDto.entityToDtoInProject(data, showListUserInBug(Const.type.TYPE_DEV.name(), data.getId()), showListUserInBug(Const.type.TYPE_REVIEWER.name(), data.getId())));
    }

    public Page<BugDto> findAllBugOfSubTask(Long id, Pageable pageable) {
        return iBugRepository.findAllBySubTaskId(id, pageable).map(data -> BugDto.entityToDtoInProject(data, showListUserInBug(Const.type.TYPE_DEV.name(), data.getId()), showListUserInBug(Const.type.TYPE_REVIEWER.name(), data.getId())));
    }

    @Autowired
    EntityManager manager;

    public Page<BugDto> findByPage(Integer pageIndex, Integer pageSize, BugModel bugModel) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String sql = "SELECT distinct new cy.dtos.project.BugDto(p) FROM BugEntity p";
        String countSQL = "select count(distinct(p)) from BugEntity p  ";
        if (bugModel.getTextSearch() != null && bugModel.getTextSearch().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE 1=1 ";
        countSQL += " WHERE 1=1 ";
        if (bugModel.getStatus() != null) {
            sql += " AND p.status = :status ";
            countSQL += " AND p.status = :status ";
        }
        if (bugModel.getSubTask() != null) {
            sql += " AND p.subTask.id = :subtaskId ";
            countSQL += " AND p.subTask.id = :subtaskId ";
        }
        if (bugModel.getStartDate() != null) {
            sql += " AND p.startDate >= :startDate ";
            countSQL += "AND p.startDate >= :startDate ";
        }
        if (bugModel.getEndDate() != null) {
            sql += " AND p.endDate <= :endDate ";
            countSQL += "AND p.endDate <= :endDate ";
        }
        if (bugModel.getTextSearch() != null) {
            if (bugModel.getTextSearch().charAt(0) == '#') {
                sql += " AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'BUG') ";
                countSQL += "AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'BUG') ";
            } else {
                sql += " AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
                countSQL += "AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
            }
        }
        sql += "order by p.createdDate desc";

        Query q = manager.createQuery(sql, BugDto.class);
        Query qCount = manager.createQuery(countSQL);

        if (bugModel.getStatus() != null) {
            q.setParameter("status", bugModel.getStatus());
            qCount.setParameter("status", bugModel.getStatus());
        }
        if (bugModel.getSubTask() != null) {
            q.setParameter("subtaskId", bugModel.getSubTask());
            qCount.setParameter("subtaskId", bugModel.getSubTask());
        }
        if (bugModel.getStartDate() != null) {
            q.setParameter("startDate", bugModel.getStartDate());
            qCount.setParameter("startDate", bugModel.getStartDate());
        }
        if (bugModel.getEndDate() != null) {
            q.setParameter("endDate", bugModel.getEndDate());
            qCount.setParameter("endDate", bugModel.getEndDate());
        }
        if (bugModel.getTextSearch() != null) {
            q.setParameter("textSearch", "%" + bugModel.getTextSearch() + "%");
            qCount.setParameter("textSearch", "%" + bugModel.getTextSearch() + "%");
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);


        Long numberResult = (Long) qCount.getSingleResult();
        Page<BugDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);
        return result;
    }

    public void changeStatusTask(Long idParent){
        List<String> allStatus = iBugRepository.getAllStatusBugByTaskId(idParent);
        int countStatus = allStatus.size();
        if (countStatus == 1){
            iTaskRepository.updateStatusTask(idParent,allStatus.get(0));
            return;
        }else if (countStatus == 2
                && allStatus.stream().anyMatch(Const.status.IN_REVIEW.name()::contains)
                && allStatus.stream().anyMatch(Const.status.DONE.name()::contains)){
            iTaskRepository.updateStatusTask(idParent,Const.status.IN_REVIEW.name());
            return;
        }else if(countStatus != 0){
            iTaskRepository.updateStatusTask(idParent,Const.status.IN_PROGRESS.name());
            return;
        }
    }

    public void changeStatusSubTask(Long idParent){
        List<String> allStatus = iBugRepository.getAllStatusBugBySubTaskId(idParent);
        int countStatus = allStatus.size();
        if (countStatus == 1){
            iSubTaskRepository.updateStatusSubTask(idParent,allStatus.get(0));
            return;
        }else if (countStatus == 2
                && allStatus.stream().anyMatch(Const.status.IN_REVIEW.name()::contains)
                && allStatus.stream().anyMatch(Const.status.DONE.name()::contains)){
            iSubTaskRepository.updateStatusSubTask(idParent,Const.status.IN_REVIEW.name());
            return;
        }else if (countStatus != 0){
            iSubTaskRepository.updateStatusSubTask(idParent,Const.status.IN_PROGRESS.name());
            return;
        }
    }

}
