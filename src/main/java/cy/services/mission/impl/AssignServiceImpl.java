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
import cy.repositories.common.IFileRepository;
import cy.repositories.common.IHistoryLogRepository;
import cy.repositories.common.IUserProjectRepository;
import cy.repositories.common.IUserRepository;
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
import java.util.Date;

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


}
