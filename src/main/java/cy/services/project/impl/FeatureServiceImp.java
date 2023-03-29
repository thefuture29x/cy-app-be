package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.ProjectDto;
import cy.dtos.project.TagDto;
import cy.dtos.project.FeatureDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.*;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.*;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeatureServiceImp implements IFeatureService {
    @Autowired
    IFeatureRepository featureRepository;
    @Autowired
    IFileRepository fileRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    IProjectRepository projectRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IUserProjectRepository userProjectRepository;
    @Autowired
    ITagService tagService;
    @Autowired
    ITagRelationService tagRelationService;
    @Autowired
    ITagRelationRepository tagRelationRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Autowired
    ITaskService taskService;
    @Autowired
    ITaskRepository taskRepository;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    EntityManager manager;


    @Override
    public List<FeatureDto> findAll() {
        return FeatureDto.toListDto(featureRepository.findAll());
    }

    @Override
    public Page<FeatureDto> findAll(Pageable page) {
        return this.featureRepository.findAll(page).map(FeatureDto::toDto);
    }

    @Override
    public List<FeatureDto> findAll(Specification<FeatureEntity> specs) {
        return this.featureRepository.findAll(specs).stream().map(FeatureDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<FeatureDto> filter(Pageable page, Specification<FeatureEntity> specs) {
        return this.featureRepository.findAll(specs, page).map(FeatureDto::toDto);
    }

    @Override
    public FeatureDto findById(Long id) {
        if (featureRepository.checkIsDeleted(id)) throw new CustomHandleException(491);
        FeatureEntity featureEntity = this.featureRepository.findById(id).orElseThrow(() -> new CustomHandleException(23));
        featureEntity.setTagList(tagRelationService.findTagByCategoryAndObject(Const.tableName.FEATURE.name(), id).stream().map(x -> x.getIdTag()).collect(Collectors.toList()).stream().map(y -> this.tagService.getById(y)).collect(Collectors.toList()));
        featureEntity.setDevTeam(userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.FEATURE.name(), id, Const.type.TYPE_DEV.name()).stream().map(y -> this.userRepository.findById(y.getIdUser()).orElseThrow(() -> new CustomHandleException(232))).collect(Collectors.toList()));
        featureEntity.setFollowTeam(userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.FEATURE.name(), featureEntity.getId(), Const.type.TYPE_FOLLOWER.name()).stream().map(y -> this.userRepository.findById(y.getIdUser()).orElseThrow(() -> new CustomHandleException(232))).collect(Collectors.toList()));
        featureEntity.setViewTeam(userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.FEATURE.name(), featureEntity.getId(), Const.type.TYPE_VIEWER.name()).stream().map(y -> this.userRepository.findById(y.getIdUser()).orElseThrow(() -> new CustomHandleException(232))).collect(Collectors.toList()));
        return FeatureDto.toDto(this.getById(id));
    }

    @Override
    public FeatureEntity getById(Long id) {
        return this.featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature not exist!!"));
    }

    @Override
    public FeatureDto add(FeatureModel model) {
        Long userId = SecurityUtils.getCurrentUserId();
        // check the user is on the project's dev list
        List<Long> listIdDevInProject = userProjectRepository.getIdByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), model.getPid(),Const.type.TYPE_DEV.name());
        if(!listIdDevInProject.stream().anyMatch(userId::equals)) {
            throw new CustomHandleException(5);
        }

        // check name already exists
        if (featureRepository.getAllByNameAndIsDeletedAndProject_Id(model.getName(), false, model.getPid()).size() > 0)
            throw new CustomHandleException(190);

        ProjectEntity projectEntity = this.projectRepository.findById(model.getPid()).orElseThrow(() -> new CustomHandleException(45354345));
        Set<Long> currentProjectUIDs = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        Set<Long> currentProjectIdFollows = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_FOLLOWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        int countError = 0;
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)) {
            countError += 1;
        }
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectIdFollows::contains)) {
            countError += 1;
        }
        if (SecurityUtils.getCurrentUserId() != projectEntity.getCreateBy().getUserId()) {
            countError += 1;
        }
        if (countError == 3) {
            throw new CustomHandleException(11);
        }
        List<TagEntity> tagList = new ArrayList<>();
        for (String tag : model.getTagList()
        ) {
            TagDto thisTag = this.tagService.add(TagModel.builder().name(tag).build());
            tagList.add(TagEntity.builder().id(thisTag.getId()).name(thisTag.getName()).build());
        }
        //Add Files
        List<MultipartFile> files = model.getFiles();
        List<FileEntity> fileEntities = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files
            ) {
                FileModel model1 = new FileModel();
                model1.setFile(file);
//            model1.setObjectId(entity.getId());
                model1.setCategory(Const.tableName.FEATURE.name());
                fileEntities.add(this.fileService.addEntity(model1));
            }
        }


        FeatureEntity entity = (FeatureEntity) FeatureEntity.builder().
                startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .createBy(SecurityUtils.getCurrentUser().getUser())
                .description(model.getDescription())
                .name(model.getName())
                .project(projectEntity)
                .attachFiles(fileEntities)
                .tagList(tagList)
                .isDeleted(false)
                .isDefault(model.getIsDefault())
