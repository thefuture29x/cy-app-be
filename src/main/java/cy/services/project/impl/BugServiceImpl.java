package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.UserDto;
import cy.dtos.project.BugDto;
import cy.dtos.project.TagDto;
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
        List<UserDto> reviewerList = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.BUG.name(), Const.type.TYPE_REVIEWER.name(), id);
        List<UserDto> responsibleList = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.BUG.name(), Const.type.TYPE_DEV.name(), id);
        List<TagDto> tagEntityList = new ArrayList<>();
        for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
            TagEntity tagEntity = iTagRepository.findById(tagRelationEntity.getIdTag()).orElse(null);
            tagEntityList.add(TagDto.toDto(tagEntity));
        }
        BugDto bugDto= BugDto.entityToDto(iBugRepository.findById(id).get());
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
                subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                subTaskRepository.saveAndFlush(subTaskEntity);
            }

            if (model.getTask() != null) {
                TaskEntity taskEntity = iTaskRepository.findById(model.getTask()).orElseThrow(() -> new CustomHandleException(251));
                bugEntity.setTask(taskEntity);
                //chuyển trạng thái Task sang fixBug
                taskEntity.setStatus(Const.status.FIX_BUG.name());
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
            iHistoryLogService.logCreate(entity.getId(), entity, Const.tableName.BUG);
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
        try {
            BugEntity bugEntity = iBugRepository.findById(model.getId()).orElse(null);
            BugEntity bugEntityOriginal = (BugEntity) Const.copy(bugEntity);
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
          /*  if(model.getStartDate().before(currentDate)){
                bugEntity.setStatus(Const.status.IN_PROGRESS.name());
            }
            else {
                bugEntity.setStatus(Const.status.TO_DO.name());
            }*/
            bugEntity.setUpdatedDate(currentDate);
            //create reviewer list
            List<UserProjectEntity> reviewerList = new ArrayList<>();
            if (model.getReviewerList() != null && model.getReviewerList().size() >= 0) {
                for (UserProjectModel user : model.getReviewerList()) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setIdUser(user.getIdUser());
                    userProjectEntity.setCategory(Const.tableName.BUG.name());
                    userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                    userProjectEntity.setObjectId(bugEntity.getId());
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
                    userProjectEntity.setObjectId(bugEntity.getId());
                    userProjectRepository.save(userProjectEntity);
                    responsibleList.add(UserProjectModel.toEntity(user));
                }
            }
            bugEntity.setReviewerList(null);
            bugEntity.setResponsibleList(null);

            List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), bugEntity.getId());
            if (tagRelationEntities != null && tagRelationEntities.size() > 0) {
                iTagRelationRepository.deleteByIdNative(tagRelationEntities.get(0).getId());
                iTagRelationRepository.deleteAllInBatch(tagRelationEntities);
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

            if (bugEntity.getAttachFiles() != null && bugEntity.getAttachFiles().size() > 0)
                bugEntity.getAttachFiles().clear();
            else {
                bugEntity.setAttachFiles(new ArrayList<>());
            }
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
            iBugRepository.save(bugEntity);
            iHistoryLogService.logUpdate(bugEntity.getId(), bugEntityOriginal, bugEntity, Const.tableName.BUG);
            return BugDto.entityToDto(bugEntity);
        } catch (Exception e) {
            System.out.println(e);
            return null;
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
                    subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    break;
                case 2:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
                    subTaskEntity.setStatus(Const.status.DONE.name());
                    break;

            }
        }else {
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
        if (bugEntity.getAssignTo().getUserId() == SecurityUtils.getCurrentUserId()) {//dev fix bug mới có thể đổi trạng thái bug để start
            switch (status) {
                case 1:
                    //dev bắt đầu fix bug
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    subTaskRepository.updateStatusSubTask(id, Const.status.FIX_BUG.name());
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
                    break;
                case 3:
                    //reviewer oke xong thì chuyển bug sang done
                    bugEntity.setStatus(Const.status.DONE.name());
                    subTaskRepository.updateStatusSubTaskAfterAllBugDone(bugEntity.getSubTask().getId());
//                    subTaskEntity.setStatus(Const.status.DONE.name());
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
                    taskEntity.setStatus(Const.status.FIX_BUG.name());
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

        Date now = Date.from(Instant.now());
        if (bugEntity.getAssignTo().getUserId() == SecurityUtils.getCurrentUserId()) {//dev fix bug mới có thể đổi trạng thái bug để start
            switch (status) {
                case 1:
                    //dev bắt đầu fix bug
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    iTaskRepository.updateStatusTask(id, Const.status.FIX_BUG.name());
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
                    break;
                case 3:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
                    iTaskRepository.updateStatusTaskAfterAllBugDone(bugEntity.getTask().getId());
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

    @Override
    public Page<BugDto> findAllBugOfProject(Long idProject, Pageable pageable) {
        return iBugRepository.findAllBugOfProject(idProject, pageable).map(data -> BugDto.entityToDto(data));
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
}
