package cy.services.attendance.impl;

import cy.dtos.attendance.NotificationDto;
import cy.entities.attendance.HistoryRequestEntity;
import cy.entities.attendance.NotificationEntity;
import cy.entities.attendance.RequestAttendEntity;
import cy.entities.common.RoleEntity;
import cy.models.attendance.CreateUpdateRequestAttend;
import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.RequestAttendDto;
import cy.dtos.common.UserDto;

import cy.entities.common.UserEntity;
import cy.models.attendance.NotificationModel;
import cy.models.attendance.RequestAttendByNameAndYearMonth;
import cy.models.attendance.RequestAttendModel;
import cy.repositories.attendance.IHistoryRequestRepository;
import cy.repositories.attendance.INotificationRepository;
import cy.repositories.attendance.IRequestAttendRepository;
import cy.repositories.common.IUserRepository;
import cy.repositories.attendance.specification.RequestAttendSpecification;
import cy.services.attendance.INotificationService;
import cy.services.attendance.IRequestAttendService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional
@Service
public class RequestAttendServiceImpl implements IRequestAttendService {
    @Autowired
    private FileUploadProvider fileUploadProvider;
    @Autowired
    IHistoryRequestRepository historyRequestRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private IRequestAttendRepository requestAttendRepository;

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
        RequestAttendDto findByIdToDto = this.requestAttendRepository.findByIdToDto(id);
        if (findByIdToDto == null) {
            throw new CustomHandleException(40);
        }
        return findByIdToDto;
    }

    public Page<RequestAttendDto> findByUserId(Long userId, Pageable pageable) {
        List<RequestAttendDto> findByUserId = this.requestAttendRepository.findByUserId(userId);
        final long start = pageable.getOffset();
        final long end = Math.min(start + pageable.getPageSize(), findByUserId.size());
        return new PageImpl<>(findByUserId.subList((int) start, (int) end), pageable, findByUserId.size());
    }


    public List<RequestAttendDto> findByUsername(RequestAttendByNameAndYearMonth data) throws ParseException {
//        String monthYear = new SimpleDateFormat("yyyy-MM").format(data.getDate()) + "%";
        String monthYearCurrent = new SimpleDateFormat("yyyy-MM").format(data.getDate()) + "-31";
        String dateAgo = new SimpleDateFormat("yyyy-MM").format(data.getDate()) + "-01";
//        String dateAgo = data.getDate().toLocalDate().minusMonths(1L).toString();
//        dateAgo =dateAgo.substring(0,7) + "-23";
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByUserNameAndDate_new(data.getName(), monthYearCurrent,dateAgo);
        if (requestAttendExist.isEmpty()) {
            return new ArrayList<>(); // Request attend not exist
        }
        return requestAttendExist.stream().map(RequestAttendDto::entityToDto).collect(Collectors.toList()); // Request attend exist
    }

    @Override
    public RequestAttendEntity getById(Long id) {
        return this.requestAttendRepository.findById(id).orElseThrow(() -> new CustomHandleException(44));
    }

    @Override
    public RequestAttendDto add(RequestAttendModel model) {
        RequestAttendEntity requestAttendEntity = this.modelToEntity(model);

        // Today can't timekeeping for next day
        if (requestAttendEntity.getDateRequestAttend().after(new Date())) {
            throw new CustomHandleException(38);
        }

        // check request attend follow day and user not exist ???
        String day = new SimpleDateFormat("yyyy-MM-dd").format(model.getDateRequestAttend());
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(day, userId);

        // cho tao moi neu khong co request attend nao hoac co request nhung da bi reject
        if (this.checkRequestAttendNotExist(day) || requestAttendExist.stream().anyMatch(x -> x.getStatus().equals(2))) {
            RequestAttendEntity result = this.requestAttendRepository.save(requestAttendEntity);

            // save notification
//            String title = "Yêu cầu chấm công";
//            String content = "Bạn đã tạo một yêu cầu chấm công ngày " + model.getDateRequestAttend() + " từ " + model.getTimeCheckIn() + " giờ đến " + model.getTimeCheckOut() + " giờ";
//            NotificationModel notificationModel = NotificationModel.builder()
//                    .title(title)
//                    .content(content)
//                    .requestAttendId(result.getId())
//                    .build();
//
//            NotificationDto notificationDto = this.notificationService.add(notificationModel);

            // save history
            Date dateHistory = result.getCreatedDate();
            String timeHistory = new SimpleDateFormat("HH:mm:ss").format(new Date());
            Integer status = result.getStatus();
            HistoryRequestEntity historyRequestEntity = HistoryRequestEntity.builder()
                    .dateHistory(dateHistory)
                    .timeHistory(timeHistory)
                    .status(status)
                    .requestAttend(result)
                    .build();
            this.historyRequestRepository.save(historyRequestEntity);

            return RequestAttendDto.entityToDto(result);
        } else {
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
        RequestAttendEntity oldRequestAttend = this.getById(model.getId());
        requestAttendUpdateEntity.setDateRequestAttend(oldRequestAttend.getDateRequestAttend());
        requestAttendUpdateEntity.setTimeCheckIn(oldRequestAttend.getTimeCheckIn());
        requestAttendUpdateEntity.setCreatedDate(new Date());
        // Today can't timekeeping for next day
        if (requestAttendUpdateEntity.getDateRequestAttend().after(new Date())) {
            throw new CustomHandleException(38);
        }

        RequestAttendEntity result = this.requestAttendRepository.saveAndFlush(requestAttendUpdateEntity);

        String title = "Yêu cầu chấm công";
        String content = "Bạn đã gửi yêu cầu chấm công ngày " + requestAttendUpdateEntity.getDateRequestAttend() + " từ " + model.getTimeCheckIn() + " giờ đến " + model.getTimeCheckOut() + " giờ";
        NotificationModel notificationModel = NotificationModel.builder()
                .title(title)
                .content(content)
                .requestAttendId(result.getId())
                .build();
        NotificationDto notificationDto = this.notificationService.add(notificationModel);

        // save history
        Date dateHistory = result.getUpdatedDate();
        String timeHistory = new SimpleDateFormat("HH:mm:ss").format(new Date());

        Integer status = result.getStatus();
        HistoryRequestEntity historyRequestEntity = HistoryRequestEntity.builder()
                .dateHistory(dateHistory)
                .timeHistory(timeHistory)
                .status(status)
                .requestAttend(result)
                .build();
        this.historyRequestRepository.save(historyRequestEntity);

        return RequestAttendDto.entityToDto(result, notificationDto);
    }

    @Override
    public boolean deleteById(Long id) {
        this.requestAttendRepository.deleteById(id);
        if (this.requestAttendRepository.findById(id).isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    public RequestAttendModel requestToModel(CreateUpdateRequestAttend request, int type) {
        int status = 0;
        String reasonCancel = "";
        if (request.getAssignUserId() == null) {
            throw new CustomHandleException(48);
        }
        List<String> fileS3Urls = new ArrayList<>();
        RequestAttendEntity requestAttendEntity = new RequestAttendEntity();
        if (type == 2) {
            // Update request, must have id
            if (request.getId() == null) {
                throw new CustomHandleException(34);
            }
            Optional<RequestAttendEntity> findRequestAttend = this.requestAttendRepository.findById(request.getId());
            // if status is approved, can't update
            if (findRequestAttend.get().getStatus() == 1) {
                throw new CustomHandleException(39);
            }
            // Request attend must not exist
            if (findRequestAttend.isEmpty()) {
                throw new CustomHandleException(35);
            } else {
                List<Object> objFiles = new ArrayList<>();
                requestAttendEntity = findRequestAttend.get();
                if (requestAttendEntity.getFiles() != null) {
                    objFiles = new JSONObject(requestAttendEntity.getFiles()).getJSONArray("files").toList();
                    for (Object objFile : objFiles) {
                        fileS3Urls.add(objFile.toString());
                    }
                }
                // If user deleted files
                if (request.getDeletedFilesNumber() != null && fileS3Urls.size() > 0) {
                    for (Integer deletedFileNumber : request.getDeletedFilesNumber()) {
                        if (deletedFileNumber < fileS3Urls.size()) {
                            fileS3Urls.remove(deletedFileNumber.intValue());
                        } else {
                            throw new CustomHandleException(32);
                        }
                    }
                } else if (request.getDeletedFilesNumber() != null && fileS3Urls.size() == 0) {
                    throw new CustomHandleException(32);
                }

                status = requestAttendEntity.getStatus();
                reasonCancel = requestAttendEntity.getReasonCancel();
            }
        }
        final String folderName = "user/" + SecurityUtils.getCurrentUsername() + "/request_attend/";
        if (request.getAttachedFiles() != null && request.getAttachedFiles().length > 0) { // Check if user has attached files
            for (MultipartFile file : request.getAttachedFiles()) {
                if (!file.getOriginalFilename().equals("")) {
                    try {
                        String s3Url = fileUploadProvider.uploadFile(folderName, file);
                        fileS3Urls.add(s3Url);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        fileS3Urls.add("Error code: 31");
                        throw new CustomHandleException(31);
                    }
                }
            }
        }

        // Check if assigned user id exist
        Optional<UserEntity> userAssigned = userRepository.findById(request.getAssignUserId());
        if (userAssigned.isEmpty()) {
            throw new CustomHandleException(33);
        }

        return RequestAttendModel.builder()
                .id(requestAttendEntity.getId())
                .timeCheckIn(request.getTimeCheckIn() == null ? requestAttendEntity.getTimeCheckIn() : request.getTimeCheckIn())
                .timeCheckOut(request.getTimeCheckOut() == null ? null : request.getTimeCheckOut())
                .dateRequestAttend(request.getDateRequestAttend())
                .status(status)
                .reasonCancel(reasonCancel)
                .files(fileS3Urls.size() > 0 ? fileS3Urls : null)
                .createdBy(UserDto.toDto(SecurityUtils.getCurrentUser().getUser()))
                .assignedTo(UserDto.toDto(userAssigned.get()))
                .build();
    }

    private RequestAttendEntity modelToEntity(RequestAttendModel model) {
        RequestAttendEntity entity = new RequestAttendEntity();
        entity.setTimeCheckIn(model.getTimeCheckIn() == null ? null : model.getTimeCheckIn());
        entity.setTimeCheckOut(model.getTimeCheckOut() == null ? null : model.getTimeCheckOut());
        entity.setDateRequestAttend(model.getDateRequestAttend() != null ? model.getDateRequestAttend() : null);
        entity.setStatus(model.getStatus());
        entity.setReasonCancel(model.getReasonCancel());
        List<String> s3Urls = model.getFiles();
        //new JSONObject(s3Urls).getJSONArray("files").toString();
        if (s3Urls != null) {
            JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
            entity.setFiles(jsonObject.toString());
        }
        entity.setCreateBy(userRepository.findById(model.getCreatedBy().getId()).get());
        entity.setAssignTo(userRepository.findById(model.getAssignedTo().getId()).get());
        HistoryRequestEntity historyRequest = new HistoryRequestEntity();
        historyRequest.setDateHistory(new Date());
        historyRequest.setTimeHistory(LocalTime.now().toString());
        historyRequest.setStatus(model.getStatus());
        historyRequest.setRequestAttend(entity);
        return entity;
    }

    @Transactional
    @Override
    public RequestAttendDto changeRequestStatus(Long id, String reasonCancel, boolean status) {
        RequestAttendEntity oldRequest = this.getById(id);
        Set<String> currentRoles = SecurityUtils.getCurrentUser().getUser().getRoleEntity().stream().map(roleEntity -> roleEntity.getRoleName()).collect(Collectors.toSet());
        if (Set.of(RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER).stream().anyMatch(currentRoles::contains)) {
            return modifyingStatus(status, oldRequest, reasonCancel);
        }
        if (Set.of(RoleEntity.LEADER).stream().anyMatch(currentRoles::contains)) {
            if (SecurityUtils.getCurrentUserId() != oldRequest.getAssignTo().getUserId()) {
                return RequestAttendDto.builder().reasonCancel("2").build();
            } else {
                return modifyingStatus(status, oldRequest, reasonCancel);
            }
        }

        if(status){
            oldRequest.setStatus(1);
            oldRequest.setReasonCancel(null);
            this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestAttend(oldRequest).status(1).dateHistory(oldRequest.getDateRequestAttend()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
            NotificationEntity notificationEntity = NotificationEntity.builder().requestAttendEntityId(oldRequest).content("Yêu cầu chấm công đã được phê duyệt bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName()).title("Yêu cầu chấm công đã được phê duyệt").dateNoti(oldRequest.getDateRequestAttend()).userId(oldRequest.getCreateBy()).isRead(false).build();
            this.notificationRepository.saveAndFlush(notificationEntity);
            return RequestAttendDto.entityToDto(this.requestAttendRepository.saveAndFlush(oldRequest));
        }
        oldRequest.setStatus(2);
        oldRequest.setReasonCancel(reasonCancel);
        this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestAttend(oldRequest).status(2).dateHistory(oldRequest.getDateRequestAttend()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
        NotificationEntity notificationEntity = NotificationEntity.builder().requestAttendEntityId(oldRequest).content("Yêu cầu chấm công đã bị hủy bởi "+ SecurityUtils.getCurrentUser().getUser().getFullName() +"\n"+reasonCancel).title("Yêu cầu chấm công đã bị hủy bỏ").dateNoti(oldRequest.getDateRequestAttend()).userId(oldRequest.getCreateBy()).isRead(false).build();
        this.notificationRepository.saveAndFlush(notificationEntity);
        return RequestAttendDto.entityToDto(this.requestAttendRepository.saveAndFlush(oldRequest));
    }

    @Override
    public Boolean checkRequestAttendNotExist(String dayRequestAttend) {
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(dayRequestAttend, userId);
        if (requestAttendExist.isEmpty()) {
            return true; // Request attend not exist
        }
        return false; // Request attend exist
    }

    @Override
    public Boolean checkRequestAttendExist(java.sql.Date dayRequestAttend) {
        String day = new SimpleDateFormat("yyyy-MM-dd").format(dayRequestAttend);
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(day, userId);
        if (requestAttendExist.isEmpty()) {
            return false; // Request attend not exist
        }
        return true; // Request attend exist
    }

    @Override
    public Long totalDayOfAttendInMonth(Long userId, Date beginDate, Date endDate) {
        if (userId == null | beginDate == null | endDate == null)
            throw new CustomHandleException(30);

        return this.requestAttendRepository.count(
                Specification.where(RequestAttendSpecification.byUserId(userId))
                        .and(RequestAttendSpecification.betweenDateAttendRequest(beginDate, endDate))
                        .and(RequestAttendSpecification.byStatus(1))
        );
    }


    public List<RequestAttendDto> findByDay(java.sql.Date dayRequestAttend) {
        String day = new SimpleDateFormat("yyyy-MM-dd").format(dayRequestAttend);
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByDayAndUser(day, userId);
        if (requestAttendExist.isEmpty()) {
            return null; // Request attend not exist
        }
        return requestAttendExist.stream().map(RequestAttendDto::entityToDto).collect(Collectors.toList()); // Request attend exist
    }

    @Transactional
    public List<RequestAttendDto> findByMonthAndYear(java.sql.Date monthAndYear) {
        String monthYear = new SimpleDateFormat("yyyy-MM").format(monthAndYear) + "%";
        Long userId = SecurityUtils.getCurrentUser().getUser().getUserId();
        List<RequestAttendEntity> requestAttendExist = this.requestAttendRepository.findByMonthAndYearAndUser(monthYear, userId);
        if (requestAttendExist.isEmpty()) {
            return null; // Request attend not exist
        }
        return requestAttendExist.stream().map(RequestAttendDto::entityToDto).collect(Collectors.toList()); // Request attend exist
    }

    private RequestAttendDto modifyingStatus(boolean status, RequestAttendEntity oldRequest, String reasonCancel) {
        if (status) {
            oldRequest.setStatus(1);
            oldRequest.setReasonCancel(null);
            this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestAttend(oldRequest).status(1).dateHistory(oldRequest.getDateRequestAttend()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
            NotificationEntity notificationEntity = NotificationEntity.builder().requestAttendEntityId(oldRequest).content("Yêu cầu chấm công đã được phê duyệt bởi " + SecurityUtils.getCurrentUser().getUser().getFullName()).title("Yêu cầu chấm công đã được phê duyệt").dateNoti(oldRequest.getDateRequestAttend()).userId(oldRequest.getCreateBy()).isRead(false).build();
            this.notificationRepository.saveAndFlush(notificationEntity);
            return RequestAttendDto.entityToDto(this.requestAttendRepository.saveAndFlush(oldRequest));
        }
        oldRequest.setStatus(2);
        oldRequest.setReasonCancel(reasonCancel);
        this.historyRequestRepository.saveAndFlush(HistoryRequestEntity.builder().requestAttend(oldRequest).status(2).dateHistory(oldRequest.getDateRequestAttend()).timeHistory(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).build());
        NotificationEntity notificationEntity = NotificationEntity.builder().requestAttendEntityId(oldRequest).content("Yêu cầu chấm công đã bị hủy bởi " + SecurityUtils.getCurrentUser().getUser().getFullName() + "\n" + reasonCancel).title("Yêu cầu chấm công đã bị hủy bỏ").dateNoti(oldRequest.getDateRequestAttend()).userId(oldRequest.getCreateBy()).isRead(false).build();
        this.notificationRepository.saveAndFlush(notificationEntity);
        return RequestAttendDto.entityToDto(this.requestAttendRepository.saveAndFlush(oldRequest));
    }

}
