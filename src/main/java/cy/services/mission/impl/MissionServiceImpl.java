package cy.services.mission.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.common.UserDto;
import cy.dtos.common.UserMetaDto;
import cy.dtos.project.ProjectDto;
import cy.entities.common.*;
import cy.entities.project.FeatureEntity;
import cy.entities.project.ProjectEntity;
import cy.models.project.ProjectModel;
import cy.models.project.UserViewProjectModel;
import cy.repositories.common.*;
import cy.repositories.project.IFeatureRepository;
import cy.repositories.project.IProjectRepository;
import cy.services.common.IFileService;
import cy.services.common.IHistoryLogService;
import cy.services.common.ITagService;
import cy.services.mission.IMissionService;
import cy.services.project.IFeatureService;
import cy.services.project.IProjectService;
import cy.services.project.IUserViewProjectService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MissionServiceImpl implements IMissionService {

}
