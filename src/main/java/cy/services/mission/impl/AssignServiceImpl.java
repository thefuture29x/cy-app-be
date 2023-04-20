package cy.services.mission.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.common.FileDto;
import cy.dtos.common.UserDto;
import cy.dtos.mission.AssignCheckListDto;
import cy.dtos.mission.AssignDto;
import cy.dtos.mission.MissionDto;
import cy.entities.common.*;
import cy.entities.mission.AssignCheckListEntity;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import cy.entities.project.SubTaskEntity;
import cy.entities.project.TaskEntity;
import cy.models.mission.AssignCheckListModel;
import cy.models.mission.AssignModel;
import cy.models.mission.MissionModel;
import cy.models.project.SubTaskUpdateModel;
import cy.models.project.UserViewProjectModel;
import cy.repositories.common.*;
import cy.repositories.mission.IAssignCheckListRepository;
import cy.repositories.mission.IAssignRepository;
import cy.repositories.mission.IMissionRepository;
import cy.services.common.IFileService;
import cy.services.common.IHistoryLogService;
import cy.services.common.IUserService;
import cy.services.mission.IAssignService;
import cy.services.mission.IMissionService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignServiceImpl implements IAssignService {

    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    IAssignRepository iAssignRepository;
    @Autowired
    IUserProjectRepository iUserProjectRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Autowired
    IAssignCheckListRepository iAssignCheckListRepository;
    @Autowired
    IMissionRepository iMissionRepository;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    EntityManager manager;

    @Override
    public AssignDto findById(Long id) {
        if (iAssignRepository.checkIsDeleted(id)) throw new CustomHandleException(491);
        AssignEntity assignEntity = this.iAssignRepository.findById(id).orElse(null);
        AssignDto assignDto = AssignDto.toDto(iAssignRepository.findById(id).orElse(null));
        List<UserDto> userDev = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_DEV.name(), assignEntity.getId());
        List<UserDto> userFollow = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_FOLLOWER.name(), assignEntity.getId());
        assignDto.setUserDevs(userDev);
        assignDto.setUserFollows(userFollow);
        assignDto.setAttachFiles(iFileRepository.findByCategoryAndObjectId(Const.tableName.ASSIGNMENT.name(),id).stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()));
        assignDto.setTagArray(iTagRelationRepository.getNameTagByCategoryAndObjectId(Const.tableName.ASSIGNMENT.name(), assignEntity.getId()));
        assignDto.setAssignCheckListDtos(iAssignCheckListRepository.findAllByAssign_Id(id).stream().map(data -> AssignCheckListDto.toDto(data)).collect(Collectors.toList()));
        return assignDto;
    }

    @Override
    public AssignDto createAssign(AssignModel assignModel) throws IOException {
        AssignEntity assignEntity = new AssignEntity();
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null)
            return null;
        UserEntity userEntity = iUserRepository.findById(userId).orElse(null);

        // check name already exists
        if (iAssignRepository.getAllByNameAndIsDeleted(assignModel.getName(), false).size() > 0)
            throw new CustomHandleException(190);

        assignEntity.setCreateBy(userEntity);
        Date currentDate = new Date();
        assignEntity.setCreatedDate(currentDate);
        assignEntity.setStartDate(assignModel.getStartDate());
        assignEntity.setEndDate(assignModel.getEndDate());
        assignEntity.setDescription(assignModel.getDescription());
        assignEntity.setName(assignModel.getName());
        assignEntity.setIsDefault(assignModel.getIsDefault()); 
        assignEntity.setStatus(Const.status.TO_DO.name());
        assignEntity.setUpdatedDate(currentDate);
        assignEntity.setNature(assignModel.getNature());
        assignEntity.setType(assignModel.getType());
        assignEntity.setMission(iMissionRepository.findById(assignModel.getIdMission()).get());
        assignEntity = iAssignRepository.save(assignEntity);

        // add user create to dev list
        UserProjectEntity userCreate = new UserProjectEntity();
        userCreate.setCategory(Const.tableName.ASSIGNMENT.name());
        userCreate.setObjectId(assignEntity.getId());
        userCreate.setType(Const.type.TYPE_DEV.name());
        userCreate.setIdUser(userId);
        iUserProjectRepository.save(userCreate);

        if (assignModel.getUserDev() != null && assignModel.getUserDev().size() > 0) {
            for (Long userDev : assignModel.getUserDev()) {
                UserEntity user = iUserRepository.findById(userDev).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                    userProjectEntity.setObjectId(assignEntity.getId());
                    userProjectEntity.setType(Const.type.TYPE_DEV.name());
                    userProjectEntity.setIdUser(user.getUserId());
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
        if (assignModel.getUserFollow() != null && assignModel.getUserFollow().size() > 0) {
            for (Long userFollow : assignModel.getUserFollow()) {
                UserEntity user = iUserRepository.findById(userFollow).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                    userProjectEntity.setObjectId(assignEntity.getId());
                    userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                    userProjectEntity.setIdUser(user.getUserId());
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
        // add assign check list
        if (assignModel.getAssignCheckListModels() != null && assignModel.getAssignCheckListModels().size() > 0) {
            for (AssignCheckListModel assignCheckListModel : assignModel.getAssignCheckListModels()) {
                AssignCheckListEntity assignCheckListEntity = new AssignCheckListEntity();
                assignCheckListEntity.setContent(assignCheckListModel.getContent());
                assignCheckListEntity.setAssign(assignEntity);
                iAssignCheckListRepository.save(assignCheckListEntity);
            }
        }
        if (assignModel.getTagArray() != null && assignModel.getTagArray().length > 0) {
            for (String tagModel : assignModel.getTagArray()) {
                TagEntity tagEntity = iTagRepository.findByName(tagModel);
                if (tagEntity == null) {
                    TagEntity tagEntity1 = new TagEntity();
                    tagEntity1.setName(tagModel);
                    tagEntity1 = iTagRepository.save(tagEntity1);
                    TagRelationEntity tagRelationEntity = new TagRelationEntity();
                    tagRelationEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                    tagRelationEntity.setIdTag(tagEntity1.getId());
                    tagRelationEntity.setObjectId(assignEntity.getId());
                    iTagRelationRepository.save(tagRelationEntity);
                } else if (tagEntity != null) {
                    TagRelationEntity tagRelationEntity = iTagRelationRepository.checkIsEmpty(assignEntity.getId(),tagEntity.getId(),Const.tableName.ASSIGNMENT.name());
                    if (tagRelationEntity == null){
                        tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(assignEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }
        }
        if (assignModel.getFiles() != null) {
            for (MultipartFile m : assignModel.getFiles()) {
                if (!m.isEmpty()) {
                    String urlFile = fileUploadProvider.uploadFile("assignment", m);
                    FileEntity fileEntity = new FileEntity();
                    String fileName = m.getOriginalFilename();
                    fileEntity.setLink(urlFile);
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    fileEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                    fileEntity.setUploadedBy(userEntity);
                    fileEntity.setObjectId(assignEntity.getId());
                    iFileRepository.saveAndFlush(fileEntity);
                }
            }
        }
        iHistoryLogService.logCreate(assignEntity.getId(), assignEntity, Const.tableName.ASSIGNMENT, assignEntity.getName());
        AssignDto result = AssignDto.toDto(assignEntity);
        return result;
    }

    @Override
    public AssignDto updateAssign(AssignModel assignModel) throws IOException, ParseException {
        if (iAssignRepository.checkIsDeleted(assignModel.getId())) throw new CustomHandleException(491);

        List<String> fileUrlsKeeping = new ArrayList<>();
        List<FileEntity> fileOriginal = iFileRepository.getByCategoryAndObjectId(Const.tableName.ASSIGNMENT.name(), assignModel.getId());
        if (assignModel.getFileUrlsKeeping() != null) {
            assignModel.getFileUrlsKeeping().stream().map(url -> fileUrlsKeeping.add(url)).collect(Collectors.toList());
        }

        if (fileUrlsKeeping.size() > 0) {
            iFileRepository.deleteFileExistInObject(fileUrlsKeeping, Const.tableName.ASSIGNMENT.name(), assignModel.getId());
        } else {
            iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.ASSIGNMENT.name(), assignModel.getId());
        }

        AssignEntity assignEntity = iAssignRepository.findById(assignModel.getId()).orElse(null);
        // check name already exists
        if (!assignEntity.getName().equals(assignModel.getName())){
            if (iAssignRepository.getAllByNameAndIsDeleted(assignModel.getName(), false).size() > 0)
                throw new CustomHandleException(190);
        }
        AssignEntity missionOriginal = (AssignEntity) Const.copy(assignEntity);

        Set<Long> currentProjectUIDs = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.ASSIGNMENT.name(), assignEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        Set<Long> currentProjectIdFollows = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.ASSIGNMENT.name(), assignEntity.getId(), Const.type.TYPE_FOLLOWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        int countError = 0;
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)) {
            countError += 1;
        }
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectIdFollows::contains)) {
            countError += 1;
        }
        if (SecurityUtils.getCurrentUserId() != assignEntity.getCreateBy().getUserId()) {
            countError += 1;
        }
        if (countError == 3) {
            throw new CustomHandleException(11);
        }

        List<UserEntity> listUserDev = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_DEV.name(), assignEntity.getId());
        List<UserEntity> listUserFollow = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_FOLLOWER.name(), assignEntity.getId());
        List<TagEntity> listTag = iTagRepository.getAllByObjectIdAndCategory(assignEntity.getId(), Const.tableName.ASSIGNMENT.name());
        missionOriginal.setDevTeam(listUserDev);
        missionOriginal.setFollowTeam(listUserFollow);
        missionOriginal.setTagList(listTag);
        missionOriginal.setAttachFiles(fileOriginal);

        if (assignEntity == null)
            return null;
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null)
            return null;
        UserEntity userEntity = iUserRepository.findById(userId).orElse(null);
        Date currentDate = new Date();
        assignEntity.setCreatedDate(currentDate);
        assignEntity.setStartDate(assignModel.getStartDate());
        assignEntity.setEndDate(assignModel.getEndDate());
        assignEntity.setDescription(assignModel.getDescription());
        assignEntity.setName(assignModel.getName());
        assignEntity.setIsDefault(assignModel.getIsDefault());
        assignEntity.setStatus(Const.status.TO_DO.name());
        assignEntity.setUpdatedDate(currentDate);
        assignEntity.setNature(assignModel.getNature());
        assignEntity.setType(assignModel.getType());
        assignEntity.setMission(iMissionRepository.findById(assignModel.getIdMission()).get());
        assignEntity = iAssignRepository.save(assignEntity);

        // get list id dev old and new of project
        List<Long> listIdUserDevOld = new ArrayList<>();
        List<Long> listIdUserDevNew = new ArrayList<>();

        if (!assignModel.getUserDev().stream().anyMatch(userId::equals)) {
            listIdUserDevNew.add(userId);
        }
        listUserDev.stream().forEach(data -> listIdUserDevOld.add(data.getUserId()));
        assignModel.getUserDev().stream().forEach(data -> listIdUserDevNew.add(data));

        deleteOldUserAndSaveNewUser(listIdUserDevOld,listIdUserDevNew,Const.type.TYPE_DEV, assignEntity.getId(), Const.tableName.ASSIGNMENT);

        // get list id follower old and new of project
        List<Long> listIdUserFollowOld = new ArrayList<>();
        List<Long> listIdUserFollowNew = new ArrayList<>();

        listUserFollow.stream().forEach(data -> listIdUserFollowOld.add(data.getUserId()));
        assignModel.getUserFollow().stream().forEach(data -> listIdUserFollowNew.add(data));

        deleteOldUserAndSaveNewUser(listIdUserFollowOld,listIdUserFollowNew,Const.type.TYPE_FOLLOWER, assignEntity.getId(), Const.tableName.ASSIGNMENT);


        List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.ASSIGNMENT.name(), assignEntity.getId());
        iTagRelationRepository.deleteAll(tagRelationEntities);
        if (assignModel.getTagArray() != null && assignModel.getTagArray().length > 0) {
            for (String tagModel : assignModel.getTagArray()) {
                TagEntity tagEntity1 = new TagEntity();
                tagEntity1.setName(tagModel);
                tagEntity1 = iTagRepository.save(tagEntity1);
                TagRelationEntity tagRelationEntity = new TagRelationEntity();
                tagRelationEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                tagRelationEntity.setIdTag(tagEntity1.getId());
                tagRelationEntity.setObjectId(assignEntity.getId());
                iTagRelationRepository.save(tagRelationEntity);
            }
        }
        // delete all assign list old
        // get list id dev old and new of project
        List<String> listContentOld = new ArrayList<>();
        List<String> listContentNew = new ArrayList<>();

        iAssignCheckListRepository.findAllByAssign_Id(assignEntity.getId()).stream().forEach(data -> listContentOld.add(data.getContent()));
        if (assignModel.getAssignCheckListModels() != null){
            assignModel.getAssignCheckListModels().stream().forEach(data -> listContentNew.add(data.getContent()));
        }

        deleteOldAssignCheckListAndSaveNew(listContentOld,listContentNew, assignEntity.getId());
        // add new assign check list
        if (assignModel.getAssignCheckListModels() != null && assignModel.getAssignCheckListModels().size() > 0) {
            for (AssignCheckListModel assignCheckListModel : assignModel.getAssignCheckListModels()) {
                AssignCheckListEntity assignCheckListEntity = iAssignCheckListRepository.findByContentAndAssign_Id(assignCheckListModel.getContent(), assignEntity.getId());
                if (assignCheckListEntity != null){
                    assignCheckListEntity.setId(assignCheckListEntity.getId());
                    assignCheckListEntity.setIsDone(assignCheckListModel.getIsDone());
                }else {
                    assignCheckListEntity = new AssignCheckListEntity();
                    assignCheckListEntity.setIsDone(false);
                }
                assignCheckListEntity.setContent(assignCheckListModel.getContent());
                assignCheckListEntity.setAssign(assignEntity);
                iAssignCheckListRepository.save(assignCheckListEntity);
            }
        }

        if (assignModel.getFiles() != null && assignModel.getFiles().length > 0) {
            for (MultipartFile m : assignModel.getFiles()) {
                if (!m.isEmpty()) {
                    String urlFile = fileUploadProvider.uploadFile("assignment", m);
                    FileEntity fileEntity = new FileEntity();
                    String fileName = m.getOriginalFilename();
                    fileEntity.setLink(urlFile);
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    fileEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                    fileEntity.setUploadedBy(userEntity);
                    fileEntity.setObjectId(assignEntity.getId());
                    iFileRepository.saveAndFlush(fileEntity);
                    assignEntity.getAttachFiles().add(fileEntity);
                }
            }
        }
        iAssignRepository.save(assignEntity);
        List<UserEntity> userDev = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_DEV.name(), assignEntity.getId());
        List<UserEntity> userFollow = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_FOLLOWER.name(), assignEntity.getId());
        List<TagEntity> listTagEntity = iTagRepository.getAllByObjectIdAndCategory(assignEntity.getId(), Const.tableName.ASSIGNMENT.name());

        assignEntity.setDevTeam(userDev);
        assignEntity.setFollowTeam(userFollow);
        assignEntity.setTagList(listTagEntity);

        iHistoryLogService.logUpdate(assignEntity.getId(), missionOriginal, assignEntity, Const.tableName.ASSIGNMENT);
        AssignDto result = AssignDto.toDto(assignEntity);
        return result;
    }

    @Override
    public Boolean changIsDeleteById(Long id) {
        AssignEntity oldProject = this.iAssignRepository.findById(id).orElseThrow(() -> new RuntimeException("Assign not exist!!"));
        oldProject.setIsDeleted(true);
        this.iAssignRepository.saveAndFlush(oldProject);
        iHistoryLogService.logDelete(id, oldProject, Const.tableName.ASSIGNMENT, oldProject.getName());
        return true;
    }

    @Override
    public Page<AssignDto> findByPage(Integer pageIndex, Integer pageSize, String sortBy, String sortType, AssignModel assignModel) {
        Long userIdd = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String sql = "SELECT distinct new cy.dtos.mission.AssignDto(p) FROM AssignEntity p " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        String countSQL = "select count(distinct(p)) from AssignEntity p  " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        if (assignModel.getTextSearch() != null && assignModel.getTextSearch().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE (up.category like 'ASSIGNMENT') AND p.isDeleted = false and (p.mission.id = :idMission) ";
        countSQL += " WHERE (up.category like 'ASSIGNMENT') AND p.isDeleted = false and (p.mission.id = :idMission) ";

        if (assignModel.getStatus() != null) {
            sql += " AND p.status = :status ";
            countSQL += " AND p.status = :status ";
        }
        if (assignModel.getNature() != null) {
            sql += " AND p.nature = :nature ";
            countSQL += " AND p.nature = :nature ";
        }
        if (assignModel.getType() != null) {
            sql += " AND p.type = :type ";
            countSQL += " AND p.type = :type ";
        }
        if (assignModel.getMonthFilter() != null) {
            sql += " AND MONTH(p.startDate) = :monthFilter ";
            countSQL += " AND MONTH(p.startDate) = :monthFilter ";
        }
        if (assignModel.getYearFilter() != null) {
            sql += " AND YEAR(p.startDate) = :yearFilter ";
            countSQL += " AND YEAR(p.startDate) = :yearFilter ";
        }
        if (assignModel.getTextSearch() != null) {
            if (assignModel.getTextSearch().charAt(0) == '#') {
                sql += " AND (t.name = :textSearch ) AND (tr.category LIKE 'ASSIGNMENT') ";
                countSQL += " AND (t.name = :textSearch ) AND (tr.category LIKE 'ASSIGNMENT') ";
            } else {
                sql += " AND (p.name LIKE :textSearch ) ";
                countSQL += "AND (p.name LIKE :textSearch ) ";
            }
        }
        if (sortBy != ""){
            switch (sortBy){
                case "startDate":
                    sql += " order by p.startDate";
                    break;
                case "endDate":
                    sql += " order by p.endDate";
                    break;
            }
        }else {
            sql += " order by up.createdDate";
        }
        if (sortType != ""){
            sql += " " + sortType;
        }else {
            sql += " desc";
        }

        Query q = manager.createQuery(sql, AssignDto.class);
        Query qCount = manager.createQuery(countSQL);

//        if (assignModel.getOtherProject()) {
//            q.setParameter("currentUserId", userIdd);
//            qCount.setParameter("currentUserId", userIdd);
//        }
        q.setParameter("idMission", assignModel.getIdMission());
        qCount.setParameter("idMission", assignModel.getIdMission());

        if (assignModel.getStatus() != null) {
            q.setParameter("status", assignModel.getStatus());
            qCount.setParameter("status", assignModel.getStatus());
        }
        if (assignModel.getNature() != null) {
            q.setParameter("nature", assignModel.getNature());
            qCount.setParameter("nature", assignModel.getNature());
        }
        if (assignModel.getType() != null) {
            q.setParameter("type", assignModel.getType());
            qCount.setParameter("type", assignModel.getType());
        }
        if (assignModel.getMonthFilter() != null) {
            q.setParameter("monthFilter", Integer.parseInt(assignModel.getMonthFilter()));
            qCount.setParameter("monthFilter", Integer.parseInt(assignModel.getMonthFilter()));
        }
        if (assignModel.getYearFilter() != null) {
            q.setParameter("yearFilter", Integer.parseInt(assignModel.getYearFilter()));
            qCount.setParameter("yearFilter", Integer.parseInt(assignModel.getYearFilter()));
        }
        if (assignModel.getTextSearch() != null) {
            String textSearch = assignModel.getTextSearch();
            if (assignModel.getTextSearch().charAt(0) == '#') {
                q.setParameter("textSearch", textSearch.substring(1));
                qCount.setParameter("textSearch", textSearch.substring(1));
            } else {
                q.setParameter("textSearch", "%" + textSearch + "%");
                qCount.setParameter("textSearch", "%" + textSearch + "%");
            }
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<AssignDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);

        result.stream().forEach(data -> {
            List<Long> listIdDev = iUserRepository.getAllIdDevByTypeAndObjectId(Const.tableName.ASSIGNMENT.name(), data.getId(),Const.type.TYPE_DEV.name());
            List<Long> listIdDevCheck = listIdDev != null ? listIdDev : new ArrayList<>();
            List<UserDto> userDev = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.ASSIGNMENT.name(), Const.type.TYPE_DEV.name(), data.getId());
            data.setUserDevs(userDev);
            data.setEditable(false);

            if (listIdDevCheck.stream().anyMatch(userIdd::equals)){
                data.setEditable(true);
            }

        });
        return result;
    }

    @Override
    public boolean updateStatusAssign(Long idAssign, SubTaskUpdateModel subTaskUpdateModel) {
        AssignEntity assignEntity = iAssignRepository.findById(idAssign).orElseThrow(() -> new CustomHandleException(253));
        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = iUserProjectRepository.getAllIdDevOfProjectByAssignIdInThisMission(idAssign, listType);
        if (!listIdDevInProject.stream().anyMatch(idUser::equals)) {
            throw new CustomHandleException(5);
        }

        if (assignEntity.getStatus().equals(subTaskUpdateModel.getNewStatus().name())) {
            if (!assignEntity.getStatus().equals("IN_REVIEW")){
                throw new CustomHandleException(205);
            }
        }

        AssignEntity assignEntityOriginal = assignEntity;

        // check only reviewer can change status to done
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.DONE.name())) {
            Set<Long> idReviewer = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.ASSIGNMENT.name(), idAssign, Const.type.TYPE_REVIEWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
            if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(idReviewer::contains)) {
                throw new CustomHandleException(254);
            }
        }

        // If current status is in review -> delete reviewer
