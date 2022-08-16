package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestDeviceDto;
import cy.entities.RequestDeviceEntity;
import cy.entities.UserEntity;
import cy.models.RequestDeviceModel;
import cy.repositories.IRequestDeviceRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestDeviceService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestDeviceServiceImpl implements IRequestDeviceService {
    @Autowired
    IRequestDeviceRepository iRequestDeviceRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Autowired
    IUserRepository userRepository;

    @Override
    public List<RequestDeviceDto> findAll() {
        return iRequestDeviceRepository.findAll().stream().map(data -> RequestDeviceDto.entityToDto(data)).collect(Collectors.toList());
    }

    @Override
    public Page<RequestDeviceDto> findAll(Pageable page) {
        return iRequestDeviceRepository.findAll(page).map(data -> RequestDeviceDto.entityToDto(data));
    }

    @Override
    public List<RequestDeviceDto> findAll(Specification<RequestDeviceEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestDeviceDto> filter(Pageable page, Specification<RequestDeviceEntity> specs) {
        return null;
    }

    @Override
    public RequestDeviceDto findById(Long id) {
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public RequestDeviceEntity getById(Long id) {
        return this.iRequestDeviceRepository.findById(id).orElseThrow(()->new CustomHandleException(11));
    }

    @Override
    public RequestDeviceDto add(RequestDeviceModel model)  {
        RequestDeviceEntity requestDeviceEntity = model.modelToEntity(model);
        requestDeviceEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());

        UserEntity assignUser = userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11));

        requestDeviceEntity.setAssignTo(assignUser);
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                requestDeviceEntity.setFiles(fileUploadProvider.uploadFile("device", model.getFiles()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        RequestDeviceDto requestDeviceDto = RequestDeviceDto.entityToDto(requestDeviceEntity);
        return requestDeviceDto;
    }

    @Override
    public List<RequestDeviceDto> add(List<RequestDeviceModel> model) {
        return null;
    }

    @Override
    public RequestDeviceDto update(RequestDeviceModel model){
        RequestDeviceEntity requestDeviceEntity = model.modelToEntity(model);
        requestDeviceEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
        if (model.getAssignTo() == null) {
            requestDeviceEntity.setAssignTo(null);

        } else {
            UserEntity assignUser = userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11));
            requestDeviceEntity.setAssignTo(assignUser);
        }
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                requestDeviceEntity.setFiles(fileUploadProvider.uploadFile("device", model.getFiles()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        RequestDeviceDto requestDeviceDto = RequestDeviceDto.entityToDto(requestDeviceEntity);
        return requestDeviceDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            iRequestDeviceRepository.deleteById(id);
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
