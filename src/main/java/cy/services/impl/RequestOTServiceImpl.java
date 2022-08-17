package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestOTDto;
import cy.entities.RequestOTEntity;
import cy.models.RequestOTModel;
import cy.repositories.IRequestOTRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestOTService;
import cy.utils.FileUploadProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestOTServiceImpl implements IRequestOTService {
    @Autowired
    private IRequestOTRepository requestOTRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    FileUploadProvider fileUploadProvider;

    @Override
    public List<RequestOTDto> findAll() {
        return requestOTRepository.findAll().stream().map(requestOTEntity -> RequestOTDto.toDto(requestOTEntity)).collect(Collectors.toList());
    }

    @Override
    public Page<RequestOTDto> findAll(Pageable page) {
        return requestOTRepository.findAll(page).map(requestOTEntity -> RequestOTDto.toDto(requestOTEntity));
    }

    @Override
    public List<RequestOTDto> findAll(Specification<RequestOTEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestOTDto> filter(Pageable page, Specification<RequestOTEntity> specs) {
        return null;
    }

    @Override
    public RequestOTDto findById(Long id) {
        return RequestOTDto.toDto(requestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public RequestOTEntity getById(Long id) {
        return this.requestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(999999));
    }

    @Override
    public RequestOTDto add(RequestOTModel model) {
        RequestOTEntity requestOTEntity = RequestOTModel.toEntity(model);
        requestOTEntity.setCreateBy(userRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestOTEntity.setAssignTo(userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {

        }
        return null;
    }

    @Override
    public List<RequestOTDto> add(List<RequestOTModel> model) {
        return null;
    }

    @Override
    public RequestOTDto update(RequestOTModel model) {
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
