package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.TagDto;
import cy.dtos.project.FeatureDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.FeatureModel;
import cy.models.project.FileModel;
import cy.models.project.TagModel;
import cy.models.project.TagRelationModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.*;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        FeatureEntity featureEntity = this.featureRepository.findById(id).orElseThrow(()->new CustomHandleException(23));
        featureEntity.setTagList(tagRelationService.findTagByCategoryAndObject(Const.tableName.FEATURE.name(),id).stream().map(x->x.getIdTag()).collect(Collectors.toList()).stream().map(y->this.tagService.getById(y)).collect(Collectors.toList()));
        featureEntity.setDevTeam(userProjectRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id).stream().map(y->this.userRepository.findById(y.getIdUser()).orElseThrow(()->new CustomHandleException(232))).collect(Collectors.toList()));
        return FeatureDto.toDto(this.getById(id));
    }

    @Override
    public FeatureEntity getById(Long id) {
        return this.featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature not exist!!"));
    }

    @Override
    public FeatureDto add(FeatureModel model) {
        ProjectEntity projectEntity = this.projectRepository.findById(model.getPid()).orElseThrow(()->new CustomHandleException(45354345));
        Set<Long> currentProjectUIDs = userProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId()).stream().map(x->x.getIdUser()).collect(Collectors.toSet());
        if(Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)){
            throw new CustomHandleException(2131231);
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
        for (MultipartFile file : files
        ) {
            FileModel model1 = new FileModel();
            model1.setFile(file);
//            model1.setObjectId(entity.getId());
            model1.setCategory(Const.tableName.FEATURE.name());
            fileEntities.add(this.fileService.addEntity(model1));
        }
        FeatureEntity entity = (FeatureEntity) FeatureEntity.builder().
                startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .createBy(SecurityUtils.getCurrentUser().getUser())
                .status(Const.status.TO_DO.name())
                .description(model.getDescription())
                .name(model.getName())
                .project(projectEntity)
                .attachFiles(fileEntities)
                .tagList(tagList)
                .isDeleted(false)
                .isDefault(model.getIsDefault())
                .priority(model.getPriority().name())
                .build();
        this.featureRepository.saveAndFlush(entity);

        //Add Tags
        tagList.stream().forEach(x -> this.tagRelationService.add(TagRelationModel.builder().idTag(x.getId()).category(Const.tableName.FEATURE.name()).objectId(entity.getId()).build()));
        //Add Users
        List<Long> curProjectIds = userProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId()).stream().map(x->x.getIdUser()).collect(Collectors.toList());
        if(new HashSet<>(curProjectIds).containsAll(model.getUids())){
            model.getUids().stream().forEach(x->this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(entity.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_DEV.name()).build()));
            entity.setDevTeam(model.getUids().stream().map(x->this.userRepository.findById(x).orElseThrow(()->new CustomHandleException(2))).collect(Collectors.toList()));
        }else {
            throw new CustomHandleException(2131231);
        }
