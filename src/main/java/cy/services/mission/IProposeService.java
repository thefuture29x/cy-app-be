package cy.services.mission;

import cy.dtos.mission.ProposeDto;
import cy.dtos.project.ProjectDto;
import cy.models.mission.ProposeModel;
import cy.models.project.ProjectModel;

import java.io.IOException;
import java.util.List;

public interface IProposeService {
    ProposeDto createPropose(ProposeModel proposeModel) throws IOException;
    List<ProposeDto> findAllOfObject(Long idObject, String category);

}
