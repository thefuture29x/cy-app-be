package cy.services.attendance.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.HistoryRequestDto;
import cy.entities.attendance.HistoryRequestEntity;
import cy.models.attendance.HistoryRequestModel;
import cy.repositories.attendance.IHistoryRequestRepository;
import cy.services.attendance.IHistoryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        List<HistoryRequestEntity> historyRequestEntities = iHistoryRequestRepository.findAll();
        if (historyRequestEntities != null && historyRequestEntities.size() > 0){
            List<HistoryRequestDto> historyRequestDtos = historyRequestEntities.stream().map(x->HistoryRequestDto.toDto(x)).collect(Collectors.toList());
            return historyRequestDtos;
        }
        return null;
    }

    @Override
    public Page<HistoryRequestDto> findAll(Pageable page) {
        Page<HistoryRequestEntity> historyRequestEntities = iHistoryRequestRepository.findAll(page);
        Page<HistoryRequestDto> historyRequestDtos = null;
        if (historyRequestEntities !=null && historyRequestEntities.getTotalElements() > 0){
            historyRequestDtos = historyRequestEntities.map(x->HistoryRequestDto.toDto(x));
        }
        return historyRequestDtos;
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
            return HistoryRequestDto.toDto(historyRequestEntity);
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
        historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(new Date()));
        historyRequestEntity.setDateHistory(new Date());
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
            historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(new Date()));
            historyRequestEntity.setDateHistory(new Date());
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
