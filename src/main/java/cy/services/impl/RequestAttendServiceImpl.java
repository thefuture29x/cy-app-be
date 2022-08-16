package cy.services.impl;

import cy.dtos.HistoryRequestDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.UserEntity;
import cy.models.CreateAttendRequest;
import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.UserDto;
import cy.entities.RequestAttendEntity;
import cy.models.RequestAttendModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.IRequestAttendRepository;
import cy.repositories.IUserRepository;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class RequestAttendServiceImpl implements IRequestAttendService {
    @Autowired
    private FileUploadProvider fileUploadProvider;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IHistoryRequestRepository historyRequestRepository;

    @Autowired
    private IRequestAttendRepository requestAttendRepository;

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
    public RequestAttendDto add(RequestAttendModel model) {
        RequestAttendEntity requestAttendEntity = this.modelToEntity(model);
        RequestAttendEntity result = this.requestAttendRepository.save(requestAttendEntity);
        return null;
    }

    @Override
    public List<RequestAttendDto> add(List<RequestAttendModel> model) {
        return null;
    }

    @Override
    public RequestAttendDto update(RequestAttendModel model) {
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

    public RequestAttendModel requestToModel(CreateAttendRequest request){
        final String folderName = "user/" + SecurityUtils.getCurrentUsername() + "/request_attend/";
        List<String> fileS3Urls = new ArrayList<>();
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

        // Check if requested user id exist
        Optional<UserEntity> userCreated = userRepository.findById(request.getRequestUserId());
        if(userCreated.isEmpty()){
            throw new CustomHandleException(32);
        }

        // Check if assigned user id exist
        Optional<UserEntity> userAssigned = userRepository.findById(request.getRequestUserId());
        if(userAssigned.isEmpty()){
            throw new CustomHandleException(33);
        }

        return RequestAttendModel.builder()
                .timeCheckIn(request.getTimeCheckIn())
                .timeCheckOut(request.getTimeCheckOut())
                .dateRequestAttend(request.getDateRequestAttend())
                .status(0)
                .reasonCancel("")
                .files(fileS3Urls)
                .createdBy(UserDto.toDto(userCreated.get()))
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

    private RequestAttendDto entityToDto(RequestAttendEntity entity){
        List<Object> s3UrlsObj = new JSONObject().getJSONArray("files").toList();
        List<String> s3Urls = new ArrayList<>();
        for(Object s3Url : s3UrlsObj){
            s3Urls.add(s3Url.toString());
        }
        return RequestAttendDto.builder()
                .id(entity.getId())
                .timeCheckIn(entity.getTimeCheckIn())
                .timeCheckOut(entity.getTimeCheckOut())
                .dateRequestAttend(entity.getDateRequestAttend())
                .status(entity.getStatus())
                .reasonCancel(entity.getReasonCancel())
                .files(s3Urls)
                .createdBy(UserDto.toDto(entity.getCreateBy()))
                .assignedTo(UserDto.toDto(entity.getAssignTo()))
                .historyRequests(null)
                .build();
    }
}
