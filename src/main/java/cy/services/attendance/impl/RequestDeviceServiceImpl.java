package cy.services.attendance.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestDeviceDto;
import cy.entities.attendance.HistoryRequestEntity;
import cy.entities.attendance.NotificationEntity;
import cy.entities.attendance.RequestDeviceEntity;
import cy.entities.common.UserEntity;
import cy.models.attendance.RequestDeviceModel;
import cy.models.attendance.RequestDeviceUpdateStatusModel;
import cy.repositories.attendance.IHistoryRequestRepository;
import cy.repositories.attendance.INotificationRepository;
import cy.repositories.attendance.IRequestDeviceRepository;
import cy.repositories.common.IUserRepository;
import cy.services.attendance.IRequestDeviceService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    EntityManager manager;

    
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

    public void createNotification_new(RequestDeviceEntity requestDeviceEntity,Boolean isRead,String title,String content){
        NotificationEntity notificationEntity=new NotificationEntity();
        notificationEntity.setDateNoti(new java.util.Date());
        notificationEntity.setUserId(requestDeviceEntity.getCreateBy());
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

        List<String> s3Urls = new ArrayList<>();
        if(model.getFiles() != null && model.getFiles().length > 0){
            for(MultipartFile fileMultipart : model.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("device",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    s3Urls.add(result);
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
            requestDeviceEntity.setFiles(jsonObject.toString());
        }
        requestDeviceEntity.setStatus(0);
        createHistory(requestDeviceEntity,0);
        createNotification(requestDeviceEntity,false,"Gửi yêu cầu mượn/thuê thiết bị thành công!","Bạn đã gửi yêu cầu mượn/thuê thiết bị thành công. Vui lòng chờ quản lí công ty phê duyệt!");

        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        RequestDeviceDto requestDeviceDto = RequestDeviceDto.entityToDto(requestDeviceEntity);
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
                    requestDeviceEntity.setReasonCancel(null);
                    iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
                    createHistory(requestDeviceEntity,1);
                    createNotification_new(requestDeviceEntity,true,"Xét duyệt bởi "+userEntity.getFullName(),"Yêu cầu cấp thiết bị");
                    // return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
                    break;
                case 2:
                    requestDeviceEntity.setStatus(2);
                    requestDeviceEntity.setReasonCancel(model.getReasonCancel());
                    iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
                    createHistory(requestDeviceEntity,2);
                    createNotification_new(requestDeviceEntity,true,"Đã bị hủy bởi "+userEntity.getFullName(),"Yêu cầu cấp thiết bị từ chối: "+ model.getReasonCancel());
                    //   return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
                    break;
            }

        }else {
            System.out.printf("userId: "+userEntity.getUserId()+" assignTo: "+requestDeviceEntity.getAssignTo().getUserId());
            System.out.printf("userIdLogin:"+SecurityUtils.getCurrentUser().getUser().getUserId());
            System.out.printf("Không có quyền chỉnh sửa yêu cầu này");
        }
       /* requestDeviceEntity.setStatus(1);
        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        createHistory(requestDeviceEntity,requestDeviceEntity.getStatus());*/
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(11)));
    }
    @Override
    public Page<RequestDeviceDto> findAllByPage(Integer pageIndex, Integer pageSize,RequestDeviceModel requestDeviceModel) {
        String sql="SELECT r FROM RequestDeviceEntity r WHERE 1=1 ";
        String countSQL = "select count(*) from RequestDeviceEntity r where 1=1 ";

        if(requestDeviceModel.getCreateBy() != null) {
            sql+=" AND r.createBy.id = "+requestDeviceModel.getCreateBy();
            countSQL+=" AND r.createBy.id = "+requestDeviceModel.getCreateBy();
        }

        if(requestDeviceModel.getStatus() != null) {
            sql+=" AND r.status = "+requestDeviceModel.getStatus();
            countSQL+=" AND r.status = "+requestDeviceModel.getStatus();
        }

        if(requestDeviceModel.getTypeRequestDevice() != null) {
            sql+=" AND r.typeRequestDevice = "+requestDeviceModel.getTypeRequestDevice();
            countSQL+=" AND r.typeRequestDevice = "+requestDeviceModel.getTypeRequestDevice();
        }
        sql+="order by r.updatedDate desc";
        Query q = manager.createQuery(sql,RequestDeviceEntity.class);
        Query qCount = manager.createQuery(countSQL);

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex,pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<RequestDeviceEntity> pageEntity = new PageImpl<>(q.getResultList(), pageable, numberResult);
        Page<RequestDeviceDto> requestDeviceDtos = pageEntity.map(x -> RequestDeviceDto.entityToDto(x));

        return requestDeviceDtos;
    }
    /*public RequestDeviceDto updateStatusCancle(Long id,String reason) {
        RequestDeviceEntity requestDeviceEntity = this.getById(id);
        requestDeviceEntity.setStatus(2);
        requestDeviceEntity.setReasonCancel(reason);
        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        createHistory(requestDeviceEntity,requestDeviceEntity.getStatus());
        return RequestDeviceDto.entityToDto(iRequestDeviceRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }*/

    public String returnDevice(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        RequestDeviceEntity requestDeviceEntity = this.getById(id);
        Integer typeRequestDevice = requestDeviceEntity.getTypeRequestDevice();
        Integer status = requestDeviceEntity.getStatus();
        if(typeRequestDevice == 2){ // Đã trả thiết bị rồi.
            throw new CustomHandleException(71);
        }
        if(status != 1){ // Yêu cầu chưa được duyệt -> khỏi phải trả =)))
            throw new CustomHandleException(73);
        }
        requestDeviceEntity.setTypeRequestDevice(2);
        iRequestDeviceRepository.saveAndFlush(requestDeviceEntity);
        this.createHistory(requestDeviceEntity,requestDeviceEntity.getStatus());
        this.createNotification(requestDeviceEntity,false,"Trả thiết bị thành công!","Bạn đã gửi yêu cầu trả " +
                "thiết bị mượn từ ngày " + sdf.format(requestDeviceEntity.getDateRequestDevice()) + " thành công.");
        this.sendNotificationToManager(requestDeviceEntity);
        return "Return device success!";
    }

    private void sendNotificationToManager(RequestDeviceEntity requestDeviceEntity){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setTitle("Người dùng " + SecurityUtils.getCurrentUsername() + " đã trả thiết bị");
        notificationEntity.setContent(SecurityUtils.getCurrentUsername() + " vừa yêu cầu trả " + requestDeviceEntity.getType()
        + " mượn từ ngày " + sdf.format(requestDeviceEntity.getDateRequestDevice()) + "!");
        notificationEntity.setIsRead(false);
        notificationEntity.setUserId(requestDeviceEntity.getAssignTo());
        notificationEntity.setRequestDevice(requestDeviceEntity);
        this.notificationRepository.saveAndFlush(notificationEntity);
    }

    public Page<RequestDeviceDto> filterByType(String type, Pageable pageable){
        Page<RequestDeviceEntity> requestDeviceEntities =
                this.iRequestDeviceRepository.filterByType(SecurityUtils.getCurrentUserId(),
                        type, pageable);
        return requestDeviceEntities.map(RequestDeviceDto::entityToDto);
    }

    public Page<RequestDeviceDto> createdByMyself(Pageable pageable){
        Page<RequestDeviceEntity> requestDeviceEntities =
                this.iRequestDeviceRepository.getAllRequestCreateByMe(SecurityUtils.getCurrentUserId(),
                        pageable);
        if(requestDeviceEntities != null
                && requestDeviceEntities.getContent() != null
                &&requestDeviceEntities.getContent().size() == 0){
            return Page.empty();
        }
        return requestDeviceEntities.map(RequestDeviceDto::entityToDto);
    }
}
