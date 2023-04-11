package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.entities.common.RoleEntity;
import cy.models.project.SubTaskUpdateModel;
import cy.models.project.TaskModel;
import cy.models.project.TaskSearchModel;
import cy.services.project.ITaskService;
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
    public ResponseDto deleteTask(@PathVariable Long id) {
        return ResponseDto.of(this.taskService.changIsDeleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping(value = "/findBypage")
    public ResponseDto findBypage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize, @RequestBody TaskModel taskModel) {
        return ResponseDto.of(taskService.findByPage(pageIndex,pageSize,taskModel));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping("/find-all-by-project-id")
    public ResponseDto getAllTaskByProjectId(@RequestParam("id") Long id,Pageable pageable){
        return ResponseDto.of(taskService.findAllByProjectId(id,pageable));
    }
    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PutMapping("/change-status/{taskId}")
    public ResponseDto updateStatusTask(@PathVariable Long taskId, @RequestBody SubTaskUpdateModel subTaskUpdateModel){
        return ResponseDto.of(taskService.updateStatusTask(taskId, subTaskUpdateModel) ? "Update status successfully" : "Update status failed");
    }
    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping(value = "/searchTask")
    public ResponseDto searchTask(@RequestBody TaskSearchModel taskSearchModel) {
        return ResponseDto.of(taskService.searchTask(taskSearchModel));
    }
}
