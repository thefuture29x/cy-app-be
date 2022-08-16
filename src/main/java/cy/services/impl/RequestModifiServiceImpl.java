package cy.services.impl;

import cy.dtos.RequestModifiDto;
import cy.entities.RequestModifiEntity;
import cy.models.RequestModifiModel;
import cy.services.IResquestModifiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestModifiServiceImpl implements IResquestModifiService {

    @Override
    public List<RequestModifiDto> findAll() {
        return null;
    }

    @Override
    public Page<RequestModifiDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<RequestModifiDto> findAll(Specification<RequestModifiEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestModifiDto> filter(Pageable page, Specification<RequestModifiEntity> specs) {
        return null;
    }

    @Override
    public RequestModifiDto findById(Long id) {
        return null;
    }

    @Override
    public RequestModifiDto add(RequestModifiModel model) {
        return null;
    }

    @Override
    public List<RequestModifiDto> add(List<RequestModifiModel> model) {
        return null;
    }

    @Override
    public RequestModifiDto update(RequestModifiModel model) {
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
