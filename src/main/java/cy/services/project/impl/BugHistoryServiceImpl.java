package cy.services.project.impl;

import cy.dtos.project.BugHistoryDto;
import cy.dtos.project.FileDto;
import cy.entities.project.BugEntity;
import cy.entities.project.BugHistoryEntity;
import cy.entities.project.FileEntity;
import cy.models.project.BugHistoryModel;
import cy.models.project.FileModel;
import cy.repositories.project.IBugHistoryRepository;
import cy.repositories.project.IBugRepository;
import cy.repositories.project.IFileRepository;
import cy.services.project.IBugHistoryService;
import cy.services.project.IFileService;
import cy.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

        Date now = Date.from(Instant.now());

        BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
        bugHistoryEntity.setBugId(model.getBugId());
        bugHistoryEntity.setStartDate(now);
        bugHistoryEntity.setEndDate(null);

        BugHistoryDto bugHistoryEntityAfterSave = BugHistoryDto.entityToDto(iBugHistoryRepository.save(bugHistoryEntity));
        for (MultipartFile file : model.getFiles()) {
            FileModel fileModel = new FileModel();
            fileModel.setFile(file);
            fileModel.setObjectId(bugHistoryEntityAfterSave.getId());
            fileModel.setCategory(Const.tableName.BUG_HISTORY.name());
            iFileService.add(fileModel);
        }
        BugEntity bugEntity = iBugRepository.findById(model.getBugId()).get();
        bugEntity.setStatus(Const.status.TO_DO.name());
        iBugRepository.save(bugEntity);
        return bugHistoryEntityAfterSave;
    }

    @Override
    public List<BugHistoryDto> add(List<BugHistoryModel> model) {
        return null;
    }

    @Override
    public BugHistoryDto update(BugHistoryModel model) {

        // delete old file
        iFileRepository.deleteByCategoryAndObjectId(Const.tableName.BUG_HISTORY.name(), model.getId());

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

        iBugHistoryRepository.flush();
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
