package cy.resources.attendance;

import cy.configs.FrontendConfiguration;
import cy.dtos.attendance.NotificationDto;
import cy.dtos.common.ResponseDto;
import cy.models.attendance.NotificationModel;
import cy.services.attendance.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "notification/")
public class NotificationResource {
    @Autowired private INotificationService notificationService;

    @GetMapping
    public ResponseDto getAllNotification(Pageable pageable) {
        return ResponseDto.of( this.notificationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseDto getNotificationById(@PathVariable Long id){
        return ResponseDto.of( this.notificationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_LEADER', 'ROLE_EMPLOYEE')")
    public ResponseDto addNotification(@RequestBody NotificationModel model) throws IOException {
        return ResponseDto.of( this.notificationService.add(model));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_LEADER', 'ROLE_EMPLOYEE')")
    public ResponseDto editNotification(@RequestBody NotificationModel model, @PathVariable Long id) throws IOException {
        model.setId(id);
        return ResponseDto.of( this.notificationService.update(model));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_LEADER', 'ROLE_EMPLOYEE')")
    public ResponseDto deleteNotification(@PathVariable Long id){
        return ResponseDto.of( this.notificationService.deleteById(id));
    }

    @GetMapping("/user")
    public ResponseDto getAllNotificationByUserId(Pageable pageable) {
        return ResponseDto.of( this.notificationService.findAllByUserId(pageable));
    }

    @GetMapping("/user/{id}") // id = notificationId
    public ResponseDto getAllNotificationByUserId(@PathVariable Long id) {
        return ResponseDto.of(NotificationDto.toDto(this.notificationService.getById(id)));
    }

    @GetMapping("/user/notificationNotRead")
    public ResponseDto getAllNotificationByUserIdNotRead(Pageable pageable) {
        return ResponseDto.of( this.notificationService.findAllByUserIdNotRead(pageable));
    }
}
