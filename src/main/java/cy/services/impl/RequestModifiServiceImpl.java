package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestModifiDto;
import cy.dtos.ResponseDto;
import cy.entities.RequestModifiEntity;
import cy.models.RequestModifiModel;
import cy.repositories.IRequestModifiRepository;
import cy.repositories.IUserRepository;
import cy.services.IResquestModifiService;
import cy.utils.FileUploadProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestModifiServiceImpl implements IResquestModifiService {
    @Autowired
    IRequestModifiRepository iRequestModifiRepository;
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Override
    public List<RequestModifiDto> findAll() {
        return iRequestModifiRepository.findAll().stream().map(data -> RequestModifiDto.toDto(data)).collect(Collectors.toList());
    }

    @Override
    public Page<RequestModifiDto> findAll(Pageable page) {
        return iRequestModifiRepository.findAll(page).map(data -> RequestModifiDto.toDto(data));
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
        return RequestModifiDto.toDto(iRequestModifiRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public RequestModifiEntity getById(Long id) {
        return null;
    }

    @Override
    public RequestModifiDto add(RequestModifiModel model) throws IOException {
        RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(model);
        requestModifiEntity.setCreateBy(iUserRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestModifiEntity.setAssignTo(iUserRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        if(model.getFiles() != null && model.getFiles().length > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    files.add(result);
                }
            }
            requestModifiEntity.setFiles(files.toString());
        }
        // there is no dto of the history request, so leave it null for now
        requestModifiEntity.setHistoryRequestEntities(null);
        return RequestModifiDto.toDto(iRequestModifiRepository.save(requestModifiEntity));
    }

    @Override
    public List<RequestModifiDto> add(List<RequestModifiModel> model) throws IOException {
        List<RequestModifiDto> requestModifiDtoList = new ArrayList<>();
        for (RequestModifiModel requestModifiModel : model) {
            RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(requestModifiModel);
            requestModifiEntity.setCreateBy(iUserRepository.findById(requestModifiModel.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
            requestModifiEntity.setAssignTo(iUserRepository.findById(requestModifiModel.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
            if(requestModifiModel.getFiles() != null && requestModifiModel.getFiles().length > 0){
                List<String> files = new ArrayList<>();
                for(MultipartFile fileMultipart : requestModifiModel.getFiles()){
                    if(!fileMultipart.isEmpty()){
                        String result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                        files.add(result);
                    }
                }
                requestModifiEntity.setFiles(files.toString());
            }
            // there is no dto of the history request, so leave it null for now
            requestModifiEntity.setHistoryRequestEntities(null);
            requestModifiDtoList.add(RequestModifiDto.toDto(iRequestModifiRepository.save(requestModifiEntity)));
        }
        return requestModifiDtoList;
    }

    @Override
    public RequestModifiDto update(RequestModifiModel model) throws IOException {
        RequestModifiEntity requestModifiEntity = iRequestModifiRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11));
        requestModifiEntity = RequestModifiModel.toEntity(model);
        requestModifiEntity.setCreateBy(iUserRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestModifiEntity.setAssignTo(iUserRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        if(model.getFiles() != null && model.getFiles().length > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    files.add(result);
                }
            }
            requestModifiEntity.setFiles(files.toString());
        }
        // there is no dto of the history request, so leave it null for now
        requestModifiEntity.setHistoryRequestEntities(null);
        return RequestModifiDto.toDto(iRequestModifiRepository.save(requestModifiEntity));
    }

    @Override
    public boolean deleteById(Long id) {
        iRequestModifiRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            iRequestModifiRepository.deleteById(id);
        }
        return true;
    }
}
