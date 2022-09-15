package cy.services.project.impl;

import cy.dtos.project.FileDto;
import cy.entities.project.FileEntity;
import cy.models.project.FileModel;
import cy.services.project.IFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements IFileService {
    @Override
    public List<FileModel> findAll() {
        return null;
    }

    @Override
    public Page<FileModel> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<FileModel> findAll(Specification<FileEntity> specs) {
        return null;
    }

    @Override
    public Page<FileModel> filter(Pageable page, Specification<FileEntity> specs) {
        return null;
    }

    @Override
    public FileModel findById(Long id) {
        return null;
    }

    @Override
    public FileEntity getById(Long id) {
        return null;
    }

    @Override
    public FileModel add(FileDto model) {
        return null;
    }

    @Override
    public List<FileModel> add(List<FileDto> model) {
        return null;
    }

    @Override
    public FileModel update(FileDto model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
