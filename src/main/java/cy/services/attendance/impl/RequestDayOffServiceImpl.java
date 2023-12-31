package cy.services.attendance.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestDayOffDto;
import cy.entities.attendance.HistoryRequestEntity;
import cy.entities.attendance.NotificationEntity;
import cy.entities.attendance.RequestDayOffEntity;
import cy.entities.common.RoleEntity;
import cy.entities.common.UserEntity;
import cy.models.attendance.RequestDayOffModel;
import cy.repositories.attendance.IHistoryRequestRepository;
import cy.repositories.attendance.INotificationRepository;
import cy.repositories.attendance.IRequestDayOffRepository;
import cy.repositories.common.IUserRepository;
import cy.services.attendance.IRequestDayOffService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        requestDayOff.setTypeOff(requestDayOffModel.getTypeOff());
        requestDayOff.setIsLegit(requestDayOffModel.getIsLegit());
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
        Set<String> currentRoles = SecurityUtils.getCurrentUser().getUser().getRoleEntity().stream().map(roleEntity -> roleEntity.getRoleName()).collect(Collectors.toSet());
        if(Set.of(RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER).stream().anyMatch(currentRoles::contains)){
            return modifyingStatus(status,oldRequest,reasonCancel);
        }
        if(Set.of(RoleEntity.LEADER).stream().anyMatch(currentRoles::contains)){
            if(SecurityUtils.getCurrentUserId() != oldRequest.getAssignTo().getUserId()){
                return RequestDayOffDto.builder().reasonCancel("2").build();
            }else {
                return modifyingStatus(status,oldRequest,reasonCancel);
            }
        }
        return null;
    }
    private RequestDayOffDto modifyingStatus(boolean status, RequestDayOffEntity oldRequest, String reasonCancel){
        if(status){
            oldRequest.setStatus(1);
            oldRequest.setReasonCancel(null);
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
    @Override
    public List<RequestDayOffDto> getTotalDayOffByMonthOfUser(String dateStart, String dateEnd, Long uid, boolean isLegit, int status, Pageable page) {
        List<RequestDayOffDto> dayOffDtos = this.iRequestDayOffRepository.getAllDayOfByMonthOfUser(uid, dateStart, dateEnd, isLegit, status, page).stream().map(RequestDayOffDto::toDto).collect(Collectors.toList());
        return dayOffDtos;
    }
}
