package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.TagDto;
import cy.dtos.project.FeatureDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.project.FeatureModel;
import cy.models.project.FileModel;
import cy.models.project.TagModel;
import cy.models.project.TagRelationModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.IFeatureRepository;
import cy.repositories.project.IProjectRepository;
import cy.repositories.project.IUserProjectRepository;
import cy.services.project.IFeatureService;
import cy.services.project.IFileService;
import cy.services.project.ITagRelationService;
import cy.services.project.ITagService;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class FeatureServiceImp implements IFeatureService {
    @Autowired
    IFeatureRepository featureRepository;
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
        return FeatureDto.toDto(this.getById(id));
    }

    @Override
    public FeatureEntity getById(Long id) {
        return this.featureRepository.findById(id).orElseThrow(() -> new RuntimeException("Feature not exist!!"));
    }

    @Override
    public FeatureDto add(FeatureModel model) {
        ProjectEntity projectEntity = this.projectRepository.findById(model.getPid()).orElseThrow(()->new CustomHandleException(45354345));
        List<TagEntity> tagList= new ArrayList<>();
        for (String tag: model.getTagList()
             ) {
            TagDto thisTag = this.tagService.add(TagModel.builder().name(tag).build());
            tagList.add(TagEntity.builder().id(thisTag.getId()).name(thisTag.getName()).build());
        }
        //Add Files
        List<MultipartFile> files = model.getFiles();
        List<FileEntity> fileEntities = new ArrayList<>();
        for (MultipartFile file:files
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
                .priority(model.getPriority().name())
                .build();
        this.featureRepository.save(entity);

        //Add Tags
        tagList.stream().forEach(x-> this.tagRelationService.add(TagRelationModel.builder().idTag(x.getId()).category(Const.tableName.FEATURE.name()).objectId(entity.getId()).build()));
        //Add Users
        List<Long> curProjectIds = projectEntity.getDevTeam().stream().map(x->x.getUserId()).collect(Collectors.toList());
        if(new HashSet<>(curProjectIds).containsAll(model.getUids())){
            model.getUids().stream().forEach(x->this.userProjectRepository.save(UserProjectEntity.builder().idUser(x).objectId(entity.getId()).category(Const.tableName.FEATURE.name()).type(Const.type.TYPE_DEV.name()).build()));
            entity.setDevTeam(model.getUids().stream().map(x->this.userRepository.findById(x).orElseThrow(()->new CustomHandleException(2))).collect(Collectors.toList()));
        }else {
            throw new CustomHandleException(2131231);
        }
//        entity.setProject();
        return FeatureDto.toDto(entity);
    }

    @Override
    public List<FeatureDto> add(List<FeatureModel> model) {
        return null;
    }

    @Override
    public FeatureDto update(FeatureModel model) {
        FeatureEntity oldFeature = this.featureRepository.findById(model.getId()).orElseThrow(()->new CustomHandleException(232));

        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        FeatureEntity oldEntity = this.getById(id);
        oldEntity.setIsDeleted(true);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
