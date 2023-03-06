package cy.services.project;

import cy.dtos.UserDto;
import cy.dtos.project.ProjectDto;
import cy.dtos.project.UserMetaDto;
import cy.models.project.ProjectModel;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IProjectService {
    ProjectDto findById(Long id);
    ProjectDto createProject(ProjectModel projectModel) throws IOException;
    ProjectDto updateProject(ProjectModel projectModel) throws IOException, ParseException;
    Boolean deleteProject(Long id);
    Boolean changIsDeleteById(Long id);
    Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize,String sortBy,String sortType, ProjectModel projectModel);

    List<UserMetaDto> getAllUserInProject(String category,String type, Long idObject);
}
