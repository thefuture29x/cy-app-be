package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestModifiDto;
import cy.dtos.ResponseDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import cy.entities.RequestModifiEntity;
import cy.entities.UserEntity;
import cy.models.RequestModifiModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.IRequestAttendRepository;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestModifiServiceImpl implements IResquestModifiService {
    @Autowired
    IRequestModifiRepository iRequestModifiRepository;
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IHistoryRequestRepository iHistoryRequestRepository;

    @Autowired
    IRequestAttendRepository iRequestAttendRepository;

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
        return this.iRequestModifiRepository.findById(id).orElseThrow(()->new CustomHandleException(11));
    }

    @Override
    public RequestModifiDto add(RequestModifiModel model) {
        RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(model);
        requestModifiEntity.setCreateBy(iUserRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestModifiEntity.setAssignTo(iUserRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        if(model.getFiles() != null && model.getFiles().length > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
    public List<RequestModifiDto> add(List<RequestModifiModel> model)  {
        List<RequestModifiDto> requestModifiDtoList = new ArrayList<>();
        for (RequestModifiModel requestModifiModel : model) {
            RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(requestModifiModel);
            requestModifiEntity.setCreateBy(iUserRepository.findById(requestModifiModel.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
            requestModifiEntity.setAssignTo(iUserRepository.findById(requestModifiModel.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
            if(requestModifiModel.getFiles() != null && requestModifiModel.getFiles().length > 0){
                List<String> files = new ArrayList<>();
                for(MultipartFile fileMultipart : requestModifiModel.getFiles()){
                    if(!fileMultipart.isEmpty()){
                        String result = null;
                        try {
                            result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
    public RequestModifiDto update(RequestModifiModel model)  {
        RequestModifiEntity requestModifiEntity = iRequestModifiRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11));
        requestModifiEntity = RequestModifiModel.toEntity(model);
        requestModifiEntity.setCreateBy(iUserRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestModifiEntity.setAssignTo(iUserRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        if(model.getFiles() != null && model.getFiles().length > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

    @Override
    public RequestModifiDto sendResquestModifi(RequestModifiModel requestModifiModel) {

        HistoryRequestEntity historyRequestEntity = new HistoryRequestEntity();
        historyRequestEntity.setDateHistory(requestModifiModel.getDateRequestModifi());
        historyRequestEntity.setStatus(0);
        if (requestModifiModel.getDateRequestModifi() != null){
            historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(new Date()));
            historyRequestEntity.setDateHistory(requestModifiModel.getDateRequestModifi());
        }

        RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(requestModifiModel);
        if (requestModifiModel.getCreateBy() == null){
            return null;

        }
        UserEntity userEntity = iUserRepository.findById(requestModifiModel.getCreateBy()).orElse(null);
        if (userEntity == null){
            return null;
        }
        requestModifiEntity.setCreateBy(userEntity);

        if (requestModifiModel.getAssignTo() == null){
            return null;
        }
        UserEntity assignTo = iUserRepository.findById(requestModifiModel.getAssignTo()).orElse(null);
        if (assignTo == null){
            return null;
        }
        requestModifiEntity.setAssignTo(assignTo);

        if(requestModifiModel.getFiles() != null && requestModifiModel.getFiles().length > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : requestModifiModel.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    files.add(result);
                }
            }
            requestModifiEntity.setFiles(files.toString());
        }

        historyRequestEntity.setRequestModifi(requestModifiEntity);
        if(requestModifiModel.getHistoryRequestModels() == null){
            requestModifiEntity.setHistoryRequestEntities(new ArrayList<>());
        };


        iHistoryRequestRepository.save(historyRequestEntity);

        return RequestModifiDto.toDto(iRequestModifiRepository.save(requestModifiEntity));
    }

    @Override
    public RequestAttendDto checkAttend(Date date, Long idUser) {
        RequestAttendEntity requestAttendEntity = iRequestAttendRepository.checkAttend(date,idUser);
        if (requestAttendEntity == null){
            return null;
        }
        return RequestAttendDto.entityToDto(requestAttendEntity);
    }
}
