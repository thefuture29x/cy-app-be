package cy.services.project;

import cy.dtos.project.UserViewProjectDto;
import cy.models.project.UserViewProjectModel;

import java.util.List;

public interface IUserViewProjectService{
    void add(UserViewProjectModel model);

    List<UserViewProjectDto> findProjectRecentlyViewed();
}
