package cy.services.attendance.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.attendance.OptionDto;
import cy.entities.attendance.OptionEntity;
import cy.repositories.attendance.IOptionRepository;
import cy.services.attendance.IOptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OptionServiceImpl implements IOptionService {

    private final IOptionRepository optionRepository;

    public OptionServiceImpl(IOptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<OptionDto> findAll() {
        return null;
    }

    @Override
    public Page<OptionDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<OptionDto> findAll(Specification<OptionEntity> specs) {
        return null;
    }

    @Override
    public Page<OptionDto> filter(Pageable page, Specification<OptionEntity> specs) {
        return null;
    }

    @Override
    public OptionDto findById(Long id) {
        return null;
    }

    @Override
    public OptionEntity getById(Long id) {
        return this.optionRepository.findById(id).orElseThrow(() -> new CustomHandleException(161));
    }

    @Override
    public OptionDto add(OptionDto model) {
        OptionEntity optionEntity = this.optionRepository.findByOptionKey(model.getOptionKey())
                .orElse(OptionEntity
                        .builder()
                        .id(model.getId())
                        .optionKey(model.getOptionKey())
                        .build());
        optionEntity.setOptionValue(model.getOptionValue());
        this.optionRepository.saveAndFlush(optionEntity);
        return OptionDto.toDto(optionEntity);
    }

    @Override
    public List<OptionDto> add(List<OptionDto> model) {
        return null;
    }

    @Override
    public OptionDto update(OptionDto model) {
        return this.add(model);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            this.optionRepository.deleteById(id);
        } catch (Exception e) {
            throw new CustomHandleException(162);
        }
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }


    @Override
    public OptionDto findByKey(String key) {
        return OptionDto.toDto(this.optionRepository.findByOptionKey(key).orElseThrow(() -> new CustomHandleException(161)));
    }

    @Override
    public List<OptionDto> findAllByKeys(List<String> keys) {
        return this.optionRepository.findAllByOptionKeyIn(keys).stream().map(OptionDto::toDto).collect(Collectors.toList());
    }
}
