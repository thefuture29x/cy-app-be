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
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkStatusIsDelete(){
        // check subTask
        List<SubTaskEntity> subTaskEntities = subTaskRepository.checkSubTasksDelete();
        if(!subTaskEntities.isEmpty()){
            subTaskEntities.stream().forEach(subTaskEntity -> this.subTaskService.deleteById(subTaskEntity.getId()));
        }

        // check Task
        List<TaskEntity> taskEntities = taskRepository.checkTasksDelete();
        if(!taskEntities.isEmpty()){
            taskEntities.stream().forEach(taskEntity -> this.taskService.deleteById(taskEntity.getId()));
        }

        // check Feature
        List<FeatureEntity> featureEntities = this.featureRepository.checkFeatureDelete();
        if(!featureEntities.isEmpty()){
            featureEntities.stream().forEach(feature -> this.featureService.deleteById(feature.getId()));
        }

        // check Project
        List<ProjectEntity> projectEntities = this.projectRepository.checkProjectDelete();
        if(!projectEntities.isEmpty()){
            projectEntities.stream().forEach(project -> this.projectService.deleteProject(project.getId()));
        }

        //check BUG
        List<BugEntity> bugEntities = this.bugRepository.checkBugDelete();
        if(!bugEntities.isEmpty()){
            bugEntities.stream().forEach(bugEntity -> this.bugService.deleteBug(bugEntity.getId()));
        }
    }

}
