package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.FileDto;
import cy.entities.project.FileEntity;
import cy.entities.project.HistoryEntity;
import cy.models.project.FileModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.IFileRepository;
import cy.repositories.project.IHistoryLogRepository;
import cy.services.project.IFileService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = {Exception.class, Throwable.class})
public class FileServiceImpl implements IFileService {
    @Autowired
    private IFileRepository fileRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private FileUploadProvider fileUploadProvider;
    @Autowired
    private IHistoryLogRepository iHistoryLogRepository;

    @Override
    public List<FileDto> findAll() {
        return fileRepository.findAll().stream().map(fileEntity -> FileDto.toDto(fileEntity)).collect(Collectors.toList());
    }

    @Override
    public Page<FileDto> findAll(Pageable page) {
        return fileRepository.findAll(page).map(fileEntity -> FileDto.toDto(fileEntity));
    }

    @Override
    public List<FileDto> findAll(Specification<FileEntity> specs) {
        return null;
    }

    @Override
    public Page<FileDto> filter(Pageable page, Specification<FileEntity> specs) {
        return null;
    }

    @Override
    public FileDto findById(Long id) {
        return FileDto.toDto(fileRepository.findById(id).orElseThrow(() -> new CustomHandleException(431)));
    }

    @Override
    public FileEntity getById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new CustomHandleException(431));
    }

    @Override
    public FileDto add(FileModel model) {
        FileEntity fileEntity = FileModel.toEntity(model);
        fileEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getFile() != null && !model.getFile().isEmpty()) {
            try {
                String result = fileUploadProvider.uploadFile(model.getCategory(), model.getFile());
                String fileName = model.getFile().getOriginalFilename();
                fileEntity.setLink(result);
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return FileDto.toDto(fileRepository.saveAndFlush(fileEntity));
    }

    @Override
    public FileEntity addEntity(FileModel model) {
        FileEntity fileEntity = FileModel.toEntity(model);
        fileEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getFile() != null && !model.getFile().isEmpty()) {
            try {
                String result = fileUploadProvider.uploadFile(model.getCategory(), model.getFile());
                String fileName = model.getFile().getOriginalFilename();
                fileEntity.setLink(result);
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileEntity;
    }

    @Override
    public List<FileDto> add(List<FileModel> model) {
        List<FileDto> fileDtoList = new ArrayList<>();
        if (model != null && !model.isEmpty()){
            for (FileModel fileModel: model){
                FileEntity fileEntity = FileModel.toEntity(fileModel);
                fileEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
                if (fileModel.getFile() != null && !fileModel.getFile().isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile(fileModel.getCategory(), fileModel.getFile());
                        String fileName = fileModel.getFile().getOriginalFilename();
                        fileEntity.setLink(result);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                fileDtoList.add(FileDto.toDto(fileRepository.save(fileEntity)));
            }
        }
        return fileDtoList;
    }

    @Override
    public FileDto update(FileModel model) {
        FileEntity fileEntity = this.getById(model.getId());
        fileEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getFile() != null && !model.getFile().isEmpty() && model.getFile().getSize() > 0) {
            try {
                String result = fileUploadProvider.uploadFile(model.getCategory(), model.getFile());
                String fileName = model.getFile().getOriginalFilename();
                fileEntity.setLink(result);
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (model.getObjectId() != null){
            fileEntity.setObjectId(model.getObjectId());
        }
        if (model.getCategory() != null && !model.getCategory().isEmpty()){
            fileEntity.setCategory(model.getCategory());
        }
        return FileDto.toDto(fileRepository.save(fileEntity));
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            FileEntity fileEntity = fileRepository.findById(id).get();
            fileRepository.deleteById(id);
            HistoryEntity newHistoryEntity = HistoryEntity
                    .builder()
                    .id(null)
                    .ObjectId(fileEntity.getObjectId())
                    .category(fileEntity.getCategory())
                    .userId(fileEntity.getUploadedBy())
                    .content(" <p>đã xóa file đính kèm.</p>")
                    .build();
            iHistoryLogRepository.saveAndFlush(newHistoryEntity);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        try {
            for (Long id: ids)
                fileRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
