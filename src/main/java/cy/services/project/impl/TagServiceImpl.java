package cy.services.project.impl;

import cy.dtos.TagDto;
import cy.entities.project.TagEntity;
import cy.models.project.TagModel;
import cy.repositories.project.ITagRepository;
import cy.services.project.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements ITagService {

    @Autowired
    ITagRepository iTagRepository;
    @Override
    public List<TagDto> findAll() {
        List<TagDto> tagDtos = iTagRepository.findAll().stream().map(x->TagDto.toDto(x)).collect(Collectors.toList());
        return tagDtos;
    }

    @Override
    public Page<TagDto> findAll(Pageable page) {
        Page<TagEntity> tagEntities = iTagRepository.findAllByPage(page);
        Page<TagDto> tagDtos = tagEntities.map(x -> TagDto.toDto(x));
        return tagDtos;
    }

    @Override
    public List<TagDto> findAll(Specification<TagEntity> specs) {
        return null;
    }

    @Override
    public Page<TagDto> filter(Pageable page, Specification<TagEntity> specs) {
        return null;
    }

    @Override
    public TagDto findById(Long id) {
        TagEntity entity = iTagRepository.findById(id).orElse(null);
        if (entity == null){
            return null;
        }
        return TagDto.toDto(iTagRepository.save(entity));
    }

    @Override
    public TagEntity getById(Long id) {
        return null;
    }

    @Override
    public TagDto add(TagModel model) {
        if (model.getName().isEmpty() || !model.getName().contains("#")){
            return null;
        }
        TagEntity entity = iTagRepository.findByName(model.getName());
        if (entity != null ){
            return null;
        }
        entity = new TagEntity();
        entity.setName(model.getName());
        return TagDto.toDto(iTagRepository.save(entity));
    }

    @Override
    public List<TagDto> add(List<TagModel> model) {
        List<TagDto> tagDtos = new ArrayList<>();
        for (TagModel tagModel : model){
           if (add(model) != null ){
               return null;
           };
           tagDtos.add(add(tagModel));
        }
        return tagDtos;
    }

    @Override
    public TagDto update(TagModel model) {
        if (model.getId() == null){
            return null;
        }

        if (model.getName().isEmpty() || !model.getName().contains("#")){
            return null;
        }
        TagEntity entity = iTagRepository.findByName(model.getName());
        if (entity != null && !entity.getName().contains(model.getName())){
            return null;
        }
        entity = iTagRepository.findById(model.getId()).orElse(null);
        if (entity == null){
            return null;
        }


        entity.setName(model.getName());
        return TagDto.toDto(iTagRepository.save(entity));
    }

    @Override
    public boolean deleteById(Long id) {

        TagEntity entity = iTagRepository.findById(id).orElse(null);
        if (entity == null){
            return false;
        }
        iTagRepository.deleteById(id);
        entity = iTagRepository.findById(id).orElse(null);
        if (entity == null){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        for (Long id : ids){
            if (deleteById(id) == false){
                return false;
            }
        }
        return true;
    }
}
