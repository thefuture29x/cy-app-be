package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.RequestDeviceDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.NotificationEntity;
import cy.entities.RequestDeviceEntity;
import cy.entities.UserEntity;
import cy.models.RequestDeviceModel;
import cy.models.RequestDeviceUpdateStatusModel;
import cy.repositories.IHistoryRequestRepository;
import cy.repositories.INotificationRepository;
import cy.repositories.IRequestDeviceRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestDeviceService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestDeviceServiceImpl implements IRequestDeviceService {
    @Autowired
    IRequestDeviceRepository iRequestDeviceRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IHistoryRequestRepository historyRequestRepository;
    @Autowired
    INotificationRepository notificationRepository;

    @Autowired
    IHistoryRequestRepository historyRequestRepository;

    @Autowired
    INotificationRepository notificationRepository;
    @Override
    public List<RequestDeviceDto> findAll() {
        return iRequestDeviceRepository.findAll().stream().map(data -> RequestDeviceDto.entityToDto(data)).collect(Collectors.toList());
    }

    @Override
    public Page<RequestDeviceDto> findAll(Pageable page) {
        return iRequestDeviceRepository.findAll(page).map(data -> RequestDeviceDto.entityToDto(data));
    }

    @Override
    public List<RequestDeviceDto> findAll(Specification<RequestDeviceEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestDeviceDto> filter(Pageable page, Specification<RequestDeviceEntity> specs) {
        return null;
    }

    @Override
    public RequestDeviceDto findById(Long id) {
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public RequestDeviceEntity getById(Long id) {
        return this.iRequestDeviceRepository.findById(id).orElseThrow(()->new CustomHandleException(11));
    }
    public void createHistory(RequestDeviceEntity requestDeviceEntity,int status){
        HistoryRequestEntity historyRequest=new HistoryRequestEntity();
        String pattern = "yyyy-MM-dd";
        historyRequest.setDateHistory(new Date());
        historyRequest.setTimeHistory(LocalTime.now().toString().substring(0,5));
        historyRequest.setStatus(status);
        historyRequest.setRequestDevice(requestDeviceEntity);
        historyRequestRepository.save(historyRequest);
    }
    public void createNotification(RequestDeviceEntity requestDeviceEntity,Boolean isRead,String title,String content){
        NotificationEntity notificationEntity=new NotificationEntity();
        notificationEntity.setDateNoti(new java.util.Date());
        notificationEntity.setUserId(SecurityUtils.getCurrentUser().getUser());
        notificationEntity.setIsRead(isRead);
        notificationEntity.setTitle(title);
        notificationEntity.setContent(content);
        notificationEntity.setRequestDevice(requestDeviceEntity);
        notificationRepository.save(notificationEntity);
    }

    @Override
    public RequestDeviceDto add(RequestDeviceModel model)  {
        RequestDeviceEntity requestDeviceEntity = model.modelToEntity(model);
        requestDeviceEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());

        UserEntity assignUser = userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11));

        requestDeviceEntity.setAssignTo(assignUser);

        if (model.getFiles() != null && model.getFiles().length > 0) {
            List<String> files = new ArrayList<>();
            for (MultipartFile fileMultipart : model.getFiles()) {
                if (!fileMultipart.isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile("device", fileMultipart);
                        files.add(result);
                    } catch (Exception e) {
                        System.out.println("upload file failed");
                    }
                }
            }
            requestDeviceEntity.setFiles(files.toString());
        }


        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        RequestDeviceDto requestDeviceDto = RequestDeviceDto.entityToDto(requestDeviceEntity);

        // Add notification for user created device request
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setTitle("Gửi yêu cầu mượn/thuê thiết bị thành công!");
        notificationEntity.setContent("Bạn đã gửi yêu cầu mượn/thuê thiết bị thành công. Vui lòng chờ quản lí công ty phê duyệt!");
        notificationEntity.setRequestDevice(requestDeviceEntity);
        notificationRepository.save(notificationEntity);

        // Save history for this request
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime nowTime = LocalTime.now(ZoneId.of("Asia/Saigon"));
        HistoryRequestEntity historyRequestEntity = new HistoryRequestEntity();
        historyRequestEntity.setDateHistory(new Date());
        historyRequestEntity.setTimeHistory(nowTime.format(dtf));
        historyRequestEntity.setStatus(0); // waiting for approve
        historyRequestEntity.setRequestDevice(requestDeviceEntity);
        historyRequestRepository.save(historyRequestEntity);
        return requestDeviceDto;
    }

    @Override
    public List<RequestDeviceDto> add(List<RequestDeviceModel> model) {
        return null;
    }

    @Override
    public RequestDeviceDto update(RequestDeviceModel model){
        RequestDeviceEntity requestDeviceEntity = model.modelToEntity(model);
        requestDeviceEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());

        UserEntity assignUser = userRepository.findById(model.getAssignTo()).orElseThrow(() -> new CustomHandleException(11));

        requestDeviceEntity.setAssignTo(assignUser);

        if (model.getFiles() != null && model.getFiles().length > 0) {
            List<String> files = new ArrayList<>();
            for (MultipartFile fileMultipart : model.getFiles()) {
                if (!fileMultipart.isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile("device", fileMultipart);
                        files.add(result);
                    } catch (Exception e) {
                        System.out.println("upload file failed");
                    }
                }
            }
            requestDeviceEntity.setFiles(files.toString());
        }


        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        RequestDeviceDto requestDeviceDto = RequestDeviceDto.entityToDto(requestDeviceEntity);
        return requestDeviceDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            iRequestDeviceRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    public RequestDeviceDto updateStatus(RequestDeviceUpdateStatusModel model) {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        RequestDeviceEntity requestDeviceEntity = this.getById(model.getId());
        if(userEntity.getUserId()==requestDeviceEntity.getAssignTo().getUserId()){
            switch (model.getSwitchCase()){
                case 1:
                    requestDeviceEntity.setStatus(1);
                    iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
                    createHistory(requestDeviceEntity,1);
                    createNotification(requestDeviceEntity,true,"Xét duyệt bởi "+userEntity.getFullName(),"Yêu cầu cấp thiết bị");
                    // return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
                    break;
                case 2:
                    requestDeviceEntity.setStatus(2);
                    requestDeviceEntity.setReasonCancel(model.getReasonCancel());
                    iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
                    createHistory(requestDeviceEntity,2);
                    createNotification(requestDeviceEntity,true,"Đã bị hủy bởi "+userEntity.getFullName(),"Yêu cầu cấp thiết bị");
                    //   return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
                    break;
            }

        }
       /* requestDeviceEntity.setStatus(1);
        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        createHistory(requestDeviceEntity,requestDeviceEntity.getStatus());*/
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11)));
    }
    /*public RequestDeviceDto updateStatusCancle(Long id,String reason) {
        RequestDeviceEntity requestDeviceEntity = this.getById(id);
        requestDeviceEntity.setStatus(2);
        requestDeviceEntity.setReasonCancel(reason);
        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        createHistory(requestDeviceEntity,requestDeviceEntity.getStatus());
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }*/
}
