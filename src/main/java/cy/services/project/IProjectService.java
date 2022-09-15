package cy.services.project;

import cy.dtos.project.ProjectDto;
import cy.models.project.ProjectModel;
import org.springframework.data.domain.Page;

public interface IProjectService {
    ProjectDto findById(Long id);
    ProjectDto createProject(ProjectModel projectModel);
    ProjectDto updateProject(ProjectModel projectModel);
    Boolean deleteProject(Long id);
    Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize, ProjectModel projectModel);

}
