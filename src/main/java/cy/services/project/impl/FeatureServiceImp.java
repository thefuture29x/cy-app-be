package cy.services.project.impl;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import cy.entities.project.ProjectEntity;
import cy.models.project.FeatureModel;
import cy.models.project.FileModel;
import cy.repositories.project.IFeatureRepository;
import cy.repositories.project.IProjectRepository;
import cy.services.project.IFeatureService;
import cy.services.project.IFileService;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureServiceImp implements IFeatureService {
    @Autowired
    IFeatureRepository featureRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    IProjectRepository projectRepository;

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
        ProjectEntity projectEntity = this.projectRepository.findById(model.getPid()).orElseThrow(()->new RuntimeException("Project not exist!!!"));
        FeatureEntity entity = (FeatureEntity) FeatureEntity.builder().
                startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .createBy(SecurityUtils.getCurrentUser().getUser())
                .status(Const.status.TO_DO.name())
                .description(model.getDescription())
                .name(model.getName())
                .project(projectEntity)
                .priority(model.getPriority().name())
                .build();
        this.featureRepository.save(entity);
        List<MultipartFile> files = model.getFiles();
        for (MultipartFile file:files
        ) {
            FileModel model1 = new FileModel();
            model1.setFile(file);
            model1.setObjectId(entity.getId());
            model1.setCategory(Const.tableName.FEATURE.name());
            this.fileService.add(model1);
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
