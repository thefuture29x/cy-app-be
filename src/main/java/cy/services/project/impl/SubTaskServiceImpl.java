package cy.services.project.impl;

import cy.dtos.project.SubTaskDto;
import cy.entities.project.SubTaskEntity;
import cy.models.project.SubTaskModel;
import cy.services.project.ISubTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTaskServiceImpl implements ISubTaskService {

    @Override
    public List<SubTaskDto> findAll() {
        return null;
    }

    @Override
    public Page<SubTaskDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<SubTaskDto> findAll(Specification<SubTaskEntity> specs) {
        return null;
    }

    @Override
    public Page<SubTaskDto> filter(Pageable page, Specification<SubTaskEntity> specs) {
        return null;
    }

    @Override
    public SubTaskDto findById(Long id) {
        return null;
    }

    @Override
    public SubTaskEntity getById(Long id) {
        return null;
    }

    @Override
    public SubTaskDto add(SubTaskModel model) {

        return null;
    }

    @Override
    public List<SubTaskDto> add(List<SubTaskModel> model) {
        return null;
    }

    @Override
    public SubTaskDto update(SubTaskModel model) {
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