//                .priority(model.getPriority().name())
                .build();

        entity.setStatus(Const.status.TO_DO.name());
        this.featureRepository.saveAndFlush(entity);

        //Add Tags
        tagList.stream().forEach(x -> this.tagRelationService.add(TagRelationModel.builder().idTag(x.getId()).category(Const.tableName.FEATURE.name()).objectId(entity.getId()).build()));

        // add user to dev list if user doesn't choose his role

        List<Long> listIdDev = model.getUids() != null ? model.getUids() : new ArrayList<>();
        List<Long> listIdFollower = model.getUserFollow() != null ? model.getUserFollow() : new ArrayList<>();

//        if (!listIdDev.stream().anyMatch(userId::equals)) {
//            if (!listIdFollower.stream().anyMatch(userId::equals)) {
//                UserProjectEntity userProjectEntity = new UserProjectEntity();
//                userProjectEntity.setCategory(Const.tableName.FEATURE.name());
//                userProjectEntity.setObjectId(entity.getId());
//                userProjectEntity.setType(Const.type.TYPE_DEV.name());
//                userProjectEntity.setIdUser(userId);
//                userProjectRepository.save(userProjectEntity);
//            }
//        }

        //Add Users


        listIdDev.stream().forEach(x -> {
            this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(entity.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_DEV.name()).build());

            // add dev to project
            if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_DEV.name(), x).size() == 0) {
                UserProjectEntity userProjectPro = this.addDev(x, Const.type.TYPE_DEV.name(), Const.tableName.PROJECT.name());
                userProjectPro.setObjectId(projectEntity.getId());
                this.userProjectRepository.saveAndFlush(userProjectPro);
            }
        });
        entity.setDevTeam(listIdDev.stream().map(x -> this.userRepository.findById(x).orElseThrow(() -> new CustomHandleException(2))).collect(Collectors.toList()));


        listIdFollower.stream().forEach(x -> this.userProjectRepository.save(UserProjectEntity.builder()
                .idUser(x)
                .objectId(entity.getId())
                .category(Const.tableName.FEATURE.name())
                .type(Const.type.TYPE_FOLLOWER.name())
                .build()));
        entity.setFollowTeam(listIdFollower.stream().map(x -> this.userRepository.findById(x).orElseThrow(() -> new CustomHandleException(2))).collect(Collectors.toList()));


//        entity.setProject();
        iHistoryLogService.logCreate(entity.getId(), entity, Const.tableName.FEATURE, entity.getName());
        return FeatureDto.toDto(entity);
    }

    public UserProjectEntity addDev(Long id, String type, String category) {
        // objectId not save yet => be will add task
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        UserProjectEntity userProject = UserProjectEntity.builder()
                .idUser(userEntity.getUserId())
                .type(type)
                .category(category)
                .build();

        return this.userProjectRepository.saveAndFlush(userProject);
    }

    @Override
    public List<FeatureDto> add(List<FeatureModel> model) {
        return null;
    }

    @Override
    public FeatureDto update(FeatureModel model) {
        if (featureRepository.checkIsDeleted(model.getId())) throw new CustomHandleException(491);

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(model.getId(), listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }

        List<FileEntity> fileOriginal = iFileRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), model.getId());
        //Clear old files
        if (model.getFileUrlsKeeping() != null) {
            iFileRepository.deleteFileExistInObject(model.getFileUrlsKeeping(), Const.tableName.FEATURE.name(), model.getId());
        } else {
            iFileRepository.deleteAllByCategoryAndObjectId(Const.tableName.FEATURE.name(), model.getId());
        }
        FeatureEntity oldFeature = this.featureRepository.findById(model.getId()).orElseThrow(() -> new CustomHandleException(232));
        // check name already exists
        if (!oldFeature.getName().equals(model.getName())){
            if (featureRepository.getAllByNameAndIsDeletedAndProject_Id(model.getName(), false, model.getPid()).size() > 0)
                throw new CustomHandleException(190);
        }
        FeatureEntity featureOriginal = (FeatureEntity) Const.copy(oldFeature);
        List<UserEntity> listUserDev = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.FEATURE.name(), Const.type.TYPE_DEV.name(), model.getId());
        List<UserEntity> listUserFollow = userRepository.getAllByCategoryAndTypeAndObjectId(Const.tableName.FEATURE.name(), Const.type.TYPE_FOLLOWER.name(), model.getId());
        List<TagEntity> listTag = iTagRepository.getAllByObjectIdAndCategory(model.getId(), Const.tableName.FEATURE.name());
        featureOriginal.setDevTeam(listUserDev);
        featureOriginal.setFollowTeam(listUserFollow);
        featureOriginal.setTagList(listTag);
        featureOriginal.setAttachFiles(fileOriginal);

        ProjectEntity projectEntity = oldFeature.getProject();
        Set<Long> currentProjectUIDs = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