//        entity.setProject();
        iHistoryLogService.logCreate(entity.getId(), entity, Const.tableName.FEATURE);
        return FeatureDto.toDto(entity);
    }

    @Override
    public List<FeatureDto> add(List<FeatureModel> model) {
        return null;
    }

    @Override
    public FeatureDto update(FeatureModel model) {
        FeatureEntity featureOriginal = (FeatureEntity) Const.copy(this.featureRepository.findById(model.getId()));
        FeatureEntity oldFeature = this.featureRepository.findById(model.getId()).orElseThrow(()->new CustomHandleException(232));
        Set<Long> currentProjectUIDs = userProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), oldFeature.getProject().getId()).stream().map(x->x.getIdUser()).collect(Collectors.toSet());
        if(Set.of(SecurityUtils.getCurrentUserId()).stream().noneMatch(currentProjectUIDs::contains)){
            throw new CustomHandleException(2131231);
        }
        ProjectEntity projectEntity = oldFeature.getProject();
        oldFeature.setName(model.getName());
        oldFeature.setDescription(model.getDescription());
        oldFeature.setStartDate(model.getStartDate());
        oldFeature.setEndDate(model.getEndDate());
        oldFeature.setIsDefault(model.getIsDefault());
        oldFeature.setPriority(model.getPriority().name());
        //Clear old tags
        clearTagList(oldFeature);
        //Add new tags
        List<String> newTagList = model.getTagList();
        List<TagEntity> newTagEntityList = new ArrayList<>();
        newTagList.stream().forEach(x -> {
            TagDto tagDto = this.tagService.add(TagModel.builder().name(x).build());
            newTagEntityList.add(TagEntity.builder().id(tagDto.getId()).name(tagDto.getName()).build());
            this.tagRelationService.add(TagRelationModel.builder().idTag(tagDto.getId()).category(Const.tableName.FEATURE.name()).objectId(oldFeature.getId()).build());
        });
        oldFeature.setTagList(newTagEntityList);

        //Clear old files
        clearFileList(oldFeature);
        //Add new files
        List<MultipartFile> newFileList = model.getFiles();
        List<FileEntity> newFileEntityList = new ArrayList<>();
        newFileList.stream().forEach(x -> {
            FileModel fileModel = new FileModel();
            fileModel.setFile(x);
            fileModel.setObjectId(oldFeature.getId());
            fileModel.setCategory(Const.tableName.FEATURE.name());
            newFileEntityList.add(this.fileService.addEntity(fileModel));
        });
        oldFeature.setAttachFiles(newFileEntityList);
        clearDevTeam(oldFeature.getId());
        List<Long> currentAvailableDev = userProjectRepository.getByCategoryAndObjectId(Const.tableName.PROJECT.name(), projectEntity.getId()).stream().map(x->x.getIdUser()).collect(Collectors.toList());
        List<Long> newDevTeam = model.getUids();
        List<UserEntity> newDevTeamEntity = new ArrayList<>();
        if (new HashSet<>(currentAvailableDev).containsAll(newDevTeam)) {
            newDevTeam.stream().forEach(x -> {
                newDevTeamEntity.add(this.userRepository.findById(x).orElseThrow(() -> new CustomHandleException(2)));
                this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(oldFeature.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_DEV.name()).build());
            });
            oldFeature.setDevTeam(newDevTeamEntity);
        } else {
            throw new CustomHandleException(2131231);
        }
        iHistoryLogService.logUpdate(oldFeature.getId(), featureOriginal, oldFeature, Const.tableName.FEATURE);
        return FeatureDto.toDto(this.featureRepository.save(oldFeature));
    }

    @Override
    public boolean deleteById(Long id) {
        FeatureEntity feature = this.featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature not exist !!!"));
        // delete Task
        List<TaskEntity> taskEntities = this.taskRepository.findByFeatureId(id);
        taskEntities.forEach(taskEntity -> this.taskService.deleteById(taskEntity.getId()));

        // delete userProject
        List<UserProjectEntity> userProjectEntities = this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id);
        for (UserProjectEntity userProjectEntity : userProjectEntities) {
            this.userProjectRepository.delete(userProjectEntity);
        }

        //delete tag_relation
        List<TagRelationEntity> tagRelationEntities =  this.tagRelationRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id);
        for (TagRelationEntity tagRelationEntity : tagRelationEntities) {
            this.tagRelationRepository.delete(tagRelationEntity);
        }

        // delete file
        fileRepository.getByCategoryAndObjectId(Const.tableName.FEATURE.name(), id).stream().forEach(fileEntity -> this.fileService.deleteById(fileEntity.getId()));

        // delete Feature
        this.featureRepository.deleteById(id);

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
            this.userProjectRepository.deleteByCategoryAndObjectId(Const.tableName.FEATURE.name(),id);
    }

    @Override
    public boolean changIsDeleteById(Long id) {
        FeatureEntity oldFeature = this.getById(id);
        oldFeature.setIsDeleted(true);
        this.featureRepository.saveAndFlush(oldFeature);
        iHistoryLogService.logDelete(id, oldFeature, Const.tableName.FEATURE);
        return true;
    }

    @Override
    public Page<FeatureDto> findAllByProjectId(Long id, Pageable pageable) {
        return this.featureRepository.findAllByProject_Id(id,pageable).map(FeatureDto::toDto);
    }
}
