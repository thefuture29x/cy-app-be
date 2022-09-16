package cy.services.project.impl;

import cy.dtos.project.BugHistoryDto;
import cy.entities.project.BugHistoryEntity;
import cy.models.project.BugHistoryModel;
import cy.repositories.project.BugHistoryRepository;
import cy.services.project.IBugHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BugHistoryServiceImpl implements IBugHistoryService {
    @Autowired
    BugHistoryRepository bugHistoryRepository;
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
        return null;
    }

    @Override
    public List<BugHistoryDto> add(List<BugHistoryModel> model) {
        return null;
    }

    @Override
    public BugHistoryDto update(BugHistoryModel model) {
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
