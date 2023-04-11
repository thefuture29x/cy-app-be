package cy.services.attendance.impl;

import cy.configs.Base64ToMultipartFile;
import cy.dtos.common.CustomHandleException;
import cy.dtos.attendance.TextQrDto;
import cy.entities.attendance.TextQrEntity;
import cy.models.attendance.TextQrModel;
import cy.repositories.common.IUserRepository;
import cy.repositories.attendance.ITextQrRepository;
import cy.services.attendance.ITextQrService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
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
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = {Exception.class, Throwable.class})
public class TextQrServiceImpl implements ITextQrService {
    @Autowired
    ITextQrRepository textQrRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Override
    public List<TextQrDto> findAll() {
        return textQrRepository.findAll().stream().map(textQrEntity -> TextQrDto.toDto(textQrEntity)).collect(Collectors.toList());
    }

    @Override
    public Page<TextQrDto> findAll(Pageable page) {
        return textQrRepository.findAll(page).map(textQrEntity -> TextQrDto.toDto(textQrEntity));
    }

    @Override
    public List<TextQrDto> findAll(Specification<TextQrEntity> specs) {
        return null;
    }

    @Override
    public Page<TextQrDto> filter(Pageable page, Specification<TextQrEntity> specs) {
        return null;
    }

    @Override
    public TextQrDto findById(Long id) {
        return TextQrDto.toDto(textQrRepository.findById(id).orElseThrow(() -> new CustomHandleException(121)));
    }

    @Override
    public TextQrEntity getById(Long id) {
        return textQrRepository.findById(id).orElseThrow(() -> new CustomHandleException(121));
    }

    @Override
    public TextQrDto add(TextQrModel model) {
        TextQrEntity textQrEntity = TextQrModel.toEntity(model);
        textQrEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getImage() != null && !model.getImage().isEmpty()) {
            try {
                textQrEntity.setImage(fileUploadProvider.uploadFile("text-qr", convertBase64ToStringPath(model.getImage())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return TextQrDto.toDto(textQrRepository.save(textQrEntity));
    }

    @Override
    public List<TextQrDto> add(List<TextQrModel> model) {
        List<TextQrDto> textQrDtoList = new ArrayList<>();
        for (TextQrModel textQrModel : model) {
            TextQrEntity textQrEntity = TextQrModel.toEntity(textQrModel);
            textQrEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
            if (textQrModel.getImage() != null && !textQrModel.getImage().isEmpty()) {
                try {
                    textQrEntity.setImage(fileUploadProvider.uploadFile("text-qr", convertBase64ToStringPath(textQrModel.getImage())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            TextQrDto textQrDto = TextQrDto.toDto(textQrRepository.save(textQrEntity));
            textQrDtoList.add(textQrDto);
        }
        return textQrDtoList;
    }

    @Override
    public TextQrDto update(TextQrModel model) {
        TextQrEntity textQrEntity = this.getById(model.getId());
        textQrEntity.setUploadedBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new CustomHandleException(11)));
        if (model.getImage() != null && !model.getImage().isEmpty() && !model.getImage().contains("http")) {
            try {
                textQrEntity.setImage(fileUploadProvider.uploadFile("text-qr", convertBase64ToStringPath(model.getImage())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        textQrEntity.setName(model.getName());
        textQrEntity.setEmail(model.getEmail());
        textQrEntity.setAddress(model.getAddress());
        textQrEntity.setCompany(model.getCompany());
        textQrEntity.setTelephone(model.getTelephone());
        textQrEntity.setFax(model.getFax());
        textQrEntity.setContent(model.getContent());
        return TextQrDto.toDto(textQrRepository.save(textQrEntity));
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            textQrRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        try {
            for (Long id: ids)
                textQrRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public MultipartFile convertBase64ToStringPath(String image) throws IOException {
        final String[] base64Array = image.split(",");
        String dataUir, data;
        if (base64Array.length > 1) {
            dataUir = base64Array[0];
            data = base64Array[1];
        } else {
            dataUir = "data:image/jpg;base64";
            data = base64Array[0];
        }
        return new Base64ToMultipartFile(data, dataUir);
    }

}
