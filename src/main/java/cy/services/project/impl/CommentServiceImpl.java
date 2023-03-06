package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.CommentDto;
import cy.entities.project.CommentEntity;
import cy.entities.project.FileEntity;
import cy.models.project.CommentModel;
import cy.models.project.FileModel;
import cy.repositories.project.ICommentRepository;
import cy.repositories.project.IFileRepository;
import cy.repositories.project.IUserProjectRepository;
import cy.repositories.project.specification.CommentSpecification;
import cy.repositories.project.specification.FileSpecification;
import cy.services.project.ICommentService;
import cy.services.project.IFileService;
import cy.services.project.IHistoryLogService;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CommentServiceImpl implements ICommentService {

    private final ICommentRepository commentRepository;

    private final IFileRepository fileRepository;

    private final IFileService fileService;

    private final IHistoryLogService historyLogService;
    private final IUserProjectRepository userProjectRepository;


    public CommentServiceImpl(ICommentRepository commentRepository, IFileRepository fileRepository, IFileService fileService, IHistoryLogService historyLogService,IUserProjectRepository userProjectRepository) {
        this.commentRepository = commentRepository;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.historyLogService = historyLogService;
        this.userProjectRepository = userProjectRepository;
    }


    @Override
    public List<CommentDto> findAll() {
        return null;
    }

    @Override
    public Page<CommentDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<CommentDto> findAll(Specification<CommentEntity> specs) {
        return null;
    }

    @Override
    public Page<CommentDto> filter(Pageable page, Specification<CommentEntity> specs) {
        return this.commentRepository.findAll(specs, page).map(CommentDto::toDto);
    }

    @Override
    public CommentDto findById(Long id) {
        return CommentDto.toDto(this.getById(id));
    }

    @Override
    public CommentEntity getById(Long id) {
        return this.commentRepository.findById(id).orElseThrow(() -> new CustomHandleException(401));
    }

    @Override
    public CommentDto add(CommentModel model) {
        CommentEntity commentEntity = CommentModel.toEntity(model);

        // check the user is on the project's dev list
        Long idUser = SecurityUtils.getCurrentUserId();
        List<String> listType = new ArrayList<>();
        listType.add(Const.type.TYPE_DEV.toString());
        listType.add(Const.type.TYPE_FOLLOWER.toString());

        if (model.getCategory().toString().equals("FEATURE")){
            List<Long> listIdDevInProject_Feature = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(model.getObjectId(), listType);
            if(!listIdDevInProject_Feature.stream().anyMatch(idUser::equals)){
                throw new CustomHandleException(5);
            }
        }

//        switch (model.getCategory().toString()){
//            case "FEATURE":
//                List<Long> listIdDevInProject_Feature = userProjectRepository.getAllIdDevOfProjectByFeatureIdInThisProject(model.getObjectId(), listType);
//                if(!listIdDevInProject_Feature.stream().anyMatch(idUser::equals)){
//                    throw new CustomHandleException(5);
//                }
//            case "TASK":
//                List<Long> listIdDevInProject_Task = userProjectRepository.getAllIdDevOfProjectByTaskIdInThisProject(model.getObjectId(), listType);
//                if(!listIdDevInProject_Task.stream().anyMatch(idUser::equals)){
//                    throw new CustomHandleException(5);
//                }
//            case "SUBTASK":
//                List<Long> listIdDevInProject_Subtask = userProjectRepository.getAllIdDevOfProjectBySubTaskIdInThisProject(model.getObjectId(), listType);
//                if(!listIdDevInProject_Subtask.stream().anyMatch(idUser::equals)){
//                    throw new CustomHandleException(5);
//                }
//            case "BUG":
//                List<Long> listIdDevInProject_Bug = userProjectRepository.getAllIdDevOfProjectByBugIdInThisProject(model.getObjectId(), listType);
//                if(!listIdDevInProject_Bug.stream().anyMatch(idUser::equals)){
//                    throw new CustomHandleException(5);
//                }
//        }

        if (model.getIdParent() != null) {
            CommentEntity parentComment = this.getById(model.getIdParent());
            if (!model.getObjectId().equals(parentComment.getObjectId())) {
                throw new CustomHandleException(402);
            }
            commentEntity.setIdParent(parentComment);
        }

        commentEntity.setUserId(SecurityUtils.getCurrentUser().getUser());
        this.commentRepository.saveAndFlush(commentEntity);
        //    for save File
        if (model.getNewFiles() != null) {
            List<FileEntity> files = model.getNewFiles().stream().map(file -> this.fileService.addEntity(FileModel.builder()
                    .file(file)
                    .category(Const.tableName.COMMENT.name())
                    .objectId(commentEntity.getId())
                    .build())).collect(Collectors.toList());

            if (files.size() > 0)
                commentEntity.setAttachFiles(files);
        }

        this.historyLogService.logCreate(commentEntity.getId(), commentEntity, Const.tableName.COMMENT,"");
        return CommentDto.toDto(commentEntity);
    }


    @Override

    public List<CommentDto> add(List<CommentModel> model) {
        return null;
    }

    @Override
    public CommentDto update(CommentModel model) {
        if (model.getFileUrlsKeeping() != null){
            fileRepository.deleteFileExistInObject(model.getFileUrlsKeeping(), Const.tableName.COMMENT.name(), model.getId());
        }else {
            fileRepository.deleteAllByCategoryAndObjectId(Const.tableName.COMMENT.name(), model.getId());
        }
        CommentEntity commentEntity = this.getById(model.getId());
        CommentEntity originComment = (CommentEntity) Const.copy(commentEntity);
        Long userId = SecurityUtils.getCurrentUserId();
        if (!commentEntity.getUserId().getUserId().equals(userId)) {
            throw new CustomHandleException(403); // not comment of user
        }

        if (!model.getCategory().name().equals(commentEntity.getCategory())
                || !model.getObjectId().equals(commentEntity.getObjectId()))
            throw new CustomHandleException(402);

        commentEntity.setContent(model.getContent());


        //   for save file
        List<FileEntity> files = new ArrayList<>();
        if (!commentEntity.getAttachFiles().isEmpty()) {
            commentEntity.getAttachFiles().forEach(f -> {
//                if (model.getAttachFiles().contains(f.getId()))
                    files.add(f);
            });
//            commentEntity.setAttachFiles(null);
        } else commentEntity.setAttachFiles(null);

        if (model.getNewFiles() != null) {
            files.addAll(model.getNewFiles().stream().map(file -> this.fileService.addEntity(FileModel.builder()
                    .file(file)
                    .category(Const.tableName.COMMENT.name())
                    .objectId(commentEntity.getId())
                    .build())).collect(Collectors.toList()));
        }


        if (files.size() > 0)
            commentEntity.setAttachFiles(files);

        this.commentRepository.save(commentEntity);

        this.historyLogService.logUpdate(commentEntity.getId(), originComment, commentEntity, Const.tableName.COMMENT);
        return CommentDto.toDto(commentEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            commentRepository.deleteAllByIdParent_Id(id);
            commentRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }


    @Override
    public Page<CommentDto> findAllByCategoryAndObjectId(Pageable pageable, Const.tableName category, Long objectId) {
        return this.commentRepository.findAll(Specification.where(
                CommentSpecification.byCategory(category)
        ).and(
                CommentSpecification.byObjectId(objectId)
        ).and(
                CommentSpecification.byParentId(null)
        ), pageable).map(CommentDto::toDto);
    }

    @Override
    public List<CommentDto> findAllChildByParentId(Pageable pageable, Long idParent) {
        return this.commentRepository.findAll(Specification.where(CommentSpecification.byParentId(idParent)), pageable).map(CommentDto::toDto).getContent();
    }
}
