package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.entities.project.BugHistoryEntity;
import cy.models.project.BugHistoryModel;
import cy.models.project.BugModel;
import cy.repositories.project.BugHistoryRepository;
import cy.repositories.project.BugRepository;
import cy.services.project.IRequestBugService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BugServiceImpl implements IRequestBugService {
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    BugHistoryRepository bugHistoryRepository;
    @Autowired
    SubTaskRepository subTaskRepository;
    @Autowired
    BugRepository bugRepository;

    @Override
    public List<BugDto> findAll() {
        return null;
    }

    @Override
    public Page<BugDto> findAll(Pageable page) {
        return bugRepository.findAll(page).map(data -> BugDto.entityToDto(data));
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
        return BugDto.entityToDto(bugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public BugEntity getById(Long id) {
        return null;
    }

    @Override
    public BugDto add(BugModel model) {
        BugEntity bugEntity = model.modelToEntity(model);
        bugEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
        bugRepository.saveAndFlush(bugEntity);
        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //chuyển trạng thái Subtask sang fixBug


        //Lưu dữ liệu vào bảng BugHistory
        BugHistoryModel bugHistoryModel = null;
        bugHistoryModel.setBugId(bugEntity.getId());
        bugHistoryModel.setStartDate(bugEntity.getStartDate());
        bugHistoryModel.setEndDate(bugEntity.getEndDate());
        BugHistoryEntity bugHistoryEntity = bugHistoryModel.modelToEntity(bugHistoryModel);
        bugHistoryEntity.setBugId(bugEntity);
        bugHistoryRepository.saveAndFlush(bugHistoryEntity);


        return bugDto;
    }

    @Override
    public List<BugDto> add(List<BugModel> model) {
        return null;
    }

    @Override
    public BugDto update(BugModel model) {
        return null;
    }
    /*
    *@author:HieuMM_Cy
    *@since:9/16/2022-8:44 AM
    *@description:Bug Done
    *@update:
    **/
    public BugDto updateStatus(Long id) {
        BugEntity bugEntity = bugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask sang inreview
        bugRepository.saveAndFlush(bugEntity);
        BugDto bugDto = BugDto.entityToDto(bugEntity);
        return bugDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            bugRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
