package cy.services.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.NotificationDto;
import cy.entities.NotificationEntity;
import cy.entities.UserEntity;
import cy.models.NotificationModel;
import cy.repositories.INotificationRepository;
import cy.repositories.IRequestAttendRepository;
import cy.repositories.IUserRepository;
import cy.services.*;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional
@Service
public class NotificationServiceImpl implements INotificationService {
    @Autowired
    private INotificationRepository notificationRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRequestAttendRepository requestAttendRepository;
    @Autowired
    private IRequestDayOffService requestDayOffService;
    @Autowired
    private IRequestDeviceService requestDeviceService;
    @Autowired
    private IRequestModifiService requestModifiService;
    @Autowired
    private IRequestOTService requestOvertimeService;

    @Override
    public List<NotificationDto> findAll() {
        return this.notificationRepository.findAll().stream().map(NotificationDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDto> findAll(Pageable page) {
        return this.notificationRepository.findAll(page).map(NotificationDto::toDto);
    }

    @Override
    public List<NotificationDto> findAll(Specification<NotificationEntity> specs) {
        return null;
    }

    @Override
    public Page<NotificationDto> filter(Pageable page, Specification<NotificationEntity> specs) {
        return null;
    }

    @Override
    public NotificationDto findById(Long id) {
        return NotificationDto.toDto(this.notificationRepository.findById(id).orElseThrow(() -> new CustomHandleException(500)));
    }

    @Override
    public NotificationEntity getById(Long id) {
        NotificationEntity notificationEntity = this.notificationRepository.findById(id).orElseThrow(()-> new CustomHandleException(131));
        notificationEntity.setIsRead(true);
        return this.notificationRepository.save(notificationEntity);
    }

    @Transactional
    @Override
    public NotificationDto add(NotificationModel model) {
        NotificationEntity notification = NotificationModel.toEntity(model);
        UserEntity userEntity = this.userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11));
        notification.setUserId(userEntity);
        // request attend
        if(model.getRequestAttendId() != null) {
            notification.setRequestAttendEntityId(this.requestAttendRepository.findById(model.getRequestAttendId()).orElseThrow(()-> new CustomHandleException(99999)));
        }
        // request day off
        if(model.getRequestDayOffId() != null) {
            notification.setRequestDayOff(this.requestDayOffService.getById(model.getRequestDayOffId()));
        }
        // request device
        if(model.getRequestDeviceId() != null) {
            notification.setRequestDevice(this.requestDeviceService.getById(model.getRequestDeviceId()));
        }
        // request modifi
        if(model.getRequestModifiId() != null) {
            notification.setRequestModifi(this.requestModifiService.getById(model.getRequestModifiId()));
        }
        // request OT
        if(model.getRequestOTId() != null) {
            notification.setRequestOT(this.requestOvertimeService.getById(model.getRequestOTId()));
        }
        return NotificationDto.toDto(this.notificationRepository.save(notification));
    }

    @Override
    public List<NotificationDto> add(List<NotificationModel> model) {
        return null;
    }

    @Transactional
    @Override
    public NotificationDto update(NotificationModel model) {
        NotificationEntity oldNotification = this.notificationRepository.findById(model.getId()).orElse(null);
        oldNotification.setTitle(model.getTitle());
        oldNotification.setContent(model.getContent());
        UserEntity userEntity = this.userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11));
        oldNotification.setUserId(userEntity);
        // request attend
        if(model.getRequestAttendId() != null) {
            oldNotification.setRequestAttendEntityId(this.requestAttendRepository.findById(model.getRequestAttendId()).orElseThrow(()-> new CustomHandleException(99999)));
        }
        // request day off
        if(model.getRequestDayOffId() != null) {
            oldNotification.setRequestDayOff(this.requestDayOffService.getById(model.getRequestDayOffId()));
        }
        // request device
        if(model.getRequestDeviceId() != null) {
            oldNotification.setRequestDevice(this.requestDeviceService.getById(model.getRequestDeviceId()));
        }
        // request modifi
        if(model.getRequestModifiId() != null) {
            oldNotification.setRequestModifi(this.requestModifiService.getById(model.getRequestModifiId()));
        }
        // request OT
        if(model.getRequestOTId() != null) {
            oldNotification.setRequestOT(this.requestOvertimeService.getById(model.getRequestOTId()));
        }
        return NotificationDto.toDto(this.notificationRepository.save(oldNotification));
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        this.notificationRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public Page<NotificationDto> findAllByUserId(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return this.notificationRepository.findAllByUserId(userId, pageable).map(NotificationDto::toDto);
    }

    @Override
    public Page<NotificationDto> findAllByUserIdNotRead(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return this.notificationRepository.findAllByUserIdNotRead(userId, pageable).map(NotificationDto::toDto);
    }
}
