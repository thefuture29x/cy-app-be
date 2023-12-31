package cy.services.attendance.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestOTDto;
import cy.entities.attendance.HistoryRequestEntity;
import cy.entities.attendance.NotificationEntity;
import cy.entities.attendance.RequestOTEntity;
import cy.entities.common.RoleEntity;
import cy.models.attendance.RequestOTModel;
import cy.repositories.attendance.IHistoryRequestRepository;
import cy.repositories.attendance.INotificationRepository;
import cy.repositories.attendance.IRequestOTRepository;
import cy.repositories.common.IUserRepository;
import cy.services.attendance.IRequestOTService;
import cy.utils.FileUploadProvider;

import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private IHistoryRequestRepository historyRequestRepository;

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
        return RequestOTDto.toDto(requestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(111)));
    }

    @Override
    public RequestOTEntity getById(Long id) {
        return this.requestOTRepository.findById(id).orElseThrow(() -> new CustomHandleException(111));
    }

    @Override
    public RequestOTDto add(RequestOTModel model) {
        RequestOTEntity requestOTEntity = RequestOTModel.toEntity(model);
        if (model.getStatus() == null){
            requestOTEntity.setStatus(0);
        }
        if (model.getCreateBy() != null) {
            requestOTEntity.setCreateBy(userRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getAssignTo() != null) {
            requestOTEntity.setAssignTo(userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                String result = fileUploadProvider.uploadFile("requestOT", model.getFiles());
                requestOTEntity.setFiles(result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        // Add notification for user created device request
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setTitle("Gửi yêu cầu làm thêm giờ thành công!");
        notificationEntity.setContent("Bạn đã gửi yêu cầu làm thêm giờ thành công. Vui lòng chờ quản lí công ty phê duyệt!");
        notificationEntity.setRequestOT(requestOTEntity);
        notificationRepository.save(notificationEntity);

        // Save history for this request
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime nowTime = LocalTime.now(ZoneId.of("Asia/Saigon"));
        HistoryRequestEntity historyRequestEntity = new HistoryRequestEntity();
        historyRequestEntity.setDateHistory(new Date());
        historyRequestEntity.setTimeHistory(nowTime.format(dtf));
        historyRequestEntity.setStatus(0); // waiting for approve
        historyRequestEntity.setRequestOT(requestOTEntity);
        historyRequestRepository.save(historyRequestEntity);

        return RequestOTDto.toDto(requestOTRepository.save(requestOTEntity));
    }

    @Override
    public List<RequestOTDto> add(List<RequestOTModel> model) {
        List<RequestOTDto> requestOTDtoList = new ArrayList<>();
        for (RequestOTModel requestOTModel: model){
            RequestOTEntity requestOTEntity = RequestOTModel.toEntity(requestOTModel);
            if (requestOTModel.getStatus() == null){
                requestOTEntity.setStatus(0);
            }
            if (requestOTModel.getCreateBy() != null) {
                requestOTEntity.setCreateBy(userRepository.findById(requestOTModel.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
            }
            if (requestOTModel.getAssignTo() != null) {
                requestOTEntity.setAssignTo(userRepository.findById(requestOTModel.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
            }
            if (requestOTModel.getFiles() != null && !requestOTModel.getFiles().isEmpty()) {
                try {
                    String result = fileUploadProvider.uploadFile("requestOT", requestOTModel.getFiles());
                    requestOTEntity.setFiles(result);
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
        RequestOTEntity requestOTEntity = this.getById(model.getId());
        requestOTEntity = RequestOTModel.toEntity(model);

        if (model.getCreateBy() != null) {
            requestOTEntity.setCreateBy(userRepository.findById(model.getCreateBy()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getAssignTo() != null) {
            requestOTEntity.setAssignTo(userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11)));
        }
        if (model.getFiles() != null && !model.getFiles().isEmpty()) {
            try {
                String result = fileUploadProvider.uploadFile("requestOT", model.getFiles());
                requestOTEntity.setFiles(result);
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

    @Override
    public RequestOTDto responseOtRequest(Long requestOtId, String reasonCancel, Boolean status) {
        RequestOTEntity requestOTEntity = this.getById(requestOtId);
        if (!(SecurityUtils.hasRole(RoleEntity.ADMIN) || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR))){
            if (SecurityUtils.getCurrentUser().getUser().getUserId() != requestOTEntity.getAssignTo().getUserId()){
                return RequestOTDto.builder().reasonCancel("113").build();
            }
        }
        if (status){
            requestOTEntity.setStatus(1);
            requestOTEntity.setReasonCancel(null);
            historyRequestRepository.save(HistoryRequestEntity.builder()
                    .requestOT(requestOTEntity)
                    .status(1)
                    .dateHistory(new Date())
                    .timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .build());
            notificationRepository.save(NotificationEntity.builder()
                    .requestOT(requestOTEntity)
                    .content("Yêu cầu OT đã được phê duyệt bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName())
                    .title("Yêu cầu OT đã được phê duyệt")
                    .dateNoti(new Date())
                    .userId(requestOTEntity.getCreateBy())
                    .isRead(false)
                    .build());
            return RequestOTDto.toDto(requestOTRepository.save(requestOTEntity));
        }
        requestOTEntity.setStatus(2);
        requestOTEntity.setReasonCancel(reasonCancel);
        historyRequestRepository.save(HistoryRequestEntity.builder()
                .requestOT(requestOTEntity)
                .status(2)
                .dateHistory(new Date())
                .timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build());
        notificationRepository.save(NotificationEntity.builder()
                .requestOT(requestOTEntity)
                .content("Yêu cầu OT đã bị hủy bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName() +"\n"+reasonCancel)
                .title("Yêu cầu OT đã bị hủy bỏ")
                .dateNoti(new Date())
                .userId(requestOTEntity.getCreateBy())
                .isRead(false)
                .build());
        return RequestOTDto.toDto(requestOTRepository.save(requestOTEntity));
    }

    @Override
    public Float totalOTHours(Long userId, Integer status, Integer typeOt, String startDate, String endDate) {
        return requestOTRepository.totalOTHours(userId, status, typeOt, startDate, endDate);
    }
}
