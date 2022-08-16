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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = {Exception.class, Throwable.class})
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
        return requestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
    }

    @Override
    public RequestOTDto add(RequestOTModel model) {
        RequestOTEntity requestOTEntity = RequestOTModel.toEntity(model);
        if (model.getCreateBy() != null) {
            requestOTEntity.setCreateBy(userRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getAssignTo() != null) {
            requestOTEntity.setAssignTo(userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                String path = fileUploadProvider.uploadFile("request-ot", model.getFiles());
                requestOTEntity.setFiles(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return RequestOTDto.toDto(requestOTRepository.save(requestOTEntity));
    }

    @Override
    public List<RequestOTDto> add(List<RequestOTModel> model) {
        List<RequestOTDto> requestOTDtoList = new ArrayList<>();
        for (RequestOTModel requestOTModel: model){
            RequestOTEntity requestOTEntity = RequestOTModel.toEntity(requestOTModel);
            if (requestOTModel.getCreateBy() != null) {
                requestOTEntity.setCreateBy(userRepository.findById(requestOTModel.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
            }
            if (requestOTModel.getAssignTo() != null) {
                requestOTEntity.setAssignTo(userRepository.findById(requestOTModel.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
            }
            if (requestOTModel.getFiles() != null && !requestOTModel.getFiles().isEmpty()) {
                try {
                    String path = fileUploadProvider.uploadFile("request-ot", requestOTModel.getFiles());
                    requestOTEntity.setFiles(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            requestOTDtoList.add(RequestOTDto.toDto(requestOTRepository.save(requestOTEntity)));
        }
        return requestOTDtoList;
    }

    @Override
    public RequestOTDto update(RequestOTModel model) {
        RequestOTEntity requestOTEntity = requestOTRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11));
        requestOTEntity = RequestOTModel.toEntity(model);

        if (model.getCreateBy() != null) {
            requestOTEntity.setCreateBy(userRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getAssignTo() != null) {
            requestOTEntity.setAssignTo(userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                String path = fileUploadProvider.uploadFile("request-ot", model.getFiles());
                requestOTEntity.setFiles(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return RequestOTDto.toDto(requestOTRepository.save(requestOTEntity));
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            requestOTRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        try {
            for (Long id: ids)
                requestOTRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
