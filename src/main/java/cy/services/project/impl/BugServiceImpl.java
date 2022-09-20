package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.BugDto;
import cy.entities.UserEntity;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
        BugEntity bugEntity = iBugRepository.findById(id).orElse(null);
        List<BugHistoryEntity> historyEntityList = iBugHistoryRepository.findAllByBugId(bugEntity);
        return BugDto.entityToDto(iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11)));
    }

    @Override
    public BugEntity getById(Long id) {
        return null;
    }

    public void saveDataInHistoryTable(BugEntity bugEntity, Date startDate, Date endDate){
        BugHistoryEntity bugHistoryEntity = new BugHistoryEntity();
        bugHistoryEntity.setBugId(bugEntity);
        bugHistoryEntity.setStartDate(startDate);
        bugHistoryEntity.setEndDate(endDate);
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
            bugEntity.setIsDeleted(false);
            //  if (bugEntity.getStartDate().compareTo(bugEntity.getCreatedDate()) != 0) {
            //nếu ngày tạo bug không phải ngày bắt đầu
            bugEntity.setStatus(Const.status.TO_DO.name());
          /*  }else if(bugEntity.getStartDate().compareTo(bugEntity.getCreatedDate()) == 0){
                //nếu ngày bắt đầu cũng là ngày tạo bug
                bugEntity.setStatus(Const.status.IN_PROGRESS.name());
            }*/

            BugEntity entity = iBugRepository.saveAndFlush(bugEntity);


            //create file
            if (model.getFiles() != null && model.getFiles().length > 0) {
                for (MultipartFile m : model.getFiles()) {
                    if (!m.isEmpty()) {
                        String urlFile = fileUploadProvider.uploadFile("bug", m);
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
            if (model.getTags() != null && model.getTags().size() > 0) {
                for (TagModel tagModel : model.getTags()) {
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if (tagEntity == null) {
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
                        tagEntity1 = iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(entity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    } else if (tagEntity != null) {
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.BUG.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(entity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }


            BugDto bugDto = BugDto.entityToDto(bugEntity);
            //chuyển trạng thái Subtask sang fixBug
            subTaskEntity.setStatus(Const.status.FIX_BUG.name());
            subTaskRepository.saveAndFlush(subTaskEntity);
            iHistoryLogService.logCreate(entity.getId(), entity, Const.tableName.BUG);
            return bugDto;
        } catch (Exception e) {
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
            BugEntity bugEntity = iBugRepository.findById(model.getId()).orElse(null);
            BugEntity bugEntityOriginal = (BugEntity) Const.copy(bugEntity);
            if(bugEntity == null)
                return null;
            Long userId = SecurityUtils.getCurrentUserId();
            if(userId == null)
                return null;
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            bugEntity.setCreateBy(userEntity);
            Date currentDate = new Date();
            bugEntity.setCreatedDate(currentDate);
            bugEntity.setStartDate(model.getStartDate());
            bugEntity.setEndDate(model.getEndDate());
            bugEntity.setDescription(model.getDescription());
            bugEntity.setName(model.getNameBug());
            bugEntity.setIsDefault(model.getIsDefault());
          /*  if(model.getStartDate().before(currentDate)){
                bugEntity.setStatus(Const.status.IN_PROGRESS.name());
            }
            else {
                bugEntity.setStatus(Const.status.TO_DO.name());
            }*/
            bugEntity.setUpdatedDate(currentDate);


            List<TagRelationEntity> tagRelationEntities = iTagRelationRepository.getByCategoryAndObjectId(Const.tableName.BUG.name(), bugEntity.getId());
            if(tagRelationEntities != null && tagRelationEntities.size() > 0){
                iTagRelationRepository.deleteByIdNative(tagRelationEntities.get(0).getId());
                iTagRelationRepository.deleteAllInBatch(tagRelationEntities);
            }
            if(model.getTags() != null && model.getTags().size() > 0){
                for (TagModel tagModel : model.getTags()){
                    TagEntity tagEntity = iTagRepository.findByName(tagModel.getName());
                    if(tagEntity == null){
                        TagEntity tagEntity1 = new TagEntity();
                        tagEntity1.setName(tagModel.getName());
                        tagEntity1 =iTagRepository.save(tagEntity1);
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity1.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                    else if(tagEntity != null){
                        TagRelationEntity tagRelationEntity = new TagRelationEntity();
                        tagRelationEntity.setCategory(Const.tableName.PROJECT.name());
                        tagRelationEntity.setIdTag(tagEntity.getId());
                        tagRelationEntity.setObjectId(bugEntity.getId());
                        iTagRelationRepository.save(tagRelationEntity);
                    }
                }
            }

            if(bugEntity.getAttachFiles() != null && bugEntity.getAttachFiles().size() > 0)
                bugEntity.getAttachFiles().clear();
            else{
                bugEntity.setAttachFiles(new ArrayList<>());
            }
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
                        fileEntity.setUploadedBy(userEntity);
                        fileEntity.setObjectId(bugEntity.getId());
                        iFileRepository.saveAndFlush(fileEntity);
                        bugEntity.getAttachFiles().add(fileEntity);
                    }
                }
            }
            iBugRepository.save(bugEntity);
            iHistoryLogService.logUpdate(bugEntity.getId(),bugEntityOriginal,bugEntity, Const.tableName.BUG);
            return BugDto.entityToDto(bugEntity);
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    /*
     *@author:HieuMM_Cy
     *@since:9/16/2022-8:44 AM
     *@description:Bug Done
     *@update:
     **/
    public BugDto updateStatusBugToSubTask(Long id, int status) {
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask sang inreview
        SubTaskEntity subTaskEntity = subTaskRepository.findById(bugEntity.getSubTask().getId()).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask
        if (bugEntity.getCreateBy().getUserId() == SecurityUtils.getCurrentUserId()) {//người tạo bug mới có thể đổi trạng thái
            switch (status) {
                case 1:
                    //reviewer fail xong thì
                    subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    break;
                case 2:
                    //reviewer oke xong thì chuyển bug sang done`
                    bugEntity.setStatus(Const.status.DONE.name());
                    subTaskEntity.setStatus(Const.status.DONE.name());
                    break;

            }
        }

        subTaskRepository.saveAndFlush(subTaskEntity);
        iBugRepository.saveAndFlush(bugEntity);


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }
    public BugDto updateStatusSubTaskToBug(Long id,int status){
        BugEntity bugEntity = iBugRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
        SubTaskEntity subTaskEntity = subTaskRepository.findById(bugEntity.getSubTask().getId()).orElseThrow(() -> new CustomHandleException(11));
        //chuyển trạng thái Subtask

        Date now = Date.from(Instant.now());
        if (bugEntity.getAssignTo().getUserId() == SecurityUtils.getCurrentUserId()) {//dev fix bug mới có thể đổi trạng thái bug để start
            switch (status) {
                case 1:
                    //dev bắt đầu fix bug
                    bugEntity.setStatus(Const.status.IN_PROGRESS.name());
                    subTaskEntity.setStatus(Const.status.FIX_BUG.name());
                    saveDataInHistoryTable(bugEntity,now,null);
                    break;
                case 2:
                    //dev kết thúc fix bug
                    subTaskEntity.setStatus(Const.status.IN_REVIEW.name());
                    bugEntity.setStatus(Const.status.IN_REVIEW.name());
                    List<BugHistoryEntity> bugHistoryEntities = iBugHistoryRepository.findAllByBugId(bugEntity);
                    for (BugHistoryEntity bugHistoryEntity : bugHistoryEntities){
                        if(bugHistoryEntity.getEndDate() == null){
                            bugHistoryEntity.setEndDate(now);
                            iBugHistoryRepository.save(bugHistoryEntity);
                        }
                    }
                    break;

            }
        }

        subTaskRepository.saveAndFlush(subTaskEntity);
        iBugRepository.saveAndFlush(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory


        BugDto bugDto = BugDto.entityToDto(bugEntity);
        //Lưu dữ liệu vào bảng BugHistory
        return bugDto;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            BugEntity bugEntity = iBugRepository.findById(id).get();
            bugEntity.setIsDeleted(true);
            iBugRepository.save(bugEntity);
            iHistoryLogService.logDelete(id, bugEntity, Const.tableName.BUG);
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
    
    public Page<BugDto> findAllBugOfProject(Long idProject, Pageable pageable) {
        return iBugRepository.findAllBugOfProject(idProject,pageable).map(data -> BugDto.entityToDto(data));
    }
}
