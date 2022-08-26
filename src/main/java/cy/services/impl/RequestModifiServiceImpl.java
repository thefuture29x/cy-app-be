package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestModifiDto;
import cy.dtos.ResponseDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import cy.entities.RequestModifiEntity;
import cy.entities.UserEntity;
import cy.models.HistoryRequestModel;
import cy.dtos.*;
import cy.entities.*;
import cy.models.AcceptRequestModifiModel;
import cy.models.RequestModifiModel;
import cy.repositories.*;
import cy.services.IRequestModifiService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestModifiServiceImpl implements IRequestModifiService {
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
    @Autowired
    INotificationRepository iNotificationRepository;

    @Autowired
    private IRequestAttendRepository requestAttendRepository;

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
    public RequestModifiDto add(RequestModifiModel model){
        RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(model);
        requestModifiEntity.setCreateBy(iUserRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        requestModifiEntity.setAssignTo(iUserRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        List<String> s3Urls = new ArrayList<>();
        if(model.getFiles() != null && model.getFiles().length > 0){
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("requestModifi",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    s3Urls.add(result);
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
            requestModifiEntity.setFiles(jsonObject.toString());
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
            createHistory(requestModifiEntity,1);
            createNotification(requestModifiEntity,true,"Yêu cầu sửa đổi thông tin chấm công","Yêu cầu thay đổi thông tin chấm công đã được tạo bởi " + requestModifiEntity.getCreateBy().getFullName() );
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
        historyRequestEntity.setDateHistory(new Date());
        historyRequestEntity.setStatus(0);
        historyRequestEntity.setTimeHistory(new SimpleDateFormat("HH:ss").format(new Date()));

        RequestModifiEntity requestModifiEntity = RequestModifiModel.toEntity(requestModifiModel);
        if (requestModifiModel.getCreateBy() == null){
            return null;
        }
        UserEntity userEntity = iUserRepository.findById(requestModifiModel.getCreateBy()).orElse(null);
        if (userEntity == null ||userEntity.getStatus() == false){
            return null;
        }
        requestModifiEntity.setCreateBy(userEntity);

        if (requestModifiModel.getAssignTo() == null ){
            return null;
        }
        UserEntity assignTo = iUserRepository.findById(requestModifiModel.getAssignTo()).orElse(null);
        if (assignTo == null ||userEntity.getStatus() == false){
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
        requestModifiEntity.getHistoryRequestEntities().add(historyRequestEntity);
        requestModifiEntity.setDateRequestModifi(new Date());


        iHistoryRequestRepository.save(historyRequestEntity);

        return RequestModifiDto.toDto(iRequestModifiRepository.save(requestModifiEntity));
    }

    @Override
    public RequestAttendDto checkAttend(Date date, Long idUser) {
        RequestAttendEntity requestAttendEntity = iRequestAttendRepository.checkAttend(date, idUser);
        if (requestAttendEntity == null){
            return null;
        }
        return RequestAttendDto.entityToDto(requestAttendEntity);
    }

    public void createHistory(RequestModifiEntity requestModifiEntity,int status){
        HistoryRequestEntity historyRequest=new HistoryRequestEntity();
        historyRequest.setDateHistory(new Date());
        historyRequest.setTimeHistory(LocalTime.now().toString().substring(0,5));
        historyRequest.setStatus(status);
        historyRequest.setRequestModifi(requestModifiEntity);
        iHistoryRequestRepository.save(historyRequest);
    }
    public void createNotification(RequestModifiEntity requestModifiEntity,Boolean isRead,String title,String content){
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setDateNoti(new java.util.Date());
        notificationEntity.setUserId(SecurityUtils.getCurrentUser().getUser());
        notificationEntity.setIsRead(isRead);
        notificationEntity.setTitle(title);
        notificationEntity.setContent(content);
        notificationEntity.setRequestModifi(requestModifiEntity);
        iNotificationRepository.save(notificationEntity);
    }

    @Override
    public RequestModifiDto updateStatus(AcceptRequestModifiModel acceptRequestModifiModel) {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        RequestModifiEntity requestModifiEntity = this.getById(acceptRequestModifiModel.getId());
        if(userEntity.getUserId()==requestModifiEntity.getAssignTo().getUserId()){
            switch (acceptRequestModifiModel.getCaseSwitch()){
                case 1:
                    requestModifiEntity.setStatus(1);
                    requestModifiEntity.setReasonCancel(null);
                    iRequestModifiRepository.saveAndFlush(requestModifiEntity);
                    createHistory(requestModifiEntity,1);
                    createNotification(requestModifiEntity,true,"Yêu cầu sửa đổi thông tin chấm công","Yêu cầu thay đổi thông tin chấm công đã được xét duyệt bởi "+userEntity.getFullName());
                    // update date-timeStart and timeEnd to request Attend
                    Long userRequestId = requestModifiEntity.getCreateBy().getUserId();
                    RequestAttendEntity oldRequestAttend = this.iRequestAttendRepository.checkAttend(requestModifiEntity.getDateRequestModifi(), userRequestId);
                    if (oldRequestAttend == null){
                        oldRequestAttend = new RequestAttendEntity();
                        oldRequestAttend.setDateRequestAttend((java.sql.Date) requestModifiEntity.getDateRequestModifi());
                        oldRequestAttend.setTimeCheckIn(requestModifiEntity.getTimeStart());
                        oldRequestAttend.setTimeCheckOut(requestModifiEntity.getTimeEnd());
                        oldRequestAttend.setCreateBy(requestModifiEntity.getCreateBy());
                        oldRequestAttend.setStatus(1);
                    }else {
                        oldRequestAttend.setDateRequestAttend((java.sql.Date) requestModifiEntity.getDateRequestModifi());
                        oldRequestAttend.setTimeCheckIn(requestModifiEntity.getTimeStart());
                        oldRequestAttend.setTimeCheckOut(requestModifiEntity.getTimeEnd());
                    }
                    this.iRequestAttendRepository.saveAndFlush(oldRequestAttend);
                    break;
                case 2:
                    requestModifiEntity.setStatus(2);
                    requestModifiEntity.setReasonCancel(acceptRequestModifiModel.getReason());
                    iRequestModifiRepository.saveAndFlush(requestModifiEntity);
                    createHistory(requestModifiEntity,2);
                    createNotification(requestModifiEntity,true,"Yêu cầu sửa đổi thông tin chấm công","Yêu cầu thay đổi thông tin chấm công đã bị từ chối bởi "+userEntity.getFullName());
                    break;
            }

        }else {
            throw new CustomHandleException(49);
        }
        return RequestModifiDto.toDto(iRequestModifiRepository.findById(acceptRequestModifiModel.getId()).orElseThrow(() -> new CustomHandleException(11)));
    }
}
