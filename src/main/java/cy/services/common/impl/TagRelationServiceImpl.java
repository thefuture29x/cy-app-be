package cy.services.common.impl;

import cy.dtos.common.CustomHandleException;
import cy.dtos.project.DataSearchTag;
import cy.dtos.common.TagRelationDto;
import cy.entities.common.TagEntity;
import cy.entities.common.TagRelationEntity;
import cy.entities.project.*;
import cy.models.common.TagRelationModel;
import cy.repositories.common.ITagRelationRepository;
import cy.repositories.common.ITagRepository;
import cy.repositories.project.*;
import cy.services.common.ITagRelationService;
import cy.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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
        TagEntity tagEntity = iTagRepository.findById(model.getIdTag()).orElseThrow(() -> new CustomHandleException(183,"không tìm thấy Tag = "+model.getIdTag()));

        tagRelationEntity.setIdTag(model.getIdTag());
        if (model.getObjectId() == null){
            return null;
        }
        if ( model.getCategory().isEmpty()){
            return null;
        }
        if (model.getCategory().contains(Const.tableName.PROJECT.toString())){
            ProjectEntity projectEntity = iProjectRepository.findById(model.getObjectId()).orElseThrow(() -> new CustomHandleException(183,"không tìm thấy id PROJECT = "+model.getObjectId()));
        }else if (model.getCategory().contains(Const.tableName.TASK.toString())){
            TaskEntity taskEntity = iTaskRepository.findById(model.getObjectId()).orElseThrow(() -> new CustomHandleException(183,"không tìm thấy id TASK = "+model.getObjectId()));
        }else if (model.getCategory().contains(Const.tableName.FEATURE.toString())){
            FeatureEntity featureEntity = iFeatureRepository.findById(model.getObjectId()).orElseThrow(() -> new CustomHandleException(183,"không tìm thấy id FEATURE = "+model.getObjectId()));
        } else if (model.getCategory().contains(Const.tableName.SUBTASK.toString())){
            SubTaskEntity subTaskEntity = iSubTaskRepository.findById(model.getObjectId()).orElseThrow(() -> new CustomHandleException(183,"không tìm thấy id SUBTASK = "+model.getObjectId()));
        }else {
            return null;
        }
        if (iTagRelationRepository.checkIsEmpty(model.getObjectId(), model.getIdTag(), model.getCategory()) != null ){
            return TagRelationDto.toDto(iTagRelationRepository.checkIsEmpty(model.getObjectId(), model.getIdTag(), model.getCategory()));
        };
        tagRelationEntity.setObjectId(model.getObjectId());
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

    @Override
    public Page<DataSearchTag> findAllByTag(String search, Pageable pageable) {
        search = '#' + search;
        Page<DataSearchTag> dataSearchTags = this.iTagRelationRepository.findAllByTag(search,pageable);
        return dataSearchTags;
    }
}
