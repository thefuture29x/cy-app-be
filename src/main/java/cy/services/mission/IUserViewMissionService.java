package cy.services.mission;

import cy.dtos.mission.UserViewMissionDto;
import cy.dtos.project.UserViewProjectDto;
import cy.models.project.UserViewProjectModel;

import java.util.List;

public interface IUserViewMissionService {
    void add(UserViewProjectModel model);

    List<UserViewMissionDto> findProjectRecentlyViewed();
}
