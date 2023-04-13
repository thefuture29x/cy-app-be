package cy.services.mission.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.mission.AssignDto;
import cy.dtos.mission.MissionDto;
import cy.entities.common.*;
import cy.entities.mission.AssignCheckListEntity;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import cy.models.mission.AssignCheckListModel;
import cy.models.mission.AssignModel;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        assignModel.getAssignCheckListModels().stream().forEach(data -> listContentNew.add(data.getContent()));

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
