package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.UserDto;
import cy.dtos.project.ProjectDto;
import cy.dtos.project.TaskDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.ProjectModel;
import cy.models.project.TaskSearchModel;
import cy.models.project.UserViewProjectModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.*;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    IUserViewProjectService iUserViewProjectService;

    @Override
    public ProjectDto findById(Long id) {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        iUserViewProjectService.add(new UserViewProjectModel(userEntity.getUserId(), id));
        ProjectEntity projectEntity = this.iProjectRepository.findById(id).orElse(null);
        ProjectDto projectDto = ProjectDto.toDto(iProjectRepository.findById(id).orElse(null));
        if (projectDto == null)
            return null;
        List<UserDto> userDev = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_DEV.name(), projectEntity.getId());
        List<UserDto> userFollow = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_FOLLOWER.name(), projectEntity.getId());
        List<UserDto> userView = userRepository.getByCategoryAndTypeAndObjectid(Const.tableName.PROJECT.name(), Const.type.TYPE_VIEWER.name(), projectEntity.getId());
        projectDto.setUserView(userView);
        projectDto.setUserDevs(userDev);
        projectDto.setUserFollows(userFollow);
        projectDto.setTagArray(iTagRelationRepository.getNameTagByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId()));
        return projectDto;
    }

    @Override
    public ProjectDto createProject(ProjectModel projectModel) throws IOException {
            ProjectEntity projectEntity = new ProjectEntity();
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null)
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
            if (projectModel.getStartDate().before(currentDate)) {
                projectEntity.setStatus(Const.status.IN_PROGRESS.name());
            } else {
                projectEntity.setStatus(Const.status.TO_DO.name());
            }
            projectEntity.setUpdatedDate(currentDate);
            projectEntity = iProjectRepository.save(projectEntity);

            if (!projectModel.getUserDev().stream().anyMatch(userId::equals)){
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                userProjectEntity.setObjectId(projectEntity.getId());
                userProjectEntity.setType(Const.type.TYPE_DEV.name());
                userProjectEntity.setIdUser(userId);
                iUserProjectRepository.save(userProjectEntity);
            }

            if (projectModel.getUserDev() != null && projectModel.getUserDev().size() > 0) {
                for (Long userDev : projectModel.getUserDev()) {
                    UserEntity user = userRepository.findById(userDev).orElse(null);
                    if (user != null) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_DEV.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if (projectModel.getUserFollow() != null && projectModel.getUserFollow().size() > 0) {
                for (Long userFollow : projectModel.getUserFollow()) {
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if (user != null) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if (projectModel.getUserViewer() != null && projectModel.getUserViewer().size() > 0) {
                for (Long userFollow : projectModel.getUserViewer()) {
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if (user != null) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_VIEWER.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if (projectModel.getTagArray() != null && projectModel.getTagArray().length > 0) {
                for (String tagModel : projectModel.getTagArray()) {
                    TagEntity tagEntity = iTagRepository.findByName(tagModel);
                    if (tagEntity == null) {
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel);
                        tagEntity1 = iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(projectEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    } else if (tagEntity != null) {
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(projectEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }
//            if (projectModel.getAvatar() != null && !projectModel.getAvatar().isEmpty()) {
            if (projectModel.getAvatar() != null) {
                String urlAvatar = fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
                FileEntity fileEntity = new FileEntity();
                String fileName = projectModel.getAvatar().getOriginalFilename();
                fileEntity.setCategory(Const.tableName.PROJECT.name());
                fileEntity.setUploadedBy(userEntity);
                fileEntity.setLink(urlAvatar);
//                fileEntity.setObjectId(projectEntity.getId());
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                projectEntity.setAvatar(fileEntity);
                projectEntity = iProjectRepository.save(projectEntity);
            }
//            if (projectModel.getFiles() != null && projectModel.getFiles().length > 0) {
            if (projectModel.getFiles() != null) {
                for (MultipartFile m : projectModel.getFiles()) {
                    if (!m.isEmpty()) {
                        String urlFile = fileUploadProvider.uploadFile("project", m);
                        FileEntity fileEntity = new FileEntity();
                        String fileName = m.getOriginalFilename();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                        fileEntity.setCategory(Const.tableName.PROJECT.name());
                        fileEntity.setUploadedBy(userEntity);
                        fileEntity.setObjectId(projectEntity.getId());
                        iFileRepository.saveAndFlush(fileEntity);
                    }
                }
            }
            iHistoryLogService.logCreate(projectEntity.getId(), projectEntity, Const.tableName.PROJECT, projectEntity.getName());
            ProjectDto result = ProjectDto.toDto(projectEntity);
            return result;
    }

    @Override
    public ProjectDto updateProject(ProjectModel projectModel) throws IOException, ParseException {
            List<String> fileUrlsKeeping = new ArrayList<>();
            List<FileEntity> fileOriginal = iFileRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectModel.getId());
            if (projectModel.getFileUrlsKeeping() != null){
                projectModel.getFileUrlsKeeping().stream().map(url -> fileUrlsKeeping.add(url)).collect(Collectors.toList());
            }
            if (projectModel.getAvatarUrl() != null){
                fileUrlsKeeping.add(projectModel.getAvatarUrl());
            }
            
            if (fileUrlsKeeping.size() > 0){
                iFileRepository.deleteFileExistInObject(fileUrlsKeeping, Const.tableName.PROJECT.name(), projectModel.getId());
            }
            else {
                iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectModel.getId());
            }

            ProjectEntity projectEntity = iProjectRepository.findById(projectModel.getId()).orElse(null);
            ProjectEntity projectOriginal = (ProjectEntity) Const.copy(projectEntity);

            Set<Long> currentProjectUIDs = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(),Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
            Set<Long> currentProjectIdFollows = iUserProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(),Const.type.TYPE_FOLLOWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
            int countError = 0;
            if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)) {
                countError += 1;
            }
            if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectIdFollows::contains)) {
                countError += 1;
            }
            if(SecurityUtils.getCurrentUserId() != projectEntity.getCreateBy().getUserId()){
                countError += 1;
            }
            if (countError == 3){
                throw new CustomHandleException(11);
            }

            List<UserEntity> listUserDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_DEV.name(), projectEntity.getId());
            List<UserEntity> listUserFollow = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_FOLLOWER.name(), projectEntity.getId());
            List<UserEntity> listUserView = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_VIEWER.name(), projectEntity.getId());
            List<TagEntity> listTag = iTagRepository.getAllByObjectIdAndCategory(projectEntity.getId(),Const.tableName.PROJECT.name());
            projectOriginal.setViewTeam(listUserView);
            projectOriginal.setDevTeam(listUserDev);
            projectOriginal.setFollowTeam(listUserFollow);
            projectOriginal.setTagList(listTag);
            projectOriginal.setAttachFiles(fileOriginal);

            if (projectEntity == null)
                return null;
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null)
                return null;
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            projectEntity.setCreateBy(userEntity);
            // Format date get only yyyy-MM-dd
            Date currentDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String currentDateString = simpleDateFormat.format(currentDate);
            Date currentDateCheck = simpleDateFormat.parse(currentDateString);

            String startDateString = simpleDateFormat.format(projectModel.getStartDate());
            Date startDateCheck = simpleDateFormat.parse(startDateString);
            // End format date get only yyyy-MM-dd

            projectEntity.setCreatedDate(currentDate);
            projectEntity.setStartDate(projectModel.getStartDate());
            projectEntity.setEndDate(projectModel.getEndDate());
            projectEntity.setDescription(projectModel.getDescription());
            projectEntity.setName(projectModel.getName());
            projectEntity.setIsDefault(projectModel.getIsDefault());

            if (startDateCheck.before(currentDateCheck)) {
                projectEntity.setStatus(Const.status.IN_PROGRESS.name());
            } else {
                projectEntity.setStatus(Const.status.TO_DO.name());
            }
            projectEntity.setUpdatedDate(currentDate);
            List<UserProjectEntity> userProjectEntities = iUserProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId());
            if (userProjectEntities != null && userProjectEntities.size() > 0) {
                iUserProjectRepository.deleteAllInBatch(userProjectEntities);
            }

            if (!projectModel.getUserDev().stream().anyMatch(userId::equals)){
                UserProjectEntity userProjectEntity = new UserProjectEntity();
                userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                userProjectEntity.setObjectId(projectEntity.getId());
                userProjectEntity.setType(Const.type.TYPE_DEV.name());
                userProjectEntity.setIdUser(userId);
                iUserProjectRepository.save(userProjectEntity);
            }


            if (projectModel.getUserDev() != null && projectModel.getUserDev().size() > 0) {
                for (Long userDev : projectModel.getUserDev()) {
                    UserEntity user = userRepository.findById(userDev).orElse(null);
                    if (user != null) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_DEV.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if (projectModel.getUserFollow() != null && projectModel.getUserFollow().size() > 0) {
                for (Long userFollow : projectModel.getUserFollow()) {
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if (user != null) {
                        UserProjectEntity userProjectEntity = new UserProjectEntity();
                        userProjectEntity.setCategory(Const.tableName.PROJECT.name());
                        userProjectEntity.setObjectId(projectEntity.getId());
                        userProjectEntity.setType(Const.type.TYPE_FOLLOWER.name());
                        userProjectEntity.setIdUser(user.getUserId());
                        iUserProjectRepository.save(userProjectEntity);
                    }
                }
            }
            if (projectModel.getUserViewer() != null && projectModel.getUserViewer().size() > 0) {
                for (Long userFollow : projectModel.getUserViewer()) {
                    UserEntity user = userRepository.findById(userFollow).orElse(null);
                    if (user != null) {
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
            iTagRelationRepository.deleteAll(tagRelationEntities);
            if (projectModel.getTagArray() != null && projectModel.getTagArray().length > 0) {
                for (String tagModel : projectModel.getTagArray()) {
                    TagEntity tagEntity1 = new TagEntity();
                    tagEntity1.setName(tagModel);
                    tagEntity1 = iTagRepository.save(tagEntity1);
                    TagRelationEntity tagRelationEntity = new TagRelationEntity();
                    tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                    tagRelationEntity.setIdTag(tagEntity1.getId());
                    tagRelationEntity.setObjectId(projectEntity.getId());
                    iTagRelationRepository.save(tagRelationEntity);
                }
            }

            if (projectEntity.getAvatar() != null){
                iFileRepository.delete(projectEntity.getAvatar());
            }

            if (projectModel.getAvatar() != null && !projectModel.getAvatar().isEmpty()) {
                String urlAvatar = fileUploadProvider.uploadFile("avatar", projectModel.getAvatar());
                FileEntity fileEntity = new FileEntity();
                String fileName = projectModel.getAvatar().getOriginalFilename();
                fileEntity.setCategory(Const.tableName.PROJECT.name());
                fileEntity.setUploadedBy(userEntity);
                fileEntity.setLink(urlAvatar);
//                fileEntity.setObjectId(projectEntity.getId());
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                projectEntity.setAvatar(fileEntity);
//                projectEntity = iProjectRepository.save(projectEntity);
            } else {
                if (projectModel.getAvatarUrl() != null) {
                    if(projectOriginal.getAvatar().getLink().equals(projectModel.getAvatarUrl())){
                        projectEntity.setAvatar(projectOriginal.getAvatar());
                    }
                }
            }

            if (projectModel.getFiles() != null && projectModel.getFiles().length > 0) {
                for (MultipartFile m : projectModel.getFiles()) {
                    if (!m.isEmpty()) {
                        String urlFile = fileUploadProvider.uploadFile("project", m);
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
            iProjectRepository.saveAndFlush(projectEntity);
            List<UserEntity> userDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_DEV.name(), projectEntity.getId());
            List<UserEntity> userFollow = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_FOLLOWER.name(), projectEntity.getId());
            List<UserEntity> userView = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.PROJECT.name(), Const.type.TYPE_VIEWER.name(), projectEntity.getId());
            List<TagEntity> listTagEntity = iTagRepository.getAllByObjectIdAndCategory(projectEntity.getId(),Const.tableName.PROJECT.name());

            projectEntity.setViewTeam(userView);
            projectEntity.setDevTeam(userDev);
            projectEntity.setFollowTeam(userFollow);
            projectEntity.setTagList(listTagEntity);

            iHistoryLogService.logUpdate(projectEntity.getId(), projectOriginal,projectEntity, Const.tableName.PROJECT);
            ProjectDto result = ProjectDto.toDto(projectEntity);
            return result;
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
        List<TagRelationEntity> tagRelationEntities = this.iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), id);
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
        iHistoryLogService.logDelete(id, oldProject, Const.tableName.PROJECT);
        return true;
    }

    @Override
    public Page<ProjectDto> findByPage(Integer pageIndex, Integer pageSize, ProjectModel projectModel) {
        Long userIdd = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String sql = "SELECT distinct new cy.dtos.project.ProjectDto(p) FROM ProjectEntity p " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        String countSQL = "select count(distinct(p)) from ProjectEntity p  " +
                "inner join UserProjectEntity up on up.objectId = p.id ";
        if (projectModel.getTextSearch() != null && projectModel.getTextSearch().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }
        sql += " WHERE (up.category like 'PROJECT') AND p.isDeleted = false ";
        countSQL += " WHERE (up.category like 'PROJECT') AND p.isDeleted = false ";

        if (projectModel.getTypeUser() != null){
            sql+= " AND up.type = :typeUser";
            countSQL+= " AND up.type = :typeUser";
        }else {
            sql+= " AND up.type is not null";
            countSQL+= " AND up.type is not null";
        }

        if (projectModel.getOtherProject()){
            sql += " and (up.idUser <> :currentUserId) ";
            countSQL += " and (up.idUser <> :currentUserId) ";
        }else {
            sql += " and (up.idUser = :currentUserId) ";
            countSQL += " and (up.idUser = :currentUserId) ";
        }

        if (projectModel.getStatus() != null) {
            sql += " AND p.status = :status ";
            countSQL += " AND p.status = :status ";
        }
        if (projectModel.getMonthFilter() != null) {
            sql += " AND MONTH(p.startDate) = :monthFilter ";
            countSQL += "AND MONTH(p.startDate) = :monthFilter ";
        }
        if (projectModel.getYearFilter() != null) {
            sql += " AND YEAR(p.startDate) = :yearFilter ";
            countSQL += "AND YEAR(p.startDate) = :yearFilter ";
        }
        if (projectModel.getTextSearch() != null) {
            if (projectModel.getTextSearch().charAt(0) == '#') {
                sql += " AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'PROJECT') ";
                countSQL += "AND (t.name LIKE :textSearch ) AND (tr.category LIKE 'PROJECT') ";
            } else {
                sql += " AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
                countSQL += "AND (p.name LIKE :textSearch or p.createBy.fullName LIKE :textSearch ) ";
            }
        }
        sql += "order by p.updatedDate desc";

        Query q = manager.createQuery(sql, ProjectDto.class);
        Query qCount = manager.createQuery(countSQL);

        q.setParameter("currentUserId", userIdd);
        qCount.setParameter("currentUserId", userIdd);

        if (projectModel.getStatus() != null) {
            q.setParameter("status", projectModel.getStatus());
            qCount.setParameter("status", projectModel.getStatus());
        }
        if (projectModel.getMonthFilter() != null) {
            q.setParameter("monthFilter", Integer.parseInt(projectModel.getMonthFilter()) );
            qCount.setParameter("monthFilter", Integer.parseInt(projectModel.getMonthFilter()));
        }
        if (projectModel.getYearFilter() != null) {
            q.setParameter("yearFilter", Integer.parseInt(projectModel.getYearFilter()));
            qCount.setParameter("yearFilter", Integer.parseInt(projectModel.getYearFilter()));
        }
        if (projectModel.getTextSearch() != null) {
            q.setParameter("textSearch", "%" + projectModel.getTextSearch() + "%");
            qCount.setParameter("textSearch", "%" + projectModel.getTextSearch() + "%");
        }
        if (projectModel.getTypeUser() != null) {
            q.setParameter("typeUser", projectModel.getTypeUser());
            qCount.setParameter("typeUser", projectModel.getTypeUser());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        Long numberResult = (Long) qCount.getSingleResult();
        Page<ProjectDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);
        return result;
    }
}
