package cy.services.impl;

import cy.dtos.PayRollDto;
import cy.entities.PayRollEntity;
import cy.models.PayRollModel;
import cy.repositories.IPayRollRepository;
import cy.services.IPayRollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PayRollServiceImpl implements IPayRollService {
    @Autowired
    IPayRollRepository iPayRollRepository;

    @Override
    public List<PayRollDto> findAll() {
        return null;
    }

    @Override
    public Page<PayRollDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<PayRollDto> findAll(Specification<PayRollEntity> specs) {
        return null;
    }

    @Override
    public Page<PayRollDto> filter(Pageable page, Specification<PayRollEntity> specs) {
        return null;
    }

    @Override
    public PayRollDto findById(Long id) {
        return null;
    }

    @Override
    public PayRollEntity getById(Long id) {
        return null;
    }

    @Override
    public PayRollDto add(PayRollModel model) {
        return null;
    }

    @Override
    public List<PayRollDto> add(List<PayRollModel> model) {
        return null;
    }

    @Override
    public PayRollDto update(PayRollModel model) {
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

    @Override
    public Page<PayRollDto> getPayRollByMonthAndYear(int month, int year,Pageable pageable) {
        return iPayRollRepository.getAllByMonthAndYear(month, year, pageable).map(data -> PayRollDto.entityToDto(data));
    }
}
