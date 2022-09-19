package cy.services.project.impl;

import cy.dtos.TagDto;
import cy.dtos.attendance.RequestDeviceDto;
import cy.dtos.project.ProjectDto;
import cy.entities.UserEntity;
import cy.entities.attendance.NotificationEntity;
import cy.entities.attendance.RequestDeviceEntity;
import cy.entities.project.*;
import cy.models.project.ProjectModel;
import cy.models.project.TagModel;
import cy.repositories.IUserRepository;
import cy.repositories.attendance.INotificationRepository;
import cy.repositories.project.*;
import cy.resources.UserResources;
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
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements IProjectService {
    @Autowired
    IProjectRepository iProjectRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;

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
        return ProjectDto.toDto(iProjectRepository.findById(id).orElse(null));
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
          if(projectModel.getTags() != null && projectModel.getTags().size() > 0){
              for (TagModel tagModel : projectModel.getTags()){
                  TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                  if(tagEntity == null){
                      TagEntity tagEntity1 = new TagEntity();
                      tagEntity1.setName(tagModel.getName());
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
          System.out.println(e);
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
                iUserProjectRepository.deleteAll(userProjectEntities);
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
            if(projectModel.getTags() != null && projectModel.getTags().size() > 0){
                for (TagModel tagModel : projectModel.getTags()){
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if(tagEntity == null){
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
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
            //iHistoryLogService.logUpdate(projectEntity.getId(),projectOriginal,projectEntity, Const.tableName.PROJECT);
            return ProjectDto.toDto(projectEntity);
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public Boolean deleteProject(Long id) {
        try{
//            iProjectRepository.deleteById(id);
            ProjectEntity projectEntity = iProjectRepository.findById(id).orElse(null);
            if(projectEntity == null)
                return false;
            projectEntity.setIsDeleted(true);
            iProjectRepository.save(projectEntity);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize, ProjectModel projectModel) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        String sql="SELECT distinct new cy.dtos.project.ProjectDto(p) FROM ProjectEntity p ";
        String countSQL = "select count(distinct(p)) from ProjectEntity p  ";
        if(projectModel.getTextSearch() != null && projectModel.getTextSearch().charAt(0) == '#'){
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE 1=1 ";
        countSQL += " WHERE 1=1 ";
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
