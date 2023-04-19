package cy.services.mission.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.common.UserDto;
import cy.dtos.common.UserMetaDto;
import cy.dtos.mission.MissionDto;
import cy.dtos.project.ProjectDto;
import cy.entities.common.*;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import cy.entities.project.ProjectEntity;
import cy.entities.project.SubTaskEntity;
import cy.entities.project.TaskEntity;
import cy.models.mission.MissionModel;
import cy.models.project.SubTaskUpdateModel;
import cy.models.project.UserViewProjectModel;
import cy.repositories.common.*;
import cy.repositories.mission.IAssignRepository;
import cy.repositories.mission.IMissionRepository;
import cy.repositories.mission.IUserViewMissionRepository;
import cy.repositories.project.IFeatureRepository;
import cy.services.common.IFileService;
import cy.services.common.IHistoryLogService;
import cy.services.common.ITagService;
import cy.services.mission.IAssignService;
import cy.services.mission.IMissionService;
import cy.services.mission.IUserViewMissionService;
import cy.services.project.IFeatureService;
import cy.services.project.IUserViewProjectService;
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
public class MissionServiceImpl implements IMissionService {

    @Autowired
    IMissionRepository iMissionRepository;
    @Autowired
    IFeatureRepository featureRepository;
    @Autowired
    IFeatureService featureService;
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    EntityManager manager;
    @Autowired
    ITagService iTagService;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    IUserProjectRepository iUserProjectRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Autowired
    IUserViewMissionService iUserViewMissionService;
    @Autowired
    IAssignRepository iAssignRepository;

