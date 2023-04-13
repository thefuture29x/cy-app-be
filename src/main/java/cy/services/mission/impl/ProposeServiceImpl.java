package cy.services.mission.impl;

import cy.dtos.common.CommentDto;
import cy.dtos.mission.ProposeDto;
import cy.dtos.project.ProjectDto;
import cy.entities.common.FileEntity;
import cy.entities.common.UserEntity;
import cy.entities.mission.ProposeEntity;
import cy.entities.project.ProjectEntity;
import cy.models.mission.ProposeModel;
import cy.repositories.common.ICommentRepository;
import cy.repositories.common.IFileRepository;
import cy.repositories.mission.IProposeRepository;
import cy.services.common.IHistoryLogService;
import cy.services.mission.IAssignService;
import cy.services.mission.IProposeService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProposeServiceImpl implements IProposeService {
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IProposeRepository iProposeRepository;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Autowired
    ICommentRepository iCommentRepository;

    @Override
    public ProposeDto createPropose(ProposeModel proposeModel) throws IOException {
        ProposeEntity proposeEntity = new ProposeEntity();
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();

        proposeEntity.setCreateBy(userEntity);
        proposeEntity.setCategory(proposeModel.getCategory());
        proposeEntity.setObjectId(proposeModel.getObjectId());
        proposeEntity.setDescription(proposeModel.getDescription());
        proposeEntity = iProposeRepository.save(proposeEntity);

        if (proposeModel.getFiles() != null) {
            for (MultipartFile m : proposeModel.getFiles()) {
                if (!m.isEmpty()) {
                    String urlFile = fileUploadProvider.uploadFile("propose", m);
                    FileEntity fileEntity = new FileEntity();
                    String fileName = m.getOriginalFilename();
                    fileEntity.setLink(urlFile);
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    fileEntity.setCategory(Const.tableName.PROPOSE.name());
                    fileEntity.setUploadedBy(userEntity);
                    fileEntity.setObjectId(proposeEntity.getId());
                    iFileRepository.saveAndFlush(fileEntity);
                }
            }
        }
        iHistoryLogService.logCreate(proposeEntity.getId(), proposeEntity, Const.tableName.PROPOSE, null);
        ProposeDto result = ProposeDto.toDto(proposeEntity);
        return result;
    }

    @Override
    public List<ProposeDto> findAllOfObject(Long idObject, String category) {
        List<ProposeDto> proposeDtoList = new ArrayList<>();
        iProposeRepository.findAllByCategoryAndObjectId(category,idObject).stream().forEach(data -> {
            ProposeDto proposeDto = ProposeDto.toDto(data);
            proposeDto.setCommentDtos(iCommentRepository.findAllByCategoryAndObjectIdAndIdParent(Const.tableName.PROPOSE.name(), data.getId(), null).stream().map(cm -> CommentDto.toDto(cm)).collect(Collectors.toList()));
            proposeDtoList.add(proposeDto);
        });
        return proposeDtoList;
    }
}
