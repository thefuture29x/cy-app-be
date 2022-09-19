package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.TagDto;
import cy.dtos.project.BugDto;
import cy.entities.project.*;
import cy.models.project.BugModel;
import cy.models.project.TagModel;
import cy.repositories.IUserRepository;
import cy.repositories.project.*;
import cy.services.project.IFileService;
import cy.services.project.IHistoryLogService;
import cy.services.project.IRequestBugService;
import cy.services.project.ITagService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
@Service
@Transactional
public class BugServiceImpl implements IRequestBugService {
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IBugHistoryRepository iBugHistoryRepository;
    @Autowired
    ISubTaskRepository subTaskRepository;
    @Autowired
    IBugRepository iBugRepository;
    @Autowired
    ITagService iTagService;
    @Autowired
    ITagRepository iTagRepository;
    @Autowired
    ITagRelationRepository iTagRelationRepository;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IFileService fileService;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IHistoryLogService iHistoryLogService;
    @Autowired
    IUserProjectRepository userProjectRepository;

    @Override
    public List<BugDto> findAll() {
        return null;
    }

    @Override
    public Page<BugDto> findAll(Pageable page) {
        return iBugRepository.findAll(page).map(data -> BugDto.entityToDto(data));
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
        BugEntity bugEntity= iBugRepository.findById(id).orElse(null);
        List<BugHistoryEntity> historyEntityList= iBugHistoryRepository.findAllByBugId(bugEntity);
        return BugDto.entityToDto(iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public BugEntity getById(Long id) {
        return null;
    }

    public void saveDataInHistoryTable(BugEntity bugEntity) {
        BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
        bugHistoryEntity.setBugId(bugEntity);
        bugHistoryEntity.setStartDate(bugEntity.getStartDate());
        bugHistoryEntity.setEndDate(bugEntity.getEndDate());
        iBugHistoryRepository.save(bugHistoryEntity);
    }

    @Override
    public BugDto add(BugModel model) {
        try {
            BugEntity bugEntity = model.modelToEntity(model);
            SubTaskEntity subTaskEntity = subTaskRepository.findById(model.getSubTask()).orElseThrow(() -> new CustomHandleException(11));
            bugEntity.setSubTask(subTaskEntity);
            bugEntity.setAssignTo(subTaskEntity.getAssignTo());
            bugEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
            //create tag
            if(model.getTags() != null && model.getTags().size() > 0){
                for (TagModel tagModel : model.getTags()){
                    TagDto tag = iTagService.add(tagModel);
                    if(tag == null){
                        TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                        if(tagEntity != null){

                        }
                    }
                }
            }
            BugEntity entity =  iBugRepository.saveAndFlush(bugEntity);

            //create file
            if(model.getFiles() != null && model.getFiles().length > 0){
                for (MultipartFile m : model.getFiles()){
                    if(!m.isEmpty()){
                        String urlFile =  fileUploadProvider.uploadFile("bug", m);
                        FileEntity fileEntity = new FileEntity();
                        String fileName = m.getOriginalFilename();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                        fileEntity.setCategory(Const.tableName.BUG.name());
                        fileEntity.setUploadedBy(SecurityUtils.getCurrentUser().getUser());
                        fileEntity.setObjectId(entity.getId());
                        iFileRepository.save(fileEntity);
                    }
                }
            }
            //create tag
            if(model.getTags() != null && model.getTags().size() > 0){
                for (TagModel tagModel : model.getTags()){
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if(tagEntity == null){
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
                        tagEntity1 =iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                    else if(tagEntity != null){
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }
            BugDto bugDto = BugDto.entityToDto(bugEntity);
            //chuyển trạng thái Subtask sang fixBug
            subTaskEntity.setStatus(Const.status.FIX_BUG.name());
            subTaskRepository.saveAndFlush(subTaskEntity);
            //Lưu dữ liệu vào bảng BugHistory
            saveDataInHistoryTable(bugEntity);
            iHistoryLogService.logCreate(entity.getId(),entity, Const.tableName.BUG);
            return bugDto;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List<BugDto> add(List<BugModel> model) {
        return null;
    }

    @Override
    public BugDto update(BugModel model) {
        try {
            BugEntity bugEntity = model.modelToEntity(model);
            SubTaskEntity subTaskEntity = subTaskRepository.findById(model.getSubTask()).orElseThrow(() -> new CustomHandleException(11));
            bugEntity.setSubTask(subTaskEntity);
            bugEntity.setAssignTo(subTaskEntity.getAssignTo());
            bugEntity.setCreateBy(SecurityUtils.getCurrentUser().getUser());
            //create tag
            if(model.getTags() != null && model.getTags().size() > 0){
                for (TagModel tagModel : model.getTags()){
                    TagDto tag = iTagService.add(tagModel);
                    if(tag == null){
                        TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                        if(tagEntity != null){

                        }
                    }
                }
            }
            BugEntity entityOriginal = iBugRepository.findById(model.getId()).get();
            BugEntity entity =  iBugRepository.saveAndFlush(bugEntity);

            //create file
            if(model.getFiles() != null && model.getFiles().length > 0){
                for (MultipartFile m : model.getFiles()){
                    if(!m.isEmpty()){
                        String urlFile =  fileUploadProvider.uploadFile("bug", m);
                        FileEntity fileEntity = new FileEntity();
                        String fileName = m.getOriginalFilename();
                        fileEntity.setLink(urlFile);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                        fileEntity.setCategory(Const.tableName.BUG.name());
                        fileEntity.setUploadedBy(SecurityUtils.getCurrentUser().getUser());
                        fileEntity.setObjectId(entity.getId());
                        iFileRepository.save(fileEntity);
                    }
                }
            }
            //create tag
            if(model.getTags() != null && model.getTags().size() > 0){
                for (TagModel tagModel : model.getTags()){
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if(tagEntity == null){
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
                        tagEntity1 =iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                    else if(tagEntity != null){
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }
            BugDto bugDto = BugDto.entityToDto(bugEntity);
            //chuyển trạng thái Subtask sang fixBug
            subTaskEntity.setStatus(Const.status.FIX_BUG.name());
            subTaskRepository.saveAndFlush(subTaskEntity);
            //Lưu dữ liệu vào bảng BugHistory
            saveDataInHistoryTable(bugEntity);
            iHistoryLogService.logUpdate(entity.getId(), entityOriginal,entity, Const.tableName.BUG);
            return bugDto;
        }catch (Exception e){
            return null;
        }
    }

    /*
     *@author:HieuMM_Cy
     *@since:9/16/2022-8:44 AM
     *@description:Bug Done
     *@update:
     **/
    public BugDto updateStatusBugDone(Long id) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask sang inreview
        SubTaskEntity subTaskEntity = subTaskRepository.findById(bugEntity.getSubTask().getId()).orElseThrow(() -> new CustomHandleException(11));
        subTaskEntity.setStatus(Const.status.IN_REVIEW.name());
        subTaskRepository.saveAndFlush(subTaskEntity);
        iBugRepository.saveAndFlush(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        saveDataInHistoryTable(bugEntity);
        BugDto bugDto = BugDto.entityToDto(bugEntity);
        return bugDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            BugEntity bugEntity = iBugRepository.findById(id).get();
            bugEntity.setIsDeleted(true);
            iBugRepository.save(bugEntity);
            iHistoryLogService.logDelete(id,bugEntity, Const.tableName.BUG);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public void deleteBug(Long id) {
        // delete historyBug
         this.iBugHistoryRepository.findByBugId(id).forEach(bugHistoryEntity -> this.iBugHistoryRepository.delete(bugHistoryEntity));
        // delete userProject
        this.userProjectRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), id).stream()
                .forEach(userProjectEntity -> this.userProjectRepository.delete(userProjectEntity));
        // delete tagRalation
        this.iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), id).stream()
                .forEach(tagRelationEntity -> this.iTagRelationRepository.delete(tagRelationEntity));
        // delete file
        iFileRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), id).stream().forEach(fileEntity -> this.fileService.deleteById(fileEntity.getId()));
        //delete Bug
        this.iBugRepository.deleteById(id);
    }
}
