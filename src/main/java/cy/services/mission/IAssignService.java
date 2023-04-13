package cy.services.mission;

import cy.dtos.mission.AssignDto;
import cy.dtos.mission.MissionDto;
import cy.models.mission.AssignModel;
import cy.models.mission.MissionModel;

import java.io.IOException;
import java.text.ParseException;

public interface IAssignService {
    AssignDto createAssign(AssignModel assignModel) throws IOException;
    AssignDto updateAssign(AssignModel assignModel) throws IOException, ParseException;
}
