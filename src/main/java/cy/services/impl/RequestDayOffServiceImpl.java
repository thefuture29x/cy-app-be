package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestDayOffDto;
import cy.entities.*;
import cy.models.RequestDayOffModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.INotificationRepository;
import cy.repositories.IRequestDayOffRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestDayOffService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());
        if (requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().size() > 0) {
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
        requestDayOff.setHistoryRequestEntities(new ArrayList<>());
        RequestDayOffEntity savedRqDayoff = iRequestDayOffRepository.saveAndFlush(requestDayOff);
        this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestDayOff(savedRqDayoff).status(0).dateHistory(new Date()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
        NotificationEntity notificationEntity = NotificationEntity.builder().requestDayOff(savedRqDayoff).content("Yêu cầu nghỉ phép đã được gửi "+ SecurityUtils.getCurrentUser().getUser().getFullName()).title("Yêu cầu nghỉ phép đã được gửi").dateNoti(new Date()).userId(savedRqDayoff.getCreateBy()).isRead(false).build();
        this.notificationRepository.saveAndFlush(notificationEntity);
        return RequestDayOffDto.toDto(savedRqDayoff);
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
        if (requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().size() > 0) {
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
        if(oldRequest.getStatus()!=0){
            return RequestDayOffDto.builder().reasonCancel("1").build();
        }
        if(SecurityUtils.getCurrentUser().getUser().getRoleEntity() != oldRequest.getAssignTo() && !(SecurityUtils.hasRole(RoleEntity.ADMIN)||SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR))){
            return RequestDayOffDto.builder().reasonCancel("2").build();
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
        NotificationEntity notificationEntity = NotificationEntity.builder().requestDayOff(oldRequest).content("Yêu cầu nghỉ phép đã bị hủy bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName() +"\n"+reasonCancel).title("Yêu cầu chấm công đã bị hủy bỏ").dateNoti(new Date()).userId(oldRequest.getCreateBy()).isRead(false).build();
        this.notificationRepository.saveAndFlush(notificationEntity);
        return RequestDayOffDto.toDto(this.iRequestDayOffRepository.saveAndFlush(oldRequest));
    }
}
