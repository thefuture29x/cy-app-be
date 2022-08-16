package cy.services.impl;

import cy.dtos.HistoryRequestDto;
import cy.entities.HistoryRequestEntity;
import cy.models.HistoryRequestModel;
import cy.repositories.IHistoryRequestRepository;
import cy.services.IHistoryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class HistoryRequestServiceImpl implements IHistoryRequestService {
    @Autowired
    IHistoryRequestRepository iHistoryRequestRepository;

    @Override
    public HistoryRequestDto saveOrUpdate(HistoryRequestModel historyRequestModel) {
        return null;
    }

    @Override
    public List<HistoryRequestDto> findAll() {
        return null;
    }

    @Override
    public Page<HistoryRequestDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<HistoryRequestDto> findAll(Specification<HistoryRequestEntity> specs) {
        return null;
    }

    @Override
    public Page<HistoryRequestDto> filter(Pageable page, Specification<HistoryRequestEntity> specs) {
        return null;
    }

    @Override
    public HistoryRequestDto findById(Long id) {
        HistoryRequestEntity historyRequestEntity = iHistoryRequestRepository.findById(id).orElse(null);
        if (historyRequestEntity != null){
            return null;
        }else {
            return null;
        }
    }

    @Override
    public HistoryRequestDto add(HistoryRequestModel model) {
        return null;
    }

    @Override
    public List<HistoryRequestDto> add(List<HistoryRequestModel> model) {
        return null;
    }

    @Override
    public HistoryRequestDto update(HistoryRequestModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        HistoryRequestEntity historyRequestEntity = iHistoryRequestRepository.findById(id).orElse(null);
        if (historyRequestEntity != null){
            iHistoryRequestRepository.deleteById(id);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {

        return false;
    }
}
