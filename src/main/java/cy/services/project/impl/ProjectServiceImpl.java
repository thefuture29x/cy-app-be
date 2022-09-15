package cy.services.project.impl;

import cy.dtos.attendance.RequestDeviceDto;
import cy.dtos.project.ProjectDto;
import cy.entities.UserEntity;
import cy.entities.attendance.RequestDeviceEntity;
import cy.entities.project.FileEntity;
import cy.entities.project.ProjectEntity;
import cy.models.project.ProjectModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.IFileRepository;
import cy.repositories.project.IProjectRepository;
import cy.services.project.IProjectService;
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
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;

    @Autowired
    EntityManager manager;

    @Override
    public ProjectDto findById(Long id) {
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
          if(!projectModel.getAvatar().isEmpty()){
              String urlAvatar =  fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
              FileEntity fileEntity =  new FileEntity();
              fileEntity.setCategory(Const.tableName.PROJECT.name());
              fileEntity.setUploadedBy(userEntity);
              fileEntity.setLink(urlAvatar);
              fileEntity.setObjectId(projectEntity.getId());
              fileEntity.setFileType(projectModel.getAvatar().getOriginalFilename());
              projectEntity.setAvatar(fileEntity);
              projectEntity = iProjectRepository.save(projectEntity);
          }
          if(projectModel.getFiles().length > 0){
              List<FileEntity> fileEntityList = new ArrayList<>();
              for (MultipartFile m : projectModel.getFiles()){
                  if(!m.isEmpty()){
                      String urlFile =  fileUploadProvider.uploadFile("project", m);
                      FileEntity fileEntity = new FileEntity();
                      fileEntity.setLink(urlFile);
                      fileEntity.setFileType(m.getOriginalFilename());
                      fileEntity.setCategory(Const.tableName.PROJECT.name());
                      fileEntity.setUploadedBy(userEntity);
                      fileEntity.setObjectId(projectEntity.getId());
                      fileEntityList.add(fileEntity);
                  }
              }
              if(fileEntityList.size() > 0){
                  projectEntity.setAttachFiles(fileEntityList);
                  projectEntity = iProjectRepository.save(projectEntity);
              }
          }
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
            projectEntity = iProjectRepository.save(projectEntity);
            if(!projectModel.getAvatar().isEmpty()){
                String urlAvatar =  fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
                FileEntity fileEntity =  new FileEntity();
                fileEntity.setCategory(Const.tableName.PROJECT.name());
                fileEntity.setUploadedBy(userEntity);
                fileEntity.setLink(urlAvatar);
                fileEntity.setObjectId(projectEntity.getId());
                fileEntity.setFileType(projectModel.getAvatar().getOriginalFilename());
                fileEntity =iFileRepository.save(fileEntity);
                projectEntity.setAvatar(fileEntity);
                projectEntity = iProjectRepository.save(projectEntity);
            }
            if(projectModel.getFiles().length > 0){
                for (MultipartFile m : projectModel.getFiles()){
                    if(!m.isEmpty()){
                        String urlFile =  fileUploadProvider.uploadFile("project", m);
                        FileEntity fileEntity = new FileEntity();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileType(m.getOriginalFilename());
                        fileEntity.setCategory(Const.tableName.PROJECT.name());
                        fileEntity.setUploadedBy(userEntity);
                        fileEntity.setObjectId(projectEntity.getId());
                        iFileRepository.save(fileEntity);
                    }
                }
            }
            return ProjectDto.toDto(projectEntity);
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public Boolean deleteProject(Long id) {
        try{
            iProjectRepository.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize, ProjectModel projectModel) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        String sql="SELECT new cy.dtos.project.ProjectDto(p) FROM ProjectEntity p WHERE 1=1 ";
        String countSQL = "select count(*) from ProjectEntity p where 1=1 ";

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

           }
           else{
               sql+=" AND (p.name LIKE %:textSearch% or p.createBy.fullName LIKE %:textSearch% ) ";
               countSQL+="AND (p.name LIKE %:textSearch% or p.createBy.fullName LIKE %:textSearch% ) ";
           }
        }
        sql+="order by r.createdDate desc";

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
            if( projectModel.getTextSearch().charAt(0) == '#'){

            }
            else{
                q.setParameter("textSearch", projectModel.getTextSearch());
                qCount.setParameter("textSearch", projectModel.getTextSearch());
            }
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<ProjectDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);
        return result;
    }
}