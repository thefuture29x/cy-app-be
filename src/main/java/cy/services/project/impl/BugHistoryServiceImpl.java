package cy.services.project.impl;

import cy.dtos.project.BugHistoryDto;
import cy.entities.common.UserEntity;
import cy.entities.project.BugEntity;
import cy.entities.project.BugHistoryEntity;
import cy.entities.common.HistoryEntity;
import cy.models.project.BugHistoryModel;
import cy.models.common.FileModel;
import cy.repositories.project.IBugHistoryRepository;
import cy.repositories.project.IBugRepository;
import cy.repositories.common.IFileRepository;
import cy.repositories.common.IHistoryLogRepository;
import cy.services.project.IBugHistoryService;
import cy.services.common.IFileService;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class BugHistoryServiceImpl implements IBugHistoryService {
    @Autowired
    IBugHistoryRepository iBugHistoryRepository;
    @Autowired
    IBugRepository iBugRepository;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IFileService iFileService;
    @Autowired
    IHistoryLogRepository iHistoryLogRepository;

    @Override
    public List<BugHistoryDto> findAll() {
        return null;
    }

    @Override
    public Page<BugHistoryDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<BugHistoryDto> findAll(Specification<BugHistoryEntity> specs) {
        return null;
    }

    @Override
    public Page<BugHistoryDto> filter(Pageable page, Specification<BugHistoryEntity> specs) {
        return null;
    }

    @Override
    public BugHistoryDto findById(Long id) {
        return null;
    }

    @Override
    public BugHistoryEntity getById(Long id) {
        return null;
    }

    @Override
    public BugHistoryDto add(BugHistoryModel model) {
        BugEntity bugEntity = iBugRepository.findById(model.getBugId()).get();
        bugEntity.setStatus(Const.status.TO_DO.name());
        iBugRepository.save(bugEntity);

        Date now = Date.from(Instant.now());
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();

        BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
        bugHistoryEntity.setBugId(model.getBugId());
        bugHistoryEntity.setStartDate(now);
        bugHistoryEntity.setEndDate(null);
        bugHistoryEntity.setDetail(model.getDetail());
        bugHistoryEntity.setDeadLine(model.getDeadLine());
        bugHistoryEntity.setTimeEstimate(model.getTimeEstimate());
        bugHistoryEntity.setDeadLine(model.getDeadLine());

        BugHistoryDto bugHistoryEntityAfterSave = BugHistoryDto.entityToDto(iBugHistoryRepository.save(bugHistoryEntity));
        if (model.getFiles() != null){
            for (MultipartFile file : model.getFiles()) {
                FileModel fileModel = new FileModel();
                fileModel.setFile(file);
                fileModel.setObjectId(bugHistoryEntityAfterSave.getId());
                fileModel.setCategory(Const.tableName.BUG_HISTORY.name());
                iFileService.add(fileModel);
            }
        }




        HistoryEntity newHistoryEntity = HistoryEntity
                .builder()
                .id(null)
                .ObjectId(model.getBugId())
                .category(Const.tableName.BUG.name())
                .userId(userEntity)
                .content("<p> đã mở lại bug <b>" + iBugRepository.findById(model.getBugId()).get().getName() +"</b></p>")
                .build();
        iHistoryLogRepository.saveAndFlush(newHistoryEntity);

        return bugHistoryEntityAfterSave;
    }

    @Override
    public List<BugHistoryDto> add(List<BugHistoryModel> model) {
        return null;
    }

    @Override
    public BugHistoryDto update(BugHistoryModel model) {

        // delete old file
//        iFileRepository.deleteByCategoryAndObjectId(Const.tableName.BUG_HISTORY.name(), model.getId());
        if (model.getFileUrlsKeeping() != null){
            iFileRepository.deleteFileExistInObject(model.getFileUrlsKeeping(), Const.tableName.BUG_HISTORY.name(), model.getId());
        }else {
            iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.BUG_HISTORY.name(), model.getId());
        }
        // save file
        if(model.getFiles( ) != null && model.getFiles().length > 0){
            for (MultipartFile file : model.getFiles()) {
                FileModel fileModel = new FileModel();
                fileModel.setFile(file);
                fileModel.setObjectId(model.getId());
                fileModel.setCategory(Const.tableName.BUG_HISTORY.name());
                iFileService.add(fileModel);
            }
        }
        BugHistoryEntity bugHistoryEntity = iBugHistoryRepository.findById(model.getId()).get();
        bugHistoryEntity.setDetail(model.getDetail());
        bugHistoryEntity.setStartDate(model.getStartDate());
        bugHistoryEntity.setDeadLine(model.getDeadLine());
        bugHistoryEntity.setTimeEstimate(model.getTimeEstimate());

//        iBugHistoryRepository.updateDetailHistoryBug(model.getDetail(),model.getId());
        iBugHistoryRepository.saveAndFlush(bugHistoryEntity);
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
