package cy.services.project.impl;

import cy.dtos.project.BugDto;
import cy.entities.project.BugEntity;
import cy.entities.project.FileEntity;
import cy.models.project.BugModel;
import cy.repositories.project.BugRepository;
import cy.services.project.IRequestBugService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BugServiceImpl implements IRequestBugService {
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    BugRepository bugRepository;
    @Override
    public List<BugDto> findAll() {
        return null;
    }

    @Override
    public Page<BugDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<BugDto> findAll(Specification<BugEntity> specs) {
        return null;
    }

    @Override
    public Page<BugDto> filter(Pageable page, Specification<BugEntity> specs) {
        return null;
    }

    @Override
    public BugDto findById(Long id) {
        return null;
    }

    @Override
    public BugEntity getById(Long id) {
        return null;
    }
Const aConst;
    @Override
    public BugDto add(BugModel model) {
        BugEntity bugEntity= model.modelToEntity(model);
        FileEntity fileEntity = new FileEntity();
        List<String> s3Urls = new ArrayList<>();
        if(model.getAttachFiles() != null && model.getAttachFiles().length > 0){
            for(MultipartFile fileMultipart : model.getAttachFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = null;
                    try {
                        result = fileUploadProvider.uploadFile("bug",fileMultipart);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    s3Urls.add(result);
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", s3Urls));
            fileEntity.setCategory(Const.tableName.BUG.toString());
            fileEntity.getFileType();
            fileEntity.setLink(jsonObject.toString());
            fileEntity.setObjectId(bugEntity.getId());

          /*  bugEntity.setAttachFiles(jsonObject.toString());*/
        }
        bugRepository.saveAndFlush(bugEntity);
        BugDto bugDto=BugDto.entityToDto(bugEntity);
        return bugDto;
    }

    @Override
    public List<BugDto> add(List<BugModel> model) {
        return null;
    }

    @Override
    public BugDto update(BugModel model) {
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
