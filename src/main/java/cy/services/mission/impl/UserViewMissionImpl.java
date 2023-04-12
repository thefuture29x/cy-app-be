package cy.services.mission.impl;

import cy.dtos.mission.UserViewMissionDto;
import cy.dtos.project.UserViewProjectDto;
import cy.entities.mission.MissionEntity;
import cy.entities.mission.UserViewMissionEntity;
import cy.entities.project.ProjectEntity;
import cy.entities.project.UserViewProjectEntity;
import cy.models.project.UserViewProjectModel;
import cy.repositories.mission.IMissionRepository;
import cy.repositories.mission.IUserViewMissionRepository;
import cy.repositories.project.IProjectRepository;
import cy.repositories.project.IUserViewProjectRepository;
import cy.services.mission.IUserViewMissionService;
import cy.services.project.IUserViewProjectService;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserViewMissionImpl implements IUserViewMissionService {
    @Autowired
    IUserViewMissionRepository iUserViewMissionRepository;
    @Autowired
    IMissionRepository iMissionRepository;

    @Override
    public void add(UserViewProjectModel model) {
        Date now = Date.from(Instant.now());
        MissionEntity project = iMissionRepository.findById(model.getObjectId()).get();
        iUserViewMissionRepository.save(new UserViewMissionEntity(null, model.getIdUser(), now, project));
    }

    @Override
    public List<UserViewMissionDto> findProjectRecentlyViewed() {
        return iUserViewMissionRepository.findProjectRecentlyViewed(SecurityUtils.getCurrentUserId()).stream().map(data -> UserViewMissionDto.toDto(data)).collect(Collectors.toList());
    }

}