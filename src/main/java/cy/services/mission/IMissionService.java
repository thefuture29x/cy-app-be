package cy.services.mission;

import cy.dtos.common.UserMetaDto;
import cy.dtos.mission.MissionDto;
import cy.dtos.project.ProjectDto;
import cy.models.mission.MissionModel;
import cy.models.project.ProjectModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IMissionService {
    MissionDto findById(Long id, boolean view);
    MissionDto createMission(MissionModel missionModel) throws IOException;
    MissionDto updateMission(MissionModel missionModel) throws IOException, ParseException;
    Boolean changIsDeleteById(Long id);
    Page<MissionDto> findByPage(Integer pageIndex, Integer pageSize,String sortBy, String sortType,MissionModel missionModel);
    List<UserMetaDto> getAllUserInMission(String category,String type, Long idObject);
}
