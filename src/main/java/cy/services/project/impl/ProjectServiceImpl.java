package cy.services.project.impl;

import cy.dtos.UserDto;
import cy.dtos.project.ProjectDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.ProjectModel;
import cy.models.project.TagModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.resources.UserResources;
import cy.services.project.*;
import cy.services.project.IHistoryLogService;
import cy.services.project.IProjectService;
import cy.services.project.ITagService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements IProjectService {
    @Autowired
    IProjectRepository iProjectRepository;
    @Autowired
    IFeatureRepository featureRepository;
    @Autowired
    IFeatureService featureService;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    EntityManager manager;

    @Autowired
    ITagService iTagService;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    IUserProjectRepository iUserProjectRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Override
    public ProjectDto findById(Long id) {
        ProjectEntity projectEntity = this.iProjectRepository.findById(id).orElse(null);
        ProjectDto projectDto = ProjectDto.toDto(iProjectRepository.findById(id).orElse(null));
        if(projectDto == null)
            return null;
        List<UserDto> userDev = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_DEV.name(), projectEntity.getId());
        List<UserDto> userFollow = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_FOLLOWER.name(), projectEntity.getId());
        List<UserDto> userView = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_VIEWER.name(), projectEntity.getId());
        projectDto.setUserView(userView);
        projectDto.setUserDevs(userDev);
        projectDto.setUserFollows(userFollow);
        return projectDto;
    }

    @Override
    public ProjectDto createProject(ProjectModel projectModel) {
      try {
          ProjectEntity projectEntity = new ProjectEntity();
          Long userId = SecurityUtils.getCurrentUserId();
          if(userId == null)
              return null;
          UserEntity userEntity = userRepository.findById(userId).orElse(null);
          projectEntity.setCreateBy(userEntity);
          Date currentDate = new Date();
          projectEntity.setCreatedDate(currentDate);
          projectEntity.setStartDate(projectModel.getStartDate());
          projectEntity.setEndDate(projectModel.getEndDate());
          projectEntity.setDescription(projectModel.getDescription());
          projectEntity.setName(projectModel.getName());
          projectEntity.setIsDefault(projectModel.getIsDefault());
          if(projectModel.getStartDate().before(currentDate)){
              projectEntity.setStatus(Const.status.IN_PROGRESS.name());
          }
          else {
              projectEntity.setStatus(Const.status.TO_DO.name());
          }
          projectEntity.setUpdatedDate(currentDate);
          projectEntity = iProjectRepository.save(projectEntity);
          if(projectModel.getUserDev() != null && projectModel.getUserDev().size() > 0){
              for (Long userDev : projectModel.getUserDev()){
                  UserEntity user = userRepository.findById(userDev).orElse(null);
                  if(user != null){
                      UserProjectEntity userProjectEntity = new UserProjectEntity();
                      userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                      userProjectEntity.setObjectId(projectEntity.getId());
                      userProjectEntity.setType(Const.type.TYPE_DEV.name());
                      userProjectEntity.setIdUser(user.getUserId());
                      iUserProjectRepository.save(userProjectEntity);
                  }
              }
          }
          if(projectModel.getUserFollow() != null && projectModel.getUserFollow().size() > 0){
              for (Long userFollow : projectModel.getUserFollow()){
                  UserEntity user = userRepository.findById(userFollow).orElse(null);
                  if(user != null){
                      UserProjectEntity userProjectEntity = new UserProjectEntity();
                      userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                      userProjectEntity.setObjectId(projectEntity.getId());
                      userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                      userProjectEntity.setIdUser(user.getUserId());
                      iUserProjectRepository.save(userProjectEntity);
                  }
              }
          }
          if(projectModel.getUserViewer() != null && projectModel.getUserViewer().size() > 0){
              for (Long userFollow : projectModel.getUserViewer()){
                  UserEntity user = userRepository.findById(userFollow).orElse(null);
                  if(user != null){
                      UserProjectEntity userProjectEntity = new UserProjectEntity();
                      userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                      userProjectEntity.setObjectId(projectEntity.getId());
                      userProjectEntity.setType(Const.type.TYPE_VIEWER.name());
                      userProjectEntity.setIdUser(user.getUserId());
                      iUserProjectRepository.save(userProjectEntity);
                  }
              }
          }
//          if(projectModel.getTags() != null && projectModel.getTags().size() > 0){
          if(projectModel.getTagArray() != null && projectModel.getTagArray().length > 0){
              for (String tagModel : projectModel.getTagArray()){
                  TagEntity tagEntity = iTagRepository.findByName(tagModel);
                  if(tagEntity == null){
                      TagEntity tagEntity1 = new TagEntity();
                      tagEntity1.setName(tagModel);
                      tagEntity1 =iTagRepository.save(tagEntity1);
                      TagRelationEntity tagRelationEntity = new TagRelationEntity();
                      tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                      tagRelationEntity.setIdTag(tagEntity1.getId());
                      tagRelationEntity.setObjectId(projectEntity.getId());
                      iTagRelationRepository.save(tagRelationEntity);
                  }
                  else if(tagEntity != null){
                      TagRelationEntity tagRelationEntity = new TagRelationEntity();
                      tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                      tagRelationEntity.setIdTag(tagEntity.getId());
                      tagRelationEntity.setObjectId(projectEntity.getId());
                      iTagRelationRepository.save(tagRelationEntity);
                  }
              }
          }
          if(projectModel.getAvatar() != null && !projectModel.getAvatar().isEmpty()){
              String urlAvatar =  fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
              FileEntity fileEntity =  new FileEntity();
              String fileName = projectModel.getAvatar().getOriginalFilename();
              fileEntity.setCategory(Const.tableName.PROJECT.name());
              fileEntity.setUploadedBy(userEntity);
              fileEntity.setLink(urlAvatar);
              fileEntity.setObjectId(projectEntity.getId());
              fileEntity.setFileName(fileName);
              fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
              projectEntity.setAvatar(fileEntity);
              projectEntity = iProjectRepository.save(projectEntity);
          }
          if(projectModel.getFiles() != null && projectModel.getFiles().length > 0){
              for (MultipartFile m : projectModel.getFiles()){
                  if(!m.isEmpty()){
                      String urlFile =  fileUploadProvider.uploadFile("project", m);
                      FileEntity fileEntity = new FileEntity();
                      String fileName = m.getOriginalFilename();
                      fileEntity.setLink(urlFile);
                      fileEntity.setFileName(fileName);
                      fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                      fileEntity.setCategory(Const.tableName.PROJECT.name());
                      fileEntity.setUploadedBy(userEntity);
                      fileEntity.setObjectId(projectEntity.getId());
                      iFileRepository.save(fileEntity);
                  }
              }
          }
          iHistoryLogService.logCreate(projectEntity.getId(), projectEntity, Const.tableName.PROJECT);
          return ProjectDto.toDto(projectEntity);
      }
      catch (Exception e){
          return null;
      }
    }
    @Override
    public ProjectDto updateProject(ProjectModel projectModel) {
        try {
            ProjectEntity projectEntity = iProjectRepository.findById(projectModel.getId()).orElse(null);
            ProjectEntity projectOriginal = (ProjectEntity) Const.copy(projectEntity);
            if(projectEntity == null)
                return null;
            Long userId = SecurityUtils.getCurrentUserId();
            if(userId == null)
                return null;
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            projectEntity.setCreateBy(userEntity);
            Date currentDate = new Date();
            projectEntity.setCreatedDate(currentDate);
            projectEntity.setStartDate(projectModel.getStartDate());
            projectEntity.setEndDate(projectModel.getEndDate());
            projectEntity.setDescription(projectModel.getDescription());
            projectEntity.setName(projectModel.getName());
            projectEntity.setIsDefault(projectModel.getIsDefault());
            if(projectModel.getStartDate().before(currentDate)){
                projectEntity.setStatus(Const.status.IN_PROGRESS.name());
            }
            else {
                projectEntity.setStatus(Const.status.TO_DO.name());
            }
            projectEntity.setUpdatedDate(currentDate);
            List<UserProjectEntity> userProjectEntities = iUserProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId());
            if(userProjectEntities != null && userProjectEntities.size()> 0){
                iUserProjectRepository.deleteAllInBatch(userProjectEntities);
            }
            if(projectModel.getUserDev() != null && projectModel.getUserDev().size() > 0){
                for (Long userDev : projectModel.getUserDev()){
                    UserEntity user = userRepository.findById(userDev).orElse(null);
                    if(user != null){
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_DEV.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if(projectModel.getUserFollow() != null && projectModel.getUserFollow().size() > 0){
                for (Long userFollow : projectModel.getUserFollow()){
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if(user != null){
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if(projectModel.getUserViewer() != null && projectModel.getUserViewer().size() > 0){
                for (Long userFollow : projectModel.getUserViewer()){
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if(user != null){
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_VIEWER.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }

            List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId());
            if(tagRelationEntities != null && tagRelationEntities.size() > 0){
                iTagRelationRepository.deleteAll(tagRelationEntities);
            }
//            if(projectModel.getTags() != null && projectModel.getTags().size() > 0){
            if(projectModel.getTagArray() != null && projectModel.getTagArray().length > 0){
                for (String tagModel : projectModel.getTagArray()){
                    TagEntity tagEntity = iTagRepository.findByName(tagModel);
                    if(tagEntity == null){
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel);
                        tagEntity1 =iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(projectEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                    else if(tagEntity != null){
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(projectEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }

            if(projectModel.getAvatar( ) != null && !projectModel.getAvatar().isEmpty()){
                String urlAvatar =  fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
                FileEntity fileEntity =  new FileEntity();
                String fileName = projectModel.getAvatar().getOriginalFilename();
                fileEntity.setCategory(Const.tableName.PROJECT.name());
                fileEntity.setUploadedBy(userEntity);
                fileEntity.setLink(urlAvatar);
                fileEntity.setObjectId(projectEntity.getId());
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                projectEntity.setAvatar(fileEntity);
                projectEntity = iProjectRepository.save(projectEntity);
            }
            if(projectEntity.getAttachFiles() != null && projectEntity.getAttachFiles().size() > 0)
                projectEntity.getAttachFiles().clear();
            else{
                projectEntity.setAttachFiles(new ArrayList<>());
            }
            if(projectModel.getFiles() != null && projectModel.getFiles().length > 0){
                for (MultipartFile m : projectModel.getFiles()){
                    if(!m.isEmpty()){
                        String urlFile =  fileUploadProvider.uploadFile("project", m);
                        FileEntity fileEntity = new FileEntity();
                        String fileName = m.getOriginalFilename();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                        fileEntity.setCategory(Const.tableName.PROJECT.name());
                        fileEntity.setUploadedBy(userEntity);
                        fileEntity.setObjectId(projectEntity.getId());
                        iFileRepository.saveAndFlush(fileEntity);
                        projectEntity.getAttachFiles().add(fileEntity);
                    }
                }
            }
            iProjectRepository.save(projectEntity);
            iHistoryLogService.logUpdate(projectEntity.getId(),projectOriginal,projectEntity, Const.tableName.PROJECT);
            return ProjectDto.toDto(projectEntity);
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public Boolean deleteProject(Long id) {
        // delete Feature
        List<FeatureEntity> featureEntities = this.featureRepository.findByProjectId(id);
        featureEntities.forEach(feature -> this.featureService.deleteById(feature.getId()));

        // delete userProject
        List<UserProjectEntity> userProjectEntities = this.iUserProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), id);
        for (UserProjectEntity userProjectEntity : userProjectEntities) {
            this.iUserProjectRepository.delete(userProjectEntity);
        }
        //delete tag_relation
        List<TagRelationEntity> tagRelationEntities =  this.iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), id);
        for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
            this.iTagRelationRepository.delete(tagRelationEntity);
        }
        // delete file
        iFileRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), id).stream().forEach(fileEntity -> this.fileService.deleteById(fileEntity.getId()));
        // delete Project
        this.iProjectRepository.deleteById(id);

        return true;
    }

    @Override
    public Boolean changIsDeleteById(Long id) {
        ProjectEntity oldProject = this.iProjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not exist!!"));
        oldProject.setIsDeleted(true);
        this.iProjectRepository.saveAndFlush(oldProject);
        iHistoryLogService.logDelete(id,oldProject, Const.tableName.PROJECT);
        return true;
    }

    @Override
    public Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize, ProjectModel projectModel) {
        Long userIdd = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        String sql="SELECT distinct new cy.dtos.project.ProjectDto(p) FROM ProjectEntity p " +
                "inner join UserProjectEntity up on up.objectId = p.id " ;
        String countSQL = "select count(distinct(p)) from ProjectEntity p  " +
                "inner join UserProjectEntity up on up.objectId = p.id " ;
        if(projectModel.getTextSearch() != null && projectModel.getTextSearch().charAt(0) == '#'){
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE (up.category like 'PROJECT') and (up.idUser = :currentUserId) ";
        countSQL += " WHERE (up.category like 'PROJECT') and (up.idUser = :currentUserId) ";
        if(projectModel.getStatus()!= null) {
            sql+=" AND p.status = :status ";
            countSQL+=" AND p.status = :status ";
        }
        if(projectModel.getStartDate() != null){
            sql+=" AND p.startDate >= :startDate ";
            countSQL+="AND p.startDate >= :startDate ";
        }
        if(projectModel.getEndDate() != null){
            sql+=" AND p.endDate <= :endDate ";
            countSQL+="AND p.endDate <= :endDate ";
        }
        if(projectModel.getTextSearch() != null){
           if(projectModel.getTextSearch().charAt(0) == '#'){
               sql+=" AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'PROJECT') ";
               countSQL+="AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'PROJECT') ";
           }
           else{
               sql+=" AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
               countSQL+="AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
           }
        }
        sql+="order by p.createdDate desc";

        Query q = manager.createQuery(sql, ProjectDto.class);
        Query qCount = manager.createQuery(countSQL);

        q.setParameter("currentUserId",userIdd);
        qCount.setParameter("currentUserId",userIdd);

        if(projectModel.getStatus() != null){
            q.setParameter("status", projectModel.getStatus());
            qCount.setParameter("status", projectModel.getStatus());
        }
        if(projectModel.getStartDate() != null){
            q.setParameter("startDate", projectModel.getStartDate());
            qCount.setParameter("startDate", projectModel.getStartDate());
        }
        if(projectModel.getEndDate() != null){
            q.setParameter("endDate", projectModel.getEndDate());
            qCount.setParameter("endDate", projectModel.getEndDate());
        }
        if(projectModel.getTextSearch() != null){
            q.setParameter("textSearch", "%" + projectModel.getTextSearch() + "%");
            qCount.setParameter("textSearch", "%" + projectModel.getTextSearch() + "%");
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<ProjectDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);
        return result;
    }
}
