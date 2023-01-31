package cy.configs;

import cy.entities.project.*;
import cy.repositories.project.*;
import cy.services.project.*;
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
    @Autowired
    ITaskService taskService;
    @Autowired
    IFeatureRepository featureRepository;
    @Autowired
    IFeatureService featureService;
    @Autowired
    IProjectRepository projectRepository;
    @Autowired
    IProjectService projectService;
    @Autowired
    IBugRepository bugRepository;
    @Autowired
    IRequestBugService bugService;

    //Cron for delete
    // second(1-59) minute(0-59) hour(1-23) dayOfMonth(1-31) month(1-12) dayOfWeek(0-6, sunday = 0)
//    @Scheduled(cron = "0 0/30 * * * *")
    public void checkStatusIsDelete(){
        // check Project
        if(!this.projectRepository.checkProjectDelete().isEmpty()){
            this.projectRepository.checkProjectDelete().forEach(project -> this.projectService.deleteProject(project.getId()));
        }

        // check Feature
        if(!this.featureRepository.checkFeatureDelete().isEmpty()){
            this.featureRepository.checkFeatureDelete().forEach(feature -> this.featureService.deleteById(feature.getId()));
        }

        // check Task
        if(!taskRepository.checkTasksDelete().isEmpty()){
            taskRepository.checkTasksDelete().forEach(taskEntity -> this.taskService.deleteById(taskEntity.getId()));
        }

        // check subTask
        if(!subTaskRepository.checkSubTasksDelete().isEmpty()){
            subTaskRepository.checkSubTasksDelete().forEach(subTaskEntity -> this.subTaskService.deleteById(subTaskEntity.getId()));
        }

        //check BUG
        if(!this.bugRepository.checkBugDelete().isEmpty()){
            this.bugRepository.checkBugDelete().forEach(bugEntity -> this.bugService.deleteBug(bugEntity.getId()));
        }

    }

}
