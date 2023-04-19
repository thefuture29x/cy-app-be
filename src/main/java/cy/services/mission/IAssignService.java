package cy.services.mission;

import cy.dtos.mission.AssignDto;
import cy.dtos.mission.MissionDto;
import cy.models.mission.AssignModel;
import cy.models.mission.MissionModel;
import cy.models.project.SubTaskUpdateModel;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.text.ParseException;

public interface IAssignService {
    AssignDto findById(Long id);
    AssignDto createAssign(AssignModel assignModel) throws IOException;
    AssignDto updateAssign(AssignModel assignModel) throws IOException, ParseException;
    Boolean changIsDeleteById(Long id);
    Page<AssignDto> findByPage(Integer pageIndex, Integer pageSize, String sortBy, String sortType, AssignModel assignModel);
    boolean updateStatusAssign(Long idAssign, SubTaskUpdateModel subTaskUpdateModel);
}
