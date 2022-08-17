package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.HistoryRequestDto;
import cy.entities.HistoryRequestEntity;
import cy.models.HistoryRequestModel;
import cy.repositories.IHistoryRequestRepository;
import cy.services.IHistoryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
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
    public HistoryRequestEntity getById(Long id) {
        return this.iHistoryRequestRepository.findById(id).orElseThrow(()-> new CustomHandleException(99999));
    }

    @Override
    public HistoryRequestDto add(HistoryRequestModel model) {
        HistoryRequestEntity historyRequestEntity = new HistoryRequestEntity();
        historyRequestEntity.setStatus(model.getStatus());
        if (model.getDateHistory() != null){
            historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(new Date()));
            historyRequestEntity.setDateHistory(model.getDateHistory());
        }
        HistoryRequestDto historyRequestDto = HistoryRequestDto.toDto(iHistoryRequestRepository.save(historyRequestEntity));
        return historyRequestDto;
    }

    @Override
    public List<HistoryRequestDto> add(List<HistoryRequestModel> model) {
        List<HistoryRequestDto> historyRequestDtos = new ArrayList<>();

        for (HistoryRequestModel historyRequestModel : model){
            HistoryRequestDto historyRequestDto = add(historyRequestModel);
            if (historyRequestDto != null){
                historyRequestDtos.add(historyRequestDto);
            }
        }
        return historyRequestDtos;
    }

    @Override
    public HistoryRequestDto update(HistoryRequestModel model) {
        HistoryRequestEntity historyRequestEntity = iHistoryRequestRepository.findById(model.getId()).orElse(null);
        if (historyRequestEntity == null){
            return null;
        }else {
            historyRequestEntity.setStatus(model.getStatus());
            if (model.getDateHistory() != null){
                historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(model.getDateHistory()));
                historyRequestEntity.setDateHistory(model.getDateHistory());
            }
            HistoryRequestDto historyRequestDto = HistoryRequestDto.toDto(iHistoryRequestRepository.save(historyRequestEntity));
            return historyRequestDto;
        }
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
        return iHistoryRequestRepository.deleteByIds(ids);
    }
}
