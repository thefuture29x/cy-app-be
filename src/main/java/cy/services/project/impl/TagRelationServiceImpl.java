package cy.services.project.impl;

import cy.dtos.TagDto;
import cy.dtos.project.TagRelationDto;
import cy.entities.attendance.RequestDeviceEntity;
import cy.entities.project.*;
import cy.models.project.TagModel;
import cy.models.project.TagRelationModel;
import cy.repositories.project.*;
import cy.services.project.ITagRelationService;
import cy.services.project.ITagService;
import cy.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagRelationServiceImpl implements ITagRelationService {
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    IFeatureRepository iFeatureRepository;
    @Autowired
    IProjectRepository iProjectRepository;
    @Autowired
    ITaskRepository iTaskRepository;
    @Autowired
    ISubTaskRepository iSubTaskRepository;


    @Override
    public List<TagRelationDto> findAll() {
        return null;
    }

    @Override
    public Page<TagRelationDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<TagRelationDto> findAll(Specification<TagRelationEntity> specs) {
        return null;
    }

    @Override
    public Page<TagRelationDto> filter(Pageable page, Specification<TagRelationEntity> specs) {
        return null;
    }

    @Override
    public TagRelationDto findById(Long id) {
        return null;
    }

    @Override
    public TagRelationEntity getById(Long id) {
        return null;
    }

    @Override
    public TagRelationDto add(TagRelationModel model) {
        TagRelationEntity tagRelationEntity = new TagRelationEntity();
        if (model.getIdTag() == null){
            return null;
        }
        TagEntity tagEntity = iTagRepository.findById(model.getIdTag()).orElse(null);
        if (tagEntity == null){
            return null;
        }
        tagRelationEntity.setIdTag(model.getIdTag());
        if (model.getObjectId() == null){
            return null;
        }
        if (model.getCategory() == null || model.getCategory().isEmpty()){
            return null;
        }
        if (model.getObjectId() == null){
            return null;
        }

        if (model.getCategory().contains(Const.tableName.PROJECT.toString())){
            ProjectEntity projectEntity = iProjectRepository.findById(model.getObjectId()).orElse(null);
            if (projectEntity == null){
                return null;
            }
            tagRelationEntity.setObjectId(projectEntity.getId());
        }else if (model.getCategory().contains(Const.tableName.TASK.toString())){
            TaskEntity taskEntity = iTaskRepository.findById(model.getObjectId()).orElse(null);
            if (taskEntity == null){
                return null;
            }
            tagRelationEntity.setObjectId(taskEntity.getId());
        }else if (model.getCategory().contains(Const.tableName.FEATURE.toString())){
            FeatureEntity featureEntity = iFeatureRepository.findById(model.getObjectId()).orElse(null);
            if (featureEntity == null){
                return null;
            }
            tagRelationEntity.setObjectId(featureEntity.getId());
        } else if (model.getCategory().contains(Const.tableName.SUBTASK.toString())){
            SubTaskEntity subTaskEntity = iSubTaskRepository.findById(model.getObjectId()).orElse(null);
            if (subTaskEntity == null){
                return null;
            }
            tagRelationEntity.setObjectId(subTaskEntity.getId());
        }else {
            return null;
        }
        tagRelationEntity.setCategory(model.getCategory());
        return TagRelationDto.toDto( iTagRelationRepository.save(tagRelationEntity));
    }

    @Override
    public List<TagRelationDto> add(List<TagRelationModel> model) {
        return null;
    }

    @Override
    public TagRelationDto update(TagRelationModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            iTagRelationRepository.deleteById(id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        for (Long id : ids){
            try{
                iTagRelationRepository.deleteById(id);
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public List<TagRelationEntity> findTagByCategoryAndObject(String category, Long objectId) {
        return iTagRelationRepository.getByCategoryAndObjectId(category, objectId);
    }
}
