package cy.configs;

import cy.entities.project.SubTaskEntity;
import cy.repositories.project.ISubTaskRepository;
import cy.repositories.project.ITaskRepository;
import cy.services.project.ISubTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class ScheduledJobConfig {
    @Autowired
    ISubTaskRepository subTaskRepository;
    @Autowired
    ISubTaskService subTaskService;
    @Autowired
    ITaskRepository taskRepository;

    //Cron for delete
    // second(1-59) minute(0-59) hour(1-23) dayOfMonth(1-31) month(1-12) dayOfWeek(0-6, sunday = 0)
    @Scheduled(cron = "0 0/5 * * * *")
    public void checkStatusIsDelete(){
        List<SubTaskEntity> subTaskEntities = subTaskRepository.checkSubTasksDelete();
        if(!subTaskEntities.isEmpty()){
            subTaskEntities.stream().forEach(subTaskEntity -> this.subTaskService.deleteById(subTaskEntity.getId()));
        }
//        taskRepository.deleteTaskEntitiesByIsDeleted();
    }

}