//        Set<Long> currentProjectIdFollows = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_FOLLOWER.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toSet());
        int countError = 0;
        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)) {
            countError += 1;
        }
//        if (Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectIdFollows::contains)) {
//            countError += 1;
//        }
        if (SecurityUtils.getCurrentUserId() != projectEntity.getCreateBy().getUserId()) {
            countError += 1;
        }
        if (countError == 2) {
            throw new CustomHandleException(11);
        }

        oldFeature.setName(model.getName());
        oldFeature.setDescription(model.getDescription());
        oldFeature.setStartDate(model.getStartDate());
        oldFeature.setEndDate(model.getEndDate());
        oldFeature.setIsDefault(model.getIsDefault());

        oldFeature.setStatus(Const.status.TO_DO.name());
//        oldFeature.setPriority(model.getPriority().name());
        //Clear old tags
        clearTagList(oldFeature);
        //Add new tags
        List<String> newTagList = model.getTagList() != null ? model.getTagList() : new ArrayList<>();
        List<TagEntity> newTagEntityList = new ArrayList<>();
        if (newTagList != null) {
            newTagList.stream().forEach(x -> {
                TagDto tagDto = this.tagService.add(TagModel.builder().name(x).build());
                newTagEntityList.add(TagEntity.builder().id(tagDto.getId()).name(tagDto.getName()).build());
                this.tagRelationService.add(TagRelationModel.builder().idTag(tagDto.getId()).category(Const.tableName.FEATURE.name()).objectId(oldFeature.getId()).build());
            });
        }
        oldFeature.setTagList(newTagEntityList);

        //Add new files
        List<MultipartFile> newFileList = model.getFiles();
        if (newFileList != null) {
            newFileList.stream().forEach(x -> {
                FileModel fileModel = new FileModel();
                fileModel.setFile(x);
                fileModel.setObjectId(oldFeature.getId());
                fileModel.setCategory(Const.tableName.FEATURE.name());
                oldFeature.getAttachFiles().add(this.fileService.addEntity(fileModel));

            });
        } 

        clearDevTeam(oldFeature.getId());