    @Override
    public MissionDto  findById(Long id, boolean view) {
        if (iMissionRepository.checkIsDeleted(id)) throw new CustomHandleException(491);
        if (view){
            UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
            iUserViewMissionService.add(new UserViewProjectModel(userEntity.getUserId(), id));
        }
        MissionEntity missionEntity = this.iMissionRepository.findById(id).orElse(null);
        MissionDto missionDto = MissionDto.toDto(iMissionRepository.findById(id).orElse(null));
        if (missionDto == null)
            return null;
        List<UserDto> userDev = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.MISSION.name(), Const.type.TYPE_DEV.name(), missionEntity.getId());
        List<UserDto> userFollow = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.MISSION.name(), Const.type.TYPE_FOLLOWER.name(), missionEntity.getId());
        List<UserDto> userView = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.MISSION.name(), Const.type.TYPE_VIEWER.name(), missionEntity.getId());
        missionDto.setUserView(userView);
        missionDto.setUserDevs(userDev);
        missionDto.setUserFollows(userFollow);
        missionDto.setTagArray(iTagRelationRepository.getNameTagByCategoryAndObjectId(Const.tableName.MISSION.name(), missionEntity.getId()));
        return missionDto;
    }

    @Override
    public MissionDto createMission(MissionModel missionModel) throws IOException {
        MissionEntity missionEntity = new MissionEntity();
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null)
            return null;
        UserEntity userEntity = iUserRepository.findById(userId).orElse(null);

        // check name already exists
        if (iMissionRepository.getAllByNameAndIsDeleted(missionModel.getName(), false).size() > 0)
            throw new CustomHandleException(190);

        missionEntity.setCreateBy(userEntity);
        Date currentDate = new Date();
        missionEntity.setCreatedDate(currentDate);
        missionEntity.setStartDate(missionModel.getStartDate());
        missionEntity.setEndDate(missionModel.getEndDate());
        missionEntity.setDescription(missionModel.getDescription());
        missionEntity.setName(missionModel.getName());
        missionEntity.setIsDefault(missionModel.getIsDefault());
        missionEntity.setStatus(Const.status.TO_DO.name());
        missionEntity.setUpdatedDate(currentDate);
        missionEntity.setNature(missionModel.getNature());
        missionEntity.setType(missionModel.getType());
        missionEntity = iMissionRepository.save(missionEntity);

        // add user create to dev list
        UserProjectEntity userCreate = new UserProjectEntity();
        userCreate.setCategory(Const.tableName.MISSION.name());
        userCreate.setObjectId(missionEntity.getId());
        userCreate.setType(Const.type.TYPE_DEV.name());
        userCreate.setIdUser(userId);
        iUserProjectRepository.save(userCreate);

        if (missionModel.getUserDev() != null && missionModel.getUserDev().size() > 0) {
            for (Long userDev : missionModel.getUserDev()) {
                UserEntity user = iUserRepository.findById(userDev).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(Const.tableName.MISSION.name());
                    userProjectEntity.setObjectId(missionEntity.getId());
                    userProjectEntity.setType(Const.type.TYPE_DEV.name());
                    userProjectEntity.setIdUser(user.getUserId());
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
        if (missionModel.getUserFollow() != null && missionModel.getUserFollow().size() > 0) {
            for (Long userFollow : missionModel.getUserFollow()) {
                UserEntity user = iUserRepository.findById(userFollow).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(Const.tableName.MISSION.name());
                    userProjectEntity.setObjectId(missionEntity.getId());
                    userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                    userProjectEntity.setIdUser(user.getUserId());
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
        if (missionModel.getUserViewer() != null && missionModel.getUserViewer().size() > 0) {
            for (Long userFollow : missionModel.getUserViewer()) {
                UserEntity user = iUserRepository.findById(userFollow).orElse(null);
                if (user != null) {
                    UserProjectEntity userProjectEntity = new UserProjectEntity();
                    userProjectEntity.setCategory(Const.tableName.MISSION.name());
                    userProjectEntity.setObjectId(missionEntity.getId());
                    userProjectEntity.setType(Const.type.TYPE_VIEWER.name());
                    userProjectEntity.setIdUser(user.getUserId());
                    iUserProjectRepository.save(userProjectEntity);
                }
            }
        }
        if (missionModel.getTagArray() != null && missionModel.getTagArray().length > 0) {
            for (String tagModel : missionModel.getTagArray()) {
                TagEntity tagEntity = iTagRepository.findByName(tagModel);
                if (tagEntity == null) {
                    TagEntity tagEntity1 = new TagEntity();
                    tagEntity1.setName(tagModel);
                    tagEntity1 = iTagRepository.save(tagEntity1);
                    TagRelationEntity tagRelationEntity = new TagRelationEntity();
                    tagRelationEntity.setCategory(Const.tableName.MISSION.name());
                    tagRelationEntity.setIdTag(tagEntity1.getId());
                    tagRelationEntity.setObjectId(missionEntity.getId());
                    iTagRelationRepository.save(tagRelationEntity);
                } else if (tagEntity != null) {
                    TagRelationEntity tagRelationEntity = iTagRelationRepository.checkIsEmpty(missionEntity.getId(),tagEntity.getId(),Const.tableName.MISSION.name());
                    if (tagRelationEntity == null){
                        tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.MISSION.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(missionEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }
        }
        if (missionModel.getFiles() != null) {
            for (MultipartFile m : missionModel.getFiles()) {
                if (!m.isEmpty()) {
                    String urlFile = fileUploadProvider.uploadFile("mission", m);
                    FileEntity fileEntity = new FileEntity();
                    String fileName = m.getOriginalFilename();
                    fileEntity.setLink(urlFile);
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    fileEntity.setCategory(Const.tableName.MISSION.name());
                    fileEntity.setUploadedBy(userEntity);
                    fileEntity.setObjectId(missionEntity.getId());
                    iFileRepository.saveAndFlush(fileEntity);
                }
            }
        }
        iHistoryLogService.logCreate(missionEntity.getId(), missionEntity, Const.tableName.MISSION, missionEntity.getName());
        MissionDto result = MissionDto.toDto(missionEntity);
        return result;
    }

    @Override
    public MissionDto updateMission(MissionModel missionModel) throws IOException, ParseException {
        if (iMissionRepository.checkIsDeleted(missionModel.getId())) throw new CustomHandleException(491);

        List<String> fileUrlsKeeping = new ArrayList<>();
        List<FileEntity> fileOriginal = iFileRepository.getByCategoryAndObjectId(Const.tableName.MISSION.name(), missionModel.getId());
        if (missionModel.getFileUrlsKeeping() != null) {
            missionModel.getFileUrlsKeeping().stream().map(url -> fileUrlsKeeping.add(url)).collect(Collectors.toList());
        }


        if (fileUrlsKeeping.size() > 0) {
            iFileRepository.deleteFileExistInObject(fileUrlsKeeping, Const.tableName.MISSION.name(), missionModel.getId());
        } else {
            iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.MISSION.name(), missionModel.getId());
        }
        
        MissionEntity missionEntity = iMissionRepository.findById(missionModel.getId()).orElse(null);
        // check name already exists
        if (!missionEntity.getName().equals(missionModel.getName())){
            if (iMissionRepository.getAllByNameAndIsDeleted(missionModel.getName(), false).size() > 0)
                throw new CustomHandleException(190);
        }
        MissionEntity missionOriginal = (MissionEntity) Const.copy(missionEntity);

        Set<Long> currentProjectUIDs = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(), missionEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        Set<Long> currentProjectIdFollows = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(), missionEntity.getId(), Const.type.TYPE_FOLLOWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        int countError = 0;
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)) {
            countError += 1;
        }
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectIdFollows::contains)) {
            countError += 1;
        }
        if (SecurityUtils.getCurrentUserId() != missionEntity.getCreateBy().getUserId()) {
            countError += 1;
        }
        if (countError == 3) {
            throw new CustomHandleException(11);
        }

        List<UserEntity> listUserDev = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_DEV.name(), missionEntity.getId());
        List<UserEntity> listUserFollow = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_FOLLOWER.name(), missionEntity.getId());
        List<UserEntity> listUserView = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_VIEWER.name(), missionEntity.getId());
        List<TagEntity> listTag = iTagRepository.getAllByObjectIdAndCategory(missionEntity.getId(), Const.tableName.MISSION.name());
        missionOriginal.setViewTeam(listUserView);
        missionOriginal.setDevTeam(listUserDev);
        missionOriginal.setFollowTeam(listUserFollow);
        missionOriginal.setTagList(listTag);
        missionOriginal.setAttachFiles(fileOriginal);

        if (missionEntity == null)
            return null;
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null)
            return null;
        UserEntity userEntity = iUserRepository.findById(userId).orElse(null);
        missionEntity.setCreateBy(userEntity);
        // Format date get only yyyy-MM-dd
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String currentDateString = simpleDateFormat.format(currentDate);
        Date currentDateCheck = simpleDateFormat.parse(currentDateString);

        String startDateString = simpleDateFormat.format(missionModel.getStartDate());
        Date startDateCheck = simpleDateFormat.parse(startDateString);
        // End format date get only yyyy-MM-dd

        missionEntity.setCreatedDate(currentDate);
        missionEntity.setStartDate(missionModel.getStartDate());
        missionEntity.setEndDate(missionModel.getEndDate());
        missionEntity.setDescription(missionModel.getDescription());
        missionEntity.setName(missionModel.getName());
        missionEntity.setIsDefault(missionModel.getIsDefault());

        missionEntity.setStatus(Const.status.TO_DO.name());
        missionEntity.setUpdatedDate(currentDate);
        missionEntity.setNature(missionModel.getNature());
        missionEntity.setType(missionModel.getType());

        // get list id dev old and new of project
        List<Long> listIdUserDevOld = new ArrayList<>();
        List<Long> listIdUserDevNew = new ArrayList<>();

        if (!missionModel.getUserDev().stream().anyMatch(userId::equals)) {
            listIdUserDevNew.add(userId);
        }
        listUserDev.stream().forEach(data -> listIdUserDevOld.add(data.getUserId()));
        missionModel.getUserDev().stream().forEach(data -> listIdUserDevNew.add(data));

        deleteOldUserAndSaveNewUser(listIdUserDevOld,listIdUserDevNew,Const.type.TYPE_DEV, missionEntity.getId(), Const.tableName.MISSION);

        // get list id follower old and new of project
        List<Long> listIdUserFollowOld = new ArrayList<>();
        List<Long> listIdUserFollowNew = new ArrayList<>();

        listUserFollow.stream().forEach(data -> listIdUserFollowOld.add(data.getUserId()));
        missionModel.getUserFollow().stream().forEach(data -> listIdUserFollowNew.add(data));

        deleteOldUserAndSaveNewUser(listIdUserFollowOld,listIdUserFollowNew,Const.type.TYPE_FOLLOWER, missionEntity.getId(), Const.tableName.MISSION);

        // get list id follower old and new of project
        List<Long> listIdUserViewerOld = new ArrayList<>();
        List<Long> listIdUserViewerNew = new ArrayList<>();

        listUserView.stream().forEach(data -> listIdUserViewerOld.add(data.getUserId()));
        missionModel.getUserViewer().stream().forEach(data -> listIdUserViewerNew.add(data));

        deleteOldUserAndSaveNewUser(listIdUserViewerOld,listIdUserViewerNew,Const.type.TYPE_VIEWER, missionEntity.getId(), Const.tableName.MISSION);


        List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.MISSION.name(), missionEntity.getId());
        iTagRelationRepository.deleteAll(tagRelationEntities);
        if (missionModel.getTagArray() != null && missionModel.getTagArray().length > 0) {
            for (String tagModel : missionModel.getTagArray()) {
                TagEntity tagEntity1 = new TagEntity();
                tagEntity1.setName(tagModel);
                tagEntity1 = iTagRepository.save(tagEntity1);
                TagRelationEntity tagRelationEntity = new TagRelationEntity();
                tagRelationEntity.setCategory(Const.tableName.MISSION.name());
                tagRelationEntity.setIdTag(tagEntity1.getId());
                tagRelationEntity.setObjectId(missionEntity.getId());
                iTagRelationRepository.save(tagRelationEntity);
            }
        }



        if (missionModel.getFiles() != null && missionModel.getFiles().length > 0) {
            for (MultipartFile m : missionModel.getFiles()) {
                if (!m.isEmpty()) {
                    String urlFile = fileUploadProvider.uploadFile("project", m);
                    FileEntity fileEntity = new FileEntity();
                    String fileName = m.getOriginalFilename();
                    fileEntity.setLink(urlFile);
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    fileEntity.setCategory(Const.tableName.MISSION.name());
                    fileEntity.setUploadedBy(userEntity);
                    fileEntity.setObjectId(missionEntity.getId());
                    iFileRepository.saveAndFlush(fileEntity);
                    missionEntity.getAttachFiles().add(fileEntity);
                }
            }
        }
        iMissionRepository.save(missionEntity);
        List<UserEntity> userDev = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_DEV.name(), missionEntity.getId());
        List<UserEntity> userFollow = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_FOLLOWER.name(), missionEntity.getId());
        List<UserEntity> userView = iUserRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.MISSION.name(), Const.type.TYPE_VIEWER.name(), missionEntity.getId());
        List<TagEntity> listTagEntity = iTagRepository.getAllByObjectIdAndCategory(missionEntity.getId(), Const.tableName.MISSION.name());

        missionEntity.setViewTeam(userView);
        missionEntity.setDevTeam(userDev);
        missionEntity.setFollowTeam(userFollow);
        missionEntity.setTagList(listTagEntity);

        iHistoryLogService.logUpdate(missionEntity.getId(), missionOriginal, missionEntity, Const.tableName.MISSION);
        MissionDto result = MissionDto.toDto(missionEntity);
        return result;
    }

    @Override
    public Boolean changIsDeleteById(Long id) {
        MissionEntity oldProject = this.iMissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Mission not exist!!"));
        oldProject.setIsDeleted(true);
        this.iMissionRepository.saveAndFlush(oldProject);
        iHistoryLogService.logDelete(id, oldProject, Const.tableName.MISSION, oldProject.getName());
        return true;
    }

    @Override
    public Page<MissionDto> findByPage(Integer pageIndex, Integer pageSize,String sortBy, String sortType,MissionModel missionModel) {
        Long userIdd = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String sql = "SELECT distinct new cy.dtos.mission.MissionDto(p) FROM MissionEntity p " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        String countSQL = "select count(distinct(p)) from MissionEntity p  " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        if (missionModel.getTextSearch() != null && missionModel.getTextSearch().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE (up.category like 'MISSION') AND p.isDeleted = false ";
        countSQL += " WHERE (up.category like 'MISSION') AND p.isDeleted = false ";

        if (missionModel.getTypeUser() != null) {
            sql += " AND up.type = :typeUser";
            countSQL += " AND up.type = :typeUser";
        } else {
            sql += " AND up.type is not null";
            countSQL += " AND up.type is not null";
        }

//        if (missionModel.getOtherProject()) {
//            sql += " and (up.idUser <> :currentUserId) ";
//            countSQL += " and (up.idUser <> :currentUserId) ";
//        }
        sql += " and (up.idUser = :currentUserId) ";
        countSQL += " and (up.idUser = :currentUserId) ";

        if (missionModel.getStatus() != null) {
            sql += " AND p.status = :status ";
            countSQL += " AND p.status = :status ";
        }
        if (missionModel.getNature() != null) {
            sql += " AND p.nature = :nature ";
            countSQL += " AND p.nature = :nature ";
        }
        if (missionModel.getType() != null) {
            sql += " AND p.type = :typeMission ";
            countSQL += " AND p.type = :typeMission ";
        }
        if (missionModel.getMonthFilter() != null) {
            sql += " AND MONTH(p.startDate) = :monthFilter ";
            countSQL += " AND MONTH(p.startDate) = :monthFilter ";
        }
        if (missionModel.getYearFilter() != null) {
            sql += " AND YEAR(p.startDate) = :yearFilter ";
            countSQL += " AND YEAR(p.startDate) = :yearFilter ";
        }
        if (missionModel.getTextSearch() != null) {
            if (missionModel.getTextSearch().charAt(0) == '#') {
                sql += " AND (t.name = :textSearch ) AND (tr.category LIKE 'MISSION') ";
                countSQL += " AND (t.name = :textSearch ) AND (tr.category LIKE 'MISSION') ";
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

        Query q = manager.createQuery(sql, MissionDto.class);
        Query qCount = manager.createQuery(countSQL);

//        if (missionModel.getOtherProject()) {
//            q.setParameter("currentUserId", userIdd);
//            qCount.setParameter("currentUserId", userIdd);
//        }
        q.setParameter("currentUserId", userIdd);
        qCount.setParameter("currentUserId", userIdd);

        if (missionModel.getStatus() != null) {
            q.setParameter("status", missionModel.getStatus());
            qCount.setParameter("status", missionModel.getStatus());
        }
        if (missionModel.getNature() != null) {
            q.setParameter("nature", missionModel.getNature());
            qCount.setParameter("nature", missionModel.getNature());
        }
        if (missionModel.getType() != null) {
            q.setParameter("typeMission", missionModel.getType());
            qCount.setParameter("typeMission", missionModel.getType());
        }
        if (missionModel.getMonthFilter() != null) {
            q.setParameter("monthFilter", Integer.parseInt(missionModel.getMonthFilter()));
            qCount.setParameter("monthFilter", Integer.parseInt(missionModel.getMonthFilter()));
        }
        if (missionModel.getYearFilter() != null) {
            q.setParameter("yearFilter", Integer.parseInt(missionModel.getYearFilter()));
            qCount.setParameter("yearFilter", Integer.parseInt(missionModel.getYearFilter()));
        }
        if (missionModel.getTextSearch() != null) {
            String textSearch = missionModel.getTextSearch();
            if (missionModel.getTextSearch().charAt(0) == '#') {
                q.setParameter("textSearch", textSearch.substring(1));
                qCount.setParameter("textSearch", textSearch.substring(1));
            } else {
                q.setParameter("textSearch", "%" + textSearch + "%");
                qCount.setParameter("textSearch", "%" + textSearch + "%");
            }
        }
        if (missionModel.getTypeUser() != null) {
            q.setParameter("typeUser", missionModel.getTypeUser());
            qCount.setParameter("typeUser", missionModel.getTypeUser());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<MissionDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);

        result.stream().forEach(data -> {
            List<Long> listIdDev = iUserRepository.getAllIdDevByTypeAndObjectId(Const.tableName.MISSION.name(), data.getId(),Const.type.TYPE_DEV.name());
            List<Long> listIdDevCheck = listIdDev != null ? listIdDev : new ArrayList<>();
            List<UserDto> userDev = iUserRepository.getByCategoryAndTypeAndObjectid(Const.tableName.MISSION.name(), Const.type.TYPE_DEV.name(), data.getId());
            data.setUserDevs(userDev);
            data.setEditable(false);

            if (listIdDevCheck.stream().anyMatch(userIdd::equals)){
                data.setEditable(true);
            }

        });
        return result;
    }

    @Override
    public List<UserMetaDto> getAllUserInMission(String category, String type, Long idObject) {
        return iUserRepository.getAllByCategoryAndTypeAndObjectId(category,type, idObject).stream().map(data -> UserMetaDto.toDto(data)).collect(Collectors.toList());
    }

    @Override
    public boolean updateStatusMission(Long idMission, SubTaskUpdateModel subTaskUpdateModel) {
        MissionEntity missionEntityExist = iMissionRepository.findById(idMission).orElseThrow(() -> new CustomHandleException(253));

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
//        List<String> listType = new ArrayList<>();
//        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = iUserProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(),idMission,Const.type.TYPE_DEV.toString());
        if (!listIdDevInProject.stream().anyMatch(idUser::equals)) {
            throw new CustomHandleException(5);
        }


        if (missionEntityExist.getStatus().equals(subTaskUpdateModel.getNewStatus().name())) {
            if (!missionEntityExist.getStatus().equals("IN_REVIEW")){
                throw new CustomHandleException(205);
            }
        }

        MissionEntity taskEntityOriginal = missionEntityExist;

        // check only reviewer can change status to done
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.DONE.name())) {
            Set<Long> idReviewer = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(), idMission, Const.type.TYPE_REVIEWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
            if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(idReviewer::contains)) {
                throw new CustomHandleException(254);
            }
        }

        // If current status is in review -> delete reviewer
//        if (missionEntityExist.getStatus().equals(Const.status.IN_REVIEW.name())) {
//            for (UserProjectEntity userProjectEntity : iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(), idMission, Const.type.TYPE_REVIEWER.name())) {
//                iUserProjectRepository.deleteByIdNative(userProjectEntity.getId());
//            }
//        }

        missionEntityExist.setStatus(subTaskUpdateModel.getNewStatus().name());
        MissionEntity saveResult = iMissionRepository.save(missionEntityExist);
        if (saveResult == null) {
            return false;
        }

        // If new status is IN_REVIEW -> add reviewer
        if (subTaskUpdateModel.getNewStatus().name().equals(Const.status.IN_REVIEW.name())) {
            if (subTaskUpdateModel.getReviewerIdList() == null) {
                throw new CustomHandleException(206);
            }
            // Delete old reviewer
            for (UserProjectEntity userProjectEntity : iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.MISSION.name(), idMission, Const.type.TYPE_REVIEWER.name())) {
                iUserProjectRepository.deleteByIdNative(userProjectEntity.getId());
            }

//            List<Long> listIdReviewer = iUserRepository.getAllIdDevByTypeAndObjectId(Const.tableName.MISSION.name(), idMission, Const.type.TYPE_REVIEWER.name());
//            projectService.deleteOldUserAndSaveNewUser(listIdReviewer,subTaskUpdateModel.getReviewerIdList(),Const.type.TYPE_REVIEWER,idMission,Const.tableName.TASK);


            for (Long reviewerId : subTaskUpdateModel.getReviewerIdList()) {
                // Check if reviewer user is not existed
                iUserRepository.findById(reviewerId).orElseThrow(() -> new CustomHandleException(207));
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.MISSION.name());
                userProjectEntity.setObjectId(idMission);
                userProjectEntity.setIdUser(reviewerId);
                userProjectEntity.setType(Const.type.TYPE_REVIEWER.name());
                iUserProjectRepository.save(userProjectEntity);
            }
        }

        iHistoryLogService.logUpdate(missionEntityExist.getId(), taskEntityOriginal, saveResult, Const.tableName.MISSION);
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
}