//        if (assignEntity.getStatus().equals(Const.status.IN_REVIEW.name())) {
//            for (UserProjectEntity userProjectEntity : userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.TASK.name(), taskId, Const.type.TYPE_REVIEWER.name())) {
//                userProjectRepository.deleteByIdNative(userProjectEntity.getId());
//            }
//        }

        assignEntity.setStatus(subTaskUpdateModel.getNewStatus().name());
        AssignEntity saveResult = iAssignRepository.save(assignEntity);
        if (saveResult == null) {
            return false;
        }

        // If new status is IN_REVIEW -> add reviewer
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.IN_REVIEW.name())) {
            if (subTaskUpdateModel.getReviewerIdList() == null) {
                throw new CustomHandleException(206);
            }
            // Delete old reviewer
            for (UserProjectEntity userProjectEntity : iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.ASSIGNMENT.name(), idAssign, Const.type.TYPE_REVIEWER.name())) {
                iUserProjectRepository.deleteByIdNative(userProjectEntity.getId());
            }

//            List<Long> listIdReviewer = userRepository.getAllIdDevByTypeAndObjectId(Const.tableName.TASK.name(), taskId, Const.type.TYPE_REVIEWER.name());
//            projectService.deleteOldUserAndSaveNewUser(listIdReviewer,subTaskUpdateModel.getReviewerIdList(),Const.type.TYPE_REVIEWER,taskId,Const.tableName.TASK);


            for (Long reviewerId : subTaskUpdateModel.getReviewerIdList()) {
                // Check if reviewer user is not existed
                iUserRepository.findById(reviewerId).orElseThrow(() -> new CustomHandleException(207));
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.ASSIGNMENT.name());
                userProjectEntity.setObjectId(idAssign);
                userProjectEntity.setIdUser(reviewerId);
                userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                iUserProjectRepository.save(userProjectEntity);
            }
        }

        iHistoryLogService.logUpdate(assignEntity.getId(), assignEntityOriginal, saveResult, Const.tableName.ASSIGNMENT);
        return true;
    }

    public void deleteOldUserAndSaveNewUser(List<Long> listIdOld,List<Long> listIdNew,Const.type userType,Long projectId,Const.tableName category){
        // find user in listIdOld but not in listIdNew
        List<Long> diff1 = new ArrayList<>(listIdOld);
        diff1.removeAll(listIdNew);

        // find user in listIdNew but not in listIdOld
        List<Long> diff2 = new ArrayList<>(listIdNew);
        diff2.removeAll(listIdOld);

        // delete old user not in listIdNew
        if (diff1.size() > 0){
            for (Long idUser : diff1) {
                iUserProjectRepository.deleteByIdUserAndTypeAndObjectId(idUser,userType.name(),projectId,category.toString());
            }
        }
        // save new user in listIdNew
        if (diff2.size() > 0){
            for (Long idUser : diff2) {
                UserEntity user = iUserRepository.findById(idUser).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(category.name());
                    userProjectEntity.setObjectId(projectId);
                    userProjectEntity.setType(userType.name());
                    userProjectEntity.setIdUser(idUser);
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
    }

    public void deleteOldAssignCheckListAndSaveNew(List<String> listContentOld,List<String> listContentNew,Long objectId){
        // find user in listContentOld but not in listContentNew
        List<String> diff1 = new ArrayList<>(listContentOld);
        diff1.removeAll(listContentNew);

        // find user in listContentNew but not in listContentOld
        List<String> diff2 = new ArrayList<>(listContentNew);
        diff2.removeAll(listContentOld);

        // delete old user not in listContentNew
        if (diff1.size() > 0){
            for (String content : diff1) {
                iAssignCheckListRepository.deleteByContentCheckListAndAssignId(content, objectId);
            }
        }
        // save new user in listIdNew
        if (diff2.size() > 0){
            for (String content : diff2) {
                AssignCheckListEntity assignCheckListEntity = new AssignCheckListEntity();
                assignCheckListEntity.setContent(content);
                assignCheckListEntity.setAssign(iAssignRepository.findById(objectId).get());
                iAssignCheckListRepository.save(assignCheckListEntity);
            }
        }
    }
}
