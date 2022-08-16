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
    public List<RequestModifiModel> findAll() {
        return null;
    }

    @Override
    public Page<RequestModifiModel> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<RequestModifiModel> findAll(Specification<RequestModifiEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestModifiModel> filter(Pageable page, Specification<RequestModifiEntity> specs) {
        return null;
    }

    @Override
    public RequestModifiModel findById(Long id) {
        return null;
    }

    @Override
    public RequestModifiModel add(RequestModifiDto model) {
        return null;
    }

    @Override
    public List<RequestModifiModel> add(List<RequestModifiDto> model) {
        return null;
    }

    @Override
    public RequestModifiModel update(RequestModifiDto model) {
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
