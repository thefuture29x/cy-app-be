package cy.services.impl;

import cy.dtos.*;

import cy.entities.HistoryRequestEntity;
import cy.entities.UserEntity;
import cy.models.CreateUpdateRequestAttend;
import cy.entities.RequestAttendEntity;
import cy.models.CreateUpdateRequestAttend;

import cy.entities.*;
import cy.models.CreateUpdateRequestAttend;
import cy.entities.UserEntity;
import cy.models.CreateUpdateRequestAttend;
import cy.models.NotificationModel;
import cy.models.RequestAttendModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.INotificationRepository;
import cy.repositories.IRequestAttendRepository;
import cy.repositories.IUserRepository;
import cy.services.INotificationService;
import cy.services.IRequestAttendService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

@org.springframework.transaction.annotation.Transactional
@Service
public class RequestAttendServiceImpl implements IRequestAttendService {
    @Autowired
    private FileUploadProvider fileUploadProvider;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRequestAttendRepository requestAttendRepository;

    @Autowired

    private INotificationRepository notificationRepository;
    @Autowired

    private INotificationService notificationService;

    @Override
    public List<RequestAttendDto> findAll() {
        return null;
    }

    @Override
    public Page<RequestAttendDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<RequestAttendDto> findAll(Specification<RequestAttendEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestAttendDto> filter(Pageable page, Specification<RequestAttendEntity> specs) {
        return null;
    }

    @Override
    public RequestAttendDto findById(Long id) {
        return null;
    }

    @Override
    public RequestAttendEntity getById(Long id) {
        return this.requestAttendRepository.findById(id).orElseThrow(()-> new CustomHandleException(99999));
    }

    @Override
    public RequestAttendDto add(RequestAttendModel model) {
        RequestAttendEntity requestAttendEntity = this.modelToEntity(model);

        // Today can't timekeeping for next day
        if(requestAttendEntity.getDateRequestAttend().after(new Date())){
            throw new CustomHandleException(38);
        }

        // check request attend follow day and user not exist ???
        String day = new SimpleDateFormat("yyyy-MM-dd").format(model.getDateRequestAttend());
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(day, userId);

        // cho tao moi neu khong co request attend nao hoac co request nhung da bi reject
        if(this.checkRequestAttendNotExist(day) || requestAttendExist.stream().anyMatch(x -> x.getStatus().equals(2))){

            RequestAttendEntity result = this.requestAttendRepository.save(requestAttendEntity);

            String title = "Request Attend";
            String content = "You have created a new request attend on " + model.getDateRequestAttend() + " from " + model.getTimeCheckIn() + " to " + model.getTimeCheckOut();
            NotificationModel notificationModel = NotificationModel.builder()
                    .title(title)
                    .content(content)
                    .requestAttendId(result.getId())
                    .build();

            NotificationDto notificationDto = this.notificationService.add(notificationModel);
            return RequestAttendDto.entityToDto(result, notificationDto);
        }else {
            throw new CustomHandleException(37);
        }

    }

    @Override
    public List<RequestAttendDto> add(List<RequestAttendModel> model) {
        return null;
    }

    @Override
    public RequestAttendDto update(RequestAttendModel model) {
        RequestAttendEntity requestAttendUpdateEntity = this.modelToEntity(model);
        requestAttendUpdateEntity.setId(model.getId());

        // Today can't timekeeping for next day
        if(requestAttendUpdateEntity.getDateRequestAttend().after(new Date())){
            throw new CustomHandleException(38);
        }

        // check request attend follow day and user not exist ???
        String day = new SimpleDateFormat("yyyy-MM-dd").format(model.getDateRequestAttend());
        if(this.checkRequestAttendNotExist(day)) {
            RequestAttendEntity result = this.requestAttendRepository.save(requestAttendUpdateEntity);
            String title = "Request Attend";
            String content = "You have updated a request attend on " + model.getDateRequestAttend() + " from " + model.getTimeCheckIn() + " to " + model.getTimeCheckOut();
            NotificationModel notificationModel = NotificationModel.builder()
                    .title(title)
                    .content(content)
                    .requestAttendId(result.getId())
                    .build();
            NotificationDto notificationDto = this.notificationService.add(notificationModel);

            return RequestAttendDto.entityToDto(result, notificationDto);

        }else {
            throw new CustomHandleException(37);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        this.requestAttendRepository.deleteById(id);
        if(this.requestAttendRepository.findById(id).isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    public RequestAttendModel requestToModel(CreateUpdateRequestAttend request, int type){
        int status = 0;
        String reasonCancel = "";
        List<String> fileS3Urls = new ArrayList<>();
        RequestAttendEntity requestAttendEntity = new RequestAttendEntity();
        if(type == 2){
            // Update request, must have id
            if(request.getId() == null){
                throw new CustomHandleException(34);
            }
            Optional<RequestAttendEntity> findRequestAttend = this.requestAttendRepository.findById(request.getId());
            // if status is approved, can't update
            if(findRequestAttend.get().getStatus() == 1){
                throw new CustomHandleException(39);
            }
            // Request attend must not exist
            if(findRequestAttend.isEmpty()){
                throw new CustomHandleException(35);
            }else {
                requestAttendEntity = findRequestAttend.get();
                List<Object> objFiles = new JSONObject(requestAttendEntity.getFiles()).getJSONArray("files").toList();
                for(Object objFile : objFiles){
                    fileS3Urls.add(objFile.toString());
                }
                // If user deleted files
                if(request.getDeletedFilesNumber() != null){
                    for(Integer deletedFileNumber : request.getDeletedFilesNumber()){
                        fileS3Urls.remove(deletedFileNumber.intValue());
                    }
                }

                status = requestAttendEntity.getStatus();
                reasonCancel = requestAttendEntity.getReasonCancel();
            }
        }
        final String folderName = "user/" + SecurityUtils.getCurrentUsername() + "/request_attend/";
        if(request.getAttachedFiles() != null){ // Check if user has attached files
            for(MultipartFile file : request.getAttachedFiles()){
                try{
                    String s3Url = fileUploadProvider.uploadFile(folderName, file);
                    fileS3Urls.add(s3Url);
                }catch (Exception ex){
                    ex.printStackTrace();
                    fileS3Urls.add("Error code: 31");
                    throw new CustomHandleException(31);
                }
            }
        }

        // Check if assigned user id exist
        Optional<UserEntity> userAssigned = userRepository.findById(request.getAssignUserId());
        if(userAssigned.isEmpty()){
            throw new CustomHandleException(33);
        }

        return RequestAttendModel.builder()
                .id(requestAttendEntity.getId())
                .timeCheckIn(request.getTimeCheckIn())
                .timeCheckOut(request.getTimeCheckOut())
                .dateRequestAttend(request.getDateRequestAttend())
                .status(status)
                .reasonCancel(reasonCancel)
                .files(fileS3Urls)
                .createdBy(UserDto.toDto(SecurityUtils.getCurrentUser().getUser()))
                .assignedTo(UserDto.toDto(userAssigned.get()))
                .build();
    }

    private RequestAttendEntity modelToEntity(RequestAttendModel model){
        RequestAttendEntity entity = new RequestAttendEntity();
        entity.setTimeCheckIn(model.getTimeCheckIn());
        entity.setTimeCheckOut(model.getTimeCheckOut());
        entity.setDateRequestAttend(model.getDateRequestAttend());
        entity.setStatus(model.getStatus());
        entity.setReasonCancel(model.getReasonCancel());
        List<String> s3Urls = model.getFiles();
        //new JSONObject(s3Urls).getJSONArray("files").toString();
        JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
        entity.setFiles(jsonObject.toString());
        entity.setCreateBy(userRepository.findById(model.getCreatedBy().getId()).get());
        entity.setAssignTo(userRepository.findById(model.getAssignedTo().getId()).get());
        HistoryRequestEntity historyRequest = new HistoryRequestEntity();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        historyRequest.setDateHistory(new Date());
        historyRequest.setTimeHistory(LocalTime.now().toString());
        historyRequest.setStatus(model.getStatus());
        historyRequest.setRequestAttend(entity);
        return entity;
    }

    @Override
    public Boolean checkRequestAttendNotExist(String dayRequestAttend) {
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(dayRequestAttend, userId);
        if(requestAttendExist.isEmpty()){
            return true; // Request attend not exist
        }
        return false; // Request attend exist
    }
}
