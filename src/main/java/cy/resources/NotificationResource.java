package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.NotificationModel;
import cy.services.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
    public ResponseDto addNotification(@RequestBody NotificationModel model){
        return ResponseDto.of( this.notificationService.add(model));
    }

    @PutMapping("/{id}")
    public ResponseDto editNotification(@RequestBody NotificationModel model, @PathVariable Long id){
        model.setId(id);
        return ResponseDto.of( this.notificationService.update(model));
    }

    @DeleteMapping("/{id}")
    public ResponseDto deleteNotification(@PathVariable Long id){
        return ResponseDto.of( this.notificationService.deleteById(id));
    }
}