//        List<Long> currentAvailableDev = userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_DEV.name()).stream().map(x -> x.getIdUser()).collect(Collectors.toList());
        List<Long> newDevTeam = model.getUids() != null ? model.getUids() : new ArrayList<>();
        List<UserEntity> newDevTeamEntity = new ArrayList<>();
        if (model.getUids() != null) {
            newDevTeam.stream().forEach(x -> {
                newDevTeamEntity.add(this.userRepository.findById(x).orElseThrow(() -> new CustomHandleException(2)));
                // add dev to feature
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.FEATURE.name(), oldFeature.getId(), Const.type.TYPE_DEV.name(), x).size() == 0) {
                    this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(oldFeature.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_DEV.name()).build());
                }
                // add dev to project
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.PROJECT.name(), projectEntity.getId(), Const.type.TYPE_DEV.name(), x).size() == 0) {
                    UserProjectEntity userProjectPro = this.addDev(x, Const.type.TYPE_DEV.name(), Const.tableName.PROJECT.name());
                    userProjectPro.setObjectId(projectEntity.getId());
                    this.userProjectRepository.saveAndFlush(userProjectPro);
                }
            });
            oldFeature.setDevTeam(newDevTeamEntity);
        }

        List<Long> newFollowTeam = model.getUserFollow() != null ? model.getUserFollow() : new ArrayList<>();
        List<UserEntity> newFollowTeamEntity = new ArrayList<>();
        if (model.getUserFollow() != null && model.getUserFollow().size() > 0) {
            newFollowTeam.stream().forEach(x -> {
                newFollowTeamEntity.add(this.userRepository.findById(x).orElseThrow(() -> new CustomHandleException(2)));
                if (userProjectRepository.getByCategoryAndObjectIdAndTypeAndIdUser(Const.tableName.FEATURE.name(), oldFeature.getId(), Const.type.TYPE_FOLLOWER.name(), x).size() == 0) {
                    this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(oldFeature.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_FOLLOWER.name()).build());
                }
            });
            oldFeature.setFollowTeam(newFollowTeamEntity);
        }
        iHistoryLogService.logUpdate(oldFeature.getId(), featureOriginal, oldFeature, Const.tableName.FEATURE);
        return FeatureDto.toDto(this.featureRepository.save(oldFeature));
    }

    @Override
    public boolean deleteById(Long id) {
        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(id, listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }

        FeatureEntity feature = this.featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature not exist !!!"));
        // delete Task
        List<TaskEntity> taskEntities = this.taskRepository.findByFeatureId(id);
        taskEntities.forEach(taskEntity -> this.taskService.deleteById(taskEntity.getId()));

        // delete userProject
        List<UserProjectEntity> userProjectEntities = this.userProjectRepository.getByCategoryAndObjectIdAndType(Const.tableName.FEATURE.name(), id, Const.type.TYPE_DEV.name());
        for (UserProjectEntity userProjectEntity : userProjectEntities) {
            this.userProjectRepository.delete(userProjectEntity);
        }

        //delete tag_relation
        List<TagRelationEntity> tagRelationEntities = this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id);
        for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
            this.tagRelationRepository.delete(tagRelationEntity);
        }

        // delete file
        fileRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id).stream().forEach(fileEntity -> this.fileService.deleteById(fileEntity.getId()));

        // delete Feature
        this.featureRepository.deleteFeature(id);

        iHistoryLogService.logDelete(id, feature, Const.tableName.FEATURE, feature.getName());

        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    private void clearTagList(FeatureEntity feature) {
        this.tagRelationService.findTagByCategoryAndObject(Const.tableName.FEATURE.name(), feature.getId()).stream().forEach(x -> this.tagRelationService.deleteById(x.getId()));
    }

    private void clearFileList(FeatureEntity feature) {
        this.fileService.deleteByIds(feature.getAttachFiles().stream().map(x -> x.getId()).collect(Collectors.toList()));
    }

    private void clearDevTeam(Long id) {
        this.userProjectRepository.deleteByCategoryAndObjectId(Const.tableName.FEATURE.name(), id);
    }

    @Override
    public boolean changIsDeleteById(Long id) {
        FeatureEntity oldFeature = this.getById(id);
        oldFeature.setIsDeleted(true);
        this.featureRepository.saveAndFlush(oldFeature);
        iHistoryLogService.logDelete(id, oldFeature, Const.tableName.FEATURE, oldFeature.getName());
        return true;
    }

    @Override
    public Page<FeatureDto> findAllByProjectId(Long id, Pageable pageable) {
        return this.featureRepository.findAllByProject_Id(id, pageable).map(FeatureDto::toDto);
    }

    @Override
    public Page<FeatureDto> findByPage(FeatureFilterModel featureFilterModel, Pageable pageable) {
        Long userIdd = SecurityUtils.getCurrentUserId();
        String sql = "SELECT distinct new cy.dtos.project.FeatureDto(p) FROM FeatureEntity p ";
        String countSQL = "select count(distinct(p)) from FeatureEntity p  ";
        if (featureFilterModel.getSearchField() != null && featureFilterModel.getSearchField().charAt(0) == '#') {
            sql += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
            countSQL += " inner join TagRelationEntity tr on tr.objectId = p.id inner join TagEntity t on t.id = tr.idTag ";
        }

        sql += " WHERE p.project.id = :projectId and p.isDeleted = false ";
        countSQL += " WHERE p.project.id = :projectId and p.isDeleted = false ";

        if (featureFilterModel.getSearchField() != null) {
            if (featureFilterModel.getSearchField().charAt(0) == '#') {
                sql += " AND (t.name = :textSearch ) AND (tr.category LIKE 'FEATURE') ";
                countSQL += " AND (t.name = :textSearch ) AND (tr.category LIKE 'FEATURE') ";
            } else {
                sql += " AND (p.name LIKE :textSearch ) ";
                countSQL += "AND (p.name LIKE :textSearch ) ";
            }
        }

        if (featureFilterModel.getStatus() != null) {
            sql += " AND p.status = :status ";
            countSQL += " AND p.status = :status ";
        }

        if (featureFilterModel.getMinDate() != null && featureFilterModel.getMaxDate() != null) {
            sql += " AND p.startDate >= :startDate AND p.endDate <= :endDate ";
            countSQL += " AND p.startDate >= :startDate AND p.endDate <= :endDate ";
        } else {
            if (featureFilterModel.getMinDate() != null) {
                sql += " AND p.startDate >= :startDate ";
                countSQL += " AND p.startDate >= :startDate ";
            } else if (featureFilterModel.getMaxDate() != null) {
                sql += " AND p.endDate >= :endDate ";
                countSQL += " AND p.endDate >= :endDate ";
            }
        }

        Sort.Order orderUpdatedDate = pageable.getSort().getOrderFor("updatedDate");
        if (orderUpdatedDate != null) {
            sql += " order by p.updatedDate " + orderUpdatedDate.getDirection().toString();
        }
        Sort.Order orderStartDate = pageable.getSort().getOrderFor("startDate");
        if (orderStartDate != null) {
            sql += " order by p.startDate " + orderStartDate.getDirection().toString();
        }

        Sort.Order orderEndDate = pageable.getSort().getOrderFor("endDate");
        if (orderEndDate != null) {
            sql += " order by p.endDate " + orderEndDate.getDirection().toString();
        }

        Query q = manager.createQuery(sql, FeatureDto.class);
        Query qCount = manager.createQuery(countSQL);


        if (featureFilterModel.getSearchField() != null) {
            String textSearch = featureFilterModel.getSearchField();
            if (featureFilterModel.getSearchField().charAt(0) == '#') {
                q.setParameter("textSearch", textSearch);
                qCount.setParameter("textSearch", textSearch);
            } else {
                q.setParameter("textSearch", "%" + textSearch + "%");
                qCount.setParameter("textSearch", "%" + textSearch + "%");
            }
        }
        if (featureFilterModel.getProjectId() != null) {
            q.setParameter("projectId", featureFilterModel.getProjectId());
            qCount.setParameter("projectId", featureFilterModel.getProjectId());
        }
        if (featureFilterModel.getStatus() != null) {
            q.setParameter("status", featureFilterModel.getStatus().toString());
            qCount.setParameter("status", featureFilterModel.getStatus().toString());
        }
        if (featureFilterModel.getMinDate() != null) {
            q.setParameter("startDate", convertDate(featureFilterModel.getMinDate() + ".000"));
            qCount.setParameter("startDate", convertDate(featureFilterModel.getMinDate() + ".000"));
        }
        if (featureFilterModel.getMaxDate() != null) {
            q.setParameter("endDate", convertDate(featureFilterModel.getMaxDate() + ".000"));
            qCount.setParameter("endDate", convertDate(featureFilterModel.getMaxDate() + ".000"));
        }

        q.setFirstResult((pageable.getPageNumber()) * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        Long numberResult = (Long) qCount.getSingleResult();
        Page<FeatureDto> result = new PageImpl<>(q.getResultList(), pageable, numberResult);

        result.stream().forEach(data -> {
            data.setEditable(false);
            List<String> listType = new ArrayList<>();
            listType.add(Const.type.TYPE_DEV.toString());
            List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(data.getId(), listType);
            if(listIdDevInProject.stream().anyMatch(userIdd::equals)){
                data.setEditable(true);
            }

        });

        return result;
    }

    public boolean updateStatusFeature(Long id, String status) {
        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        List<Long> listIdDevInProject = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(id, listType);
        if(!listIdDevInProject.stream().anyMatch(idUser::equals)){
            throw new CustomHandleException(5);
        }
        featureRepository.updateStatusFeature(status, id);
        return true;
    }

    public static java.sql.Timestamp convertDate(String date) {
        java.sql.Timestamp result = null;
        SimpleDateFormat localeIta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date parsedDate = localeIta.parse(date);
            result = new Timestamp(parsedDate.getTime());
//            result.setHours(0);
//            result.setMinutes(0);
//            result.setSeconds(0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }
}
