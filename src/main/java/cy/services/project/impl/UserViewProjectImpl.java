package cy.services.project.impl;

import cy.dtos.project.UserViewProjectDto;
import cy.entities.project.ProjectEntity;
import cy.entities.project.UserViewProjectEntity;
import cy.models.project.UserViewProjectModel;
import cy.repositories.project.IProjectRepository;
import cy.repositories.project.IUserViewProjectRepository;
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
public class UserViewProjectImpl implements IUserViewProjectService {
    @Autowired
    IUserViewProjectRepository iUserViewProjectRepository;
    @Autowired
    IProjectRepository iProjectRepository;

    @Override
    public void add(UserViewProjectModel model) {
        Date now = Date.from(Instant.now());
        ProjectEntity project = iProjectRepository.findById(model.getObjectId()).get();
        iUserViewProjectRepository.save(new UserViewProjectEntity(null, model.getIdUser(),now,project));
    }

    @Override
    public List<UserViewProjectDto> findProjectRecentlyViewed() {
        return iUserViewProjectRepository.findProjectRecentlyViewed(SecurityUtils.getCurrentUserId()).stream().map(data -> UserViewProjectDto.toDto(data)).collect(Collectors.toList());
    }
}
