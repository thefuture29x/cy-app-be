package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.attendance.CreateUpdateRequestAttend;
import cy.models.project.TaskModel;
import cy.services.project.ITaskService;
import cy.utils.Const;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "task")
@RestController
public class TaskResouce {
    private final ITaskService taskService;

    public TaskResouce(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseDto getAllTask(Pageable pageable){
        return ResponseDto.of(this.taskService.findAll(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseDto getTaskById(@PathVariable("id") Long id) {
        return ResponseDto.of(this.taskService.findById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping
    @Transactional
    public ResponseDto addTask(TaskModel model) {
        model.setId(null);
        return ResponseDto.of(this.taskService.add(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PutMapping("/{id}")
    @Transactional
    public ResponseDto updateTask(TaskModel model, @PathVariable Long id) {
        model.setId(id);
        return ResponseDto.of(this.taskService.update(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseDto deleteTask(@PathVariable Long id) {
        return ResponseDto.of(this.taskService.changIsDeleteById(id));
    }
}
