package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestDayOffDto;
import cy.dtos.ResponseDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.NotificationEntity;
import cy.entities.RequestDayOffEntity;
import cy.entities.UserEntity;
import cy.entities.*;
import cy.models.RequestDayOffModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.INotificationRepository;
import cy.repositories.IRequestDayOffRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestDayOffService;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RequestDayOffServiceImpl implements IRequestDayOffService {
    @Autowired
    IRequestDayOffRepository iRequestDayOffRepository;

    @Autowired
    IHistoryRequestRepository historyRequestRepository;

    @Autowired
    INotificationRepository notificationRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Override
    public List<RequestDayOffDto> findAll() {
        return null;
    }

    @Override
    public Page<RequestDayOffDto> findAll(Pageable page) {
        Page<RequestDayOffEntity> findByPage = iRequestDayOffRepository.findBypage(page);
        return findByPage.map(x -> RequestDayOffDto.toDto(x));
    }

    @Override
    public List<RequestDayOffDto> findAll(Specification<RequestDayOffEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestDayOffDto> filter(Pageable page, Specification<RequestDayOffEntity> specs) {
        return null;
    }

    @Override
    public RequestDayOffDto findById(Long id) {
        return null;
    }

    @Override
    public RequestDayOffEntity getById(Long id) {
        return this.iRequestDayOffRepository.findById(id).orElseThrow(() -> new CustomHandleException(99999));
    }
    @Transactional
    @Override
    public RequestDayOffDto add(RequestDayOffModel requestDayOffModel) {
        RequestDayOffEntity requestDayOff = null;
        if (requestDayOffModel.getId() != null) {
            requestDayOff = iRequestDayOffRepository.findById(requestDayOffModel.getId()).orElse(null);
        }
        if (requestDayOff == null) {
            requestDayOff = new RequestDayOffEntity();
        }
        if (requestDayOffModel.getAssignId() != null) {
            UserEntity assignTo = userRepository.findById(requestDayOffModel.getAssignId()).orElse(null);
            if (assignTo != null)
                requestDayOff.setAssignTo(assignTo);
        }
        if (requestDayOffModel.getCreatedById() != null) {
            UserEntity createdBy = userRepository.findById(requestDayOffModel.getCreatedById()).orElse(null);
            if (createdBy != null)
                requestDayOff.setCreateBy(createdBy);
        }
        requestDayOff.setDescription(requestDayOffModel.getDescription());
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());

        List<String> s3Urls = new ArrayList<>();
        if(requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().length > 0){
            for(MultipartFile fileMultipart : requestDayOffModel.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("requestDayOff",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    s3Urls.add(result);
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
            requestDayOff.setFiles(jsonObject.toString());
        }

        requestDayOff.setDateDayOff(requestDayOffModel.getDateDayOff());
        requestDayOff = iRequestDayOffRepository.save(requestDayOff);

        // Add notification for user created device request
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setTitle("Gửi đơn xin nghỉ làm thành công!");
        notificationEntity.setContent("Bạn đã gửi đơn xin nghỉ làm thành công. Vui lòng chờ quản lí công ty phê duyệt!");
        notificationEntity.setRequestDayOff(requestDayOff);
        notificationRepository.save(notificationEntity);

        // Save history for this request
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime nowTime = LocalTime.now(ZoneId.of("Asia/Saigon"));
        HistoryRequestEntity historyRequestEntity = new HistoryRequestEntity();
        historyRequestEntity.setDateHistory(new Date());
        historyRequestEntity.setTimeHistory(nowTime.format(dtf));
        historyRequestEntity.setStatus(0); // waiting for approve
        historyRequestEntity.setRequestDayOff(requestDayOff);
        historyRequestRepository.save(historyRequestEntity);
        return RequestDayOffDto.toDto(requestDayOff);
    }

    @Override
    public List<RequestDayOffDto> add(List<RequestDayOffModel> model) {
        return null;
    }

    @Override
    public RequestDayOffDto update(RequestDayOffModel requestDayOffModel) {
        RequestDayOffEntity requestDayOff = null;
        if (requestDayOffModel.getId() != null) {
            requestDayOff = iRequestDayOffRepository.findById(requestDayOffModel.getId()).orElse(null);
        }
        if (requestDayOff == null) {
            requestDayOff = new RequestDayOffEntity();
        }
        if (requestDayOffModel.getAssignId() != null) {
            UserEntity assignTo = userRepository.findById(requestDayOffModel.getAssignId()).orElse(null);
            if (assignTo != null)
                requestDayOff.setAssignTo(assignTo);
        }
        if (requestDayOffModel.getCreatedById() != null) {
            UserEntity createdBy = userRepository.findById(requestDayOffModel.getCreatedById()).orElse(null);
            if (createdBy != null)
                requestDayOff.setCreateBy(createdBy);
        }
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());
        if (requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().length > 0) {
            List<String> files = new ArrayList<>();
            for (MultipartFile fileMultipart : requestDayOffModel.getFiles()) {
                if (!fileMultipart.isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile("requestDayOff", fileMultipart);
                        files.add(result);
                    } catch (Exception e) {
                        System.out.println("upload file failed");
                    }
                }
            }
            requestDayOff.setFiles(files.toString());
        }
        requestDayOff.setDateDayOff(new Date());
        requestDayOff = iRequestDayOffRepository.save(requestDayOff);
        return RequestDayOffDto.toDto(requestDayOff);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            iRequestDayOffRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Transactional
    @Override
    public RequestDayOffDto changeRequestStatus(Long id, String reasonCancel, boolean status) {
        RequestDayOffEntity oldRequest = this.getById(id);
        if(!SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)||!SecurityUtils.hasRole(RoleEntity.ADMIN)){
            if(SecurityUtils.getCurrentUserId() != oldRequest.getAssignTo().getUserId()){
                return RequestDayOffDto.builder().reasonCancel("2").build();
            }
        }
        if(status){
            oldRequest.setStatus(1);
            this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestDayOff(oldRequest).status(1).dateHistory(new Date()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
            NotificationEntity notificationEntity = NotificationEntity.builder().requestDayOff(oldRequest).content("Yêu cầu nghỉ phép đã được phê duyệt bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName()).title("Yêu cầu nghỉ phép đã được phê duyệt").dateNoti(new Date()).userId(oldRequest.getCreateBy()).isRead(false).build();
            this.notificationRepository.saveAndFlush(notificationEntity);
            return RequestDayOffDto.toDto(this.iRequestDayOffRepository.saveAndFlush(oldRequest));
        }
        oldRequest.setStatus(2);
        oldRequest.setReasonCancel(reasonCancel);
        this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestDayOff(oldRequest).status(2).dateHistory(new Date()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
        NotificationEntity notificationEntity = NotificationEntity.builder().requestDayOff(oldRequest).content("Yêu cầu nghỉ phép đã bị hủy bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName() +"\n"+reasonCancel).title("Yêu cầu nghỉ phép đã bị hủy bỏ").dateNoti(new Date()).userId(oldRequest.getCreateBy()).isRead(false).build();
        this.notificationRepository.saveAndFlush(notificationEntity);
        return RequestDayOffDto.toDto(this.iRequestDayOffRepository.saveAndFlush(oldRequest));
    }
}
