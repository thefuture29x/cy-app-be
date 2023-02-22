package cy.services.project.impl;

import cy.dtos.CustomHandleException;
import cy.dtos.project.HistoryLogDto;
import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.models.attendance.NotificationModel;
import cy.repositories.project.IHistoryLogRepository;
import cy.services.attendance.INotificationService;
import cy.services.project.IHistoryLogService;
import cy.utils.Const;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.reflect.Modifier.PUBLIC;

@Component
@Transactional
public class HistoryServiceLogImpl implements IHistoryLogService {


    private final IHistoryLogRepository historyLogRepository;
    @Autowired
    INotificationService iNotificationService;

    public HistoryServiceLogImpl(IHistoryLogRepository historyLogRepository) {
        this.historyLogRepository = historyLogRepository;
    }


    @Override
    public List<HistoryLogDto> findAll() {
        return null;
    }

    @Override
    public Page<HistoryLogDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<HistoryLogDto> findAll(Specification<HistoryEntity> specs) {
        return null;
    }

    @Override
    public Page<HistoryLogDto> filter(Pageable page, Specification<HistoryEntity> specs) {
        return this.historyLogRepository.findAll(specs, page).map(HistoryLogDto::toDto);
    }

    @Override
    public HistoryLogDto findById(Long id) {
        return null;
    }

    @Override
    public HistoryEntity getById(Long id) {
        return null;
    }

    @Override
    public HistoryLogDto add(HistoryEntity model) {
        return null;
    }

    @Override
    public List<HistoryLogDto> add(List<HistoryEntity> model) {
        return null;
    }

    @Override
    public HistoryLogDto update(HistoryEntity model) {
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

    @Override
    public void logChangedTeamInProject(Long objectId,
                                        Object o,
                                        List<List<UserEntity>> oL,
                                        List<List<UserEntity>> nL,
                                        Const.tableName category) {
        // annotation on class
        HistoryLogTitle annotationClass = o.getClass().getAnnotation(HistoryLogTitle.class);
        if (annotationClass == null)
            throw new CustomHandleException(371);

        UserEntity user = SecurityUtils.getCurrentUser().getUser();
        HistoryEntity his1 = this.checkTeam(oL.get(0), nL.get(0), "người phụ trách.<br>");
        HistoryEntity his2 = this.checkTeam(oL.get(1), nL.get(1), "người người theo dõi.<br>");
        HistoryEntity his3 = this.checkTeam(oL.get(2), nL.get(1), "người xem.<br>");

        if (his1 != null && his2 == null && his3 == null) {
            his1.setObjectId(objectId);
            his1.setUserId(user);
            his1.setCategory(category.name());
            this.historyLogRepository.saveAndFlush(his1);
        } else if (his1 == null && his2 != null && his3 == null) {
            his2.setObjectId(objectId);
            his2.setUserId(user);
            his2.setCategory(category.name());
            this.historyLogRepository.saveAndFlush(his2);
        } else if (his1 == null && his2 == null && his3 != null) {
            his3.setObjectId(objectId);
            his3.setUserId(user);
            his3.setCategory(category.name());
            this.historyLogRepository.saveAndFlush(his3);
        } else {
            StringBuilder content = new StringBuilder(" đã thay đổi ").append(annotationClass.title()).append(".<br>");
            HistoryEntity his = HistoryEntity.builder()
                    .ObjectId(objectId)
                    .category(category.name())
                    .userId(user)
                    .content(content.toString())
                    .build();
            this.historyLogRepository.saveAndFlush(his);
        }
    }

    private HistoryEntity checkTeam(List<UserEntity> oL, List<UserEntity> nL, String prefix) {

        if (oL == null && nL == null)
            return null;

        StringBuilder changedContent = new StringBuilder(" đã cập nhật ")
                .append(prefix);
        HistoryEntity historyEntity = HistoryEntity
                .builder()
                .build();

        if (oL == null || nL == null) {
            if (oL == null) {
                for (UserEntity user : oL) {
                    changedContent
                            .append(user.getUserName())
                            .append(" đã bị xóa.<br>");
                }
            } else {
                for (UserEntity user : nL) {
                    changedContent
                            .append(user.getUserName())
                            .append(" đã được thêm xóa.<br>");
                }
            }
        }

        if (!oL.equals(nL)) {
            oL.forEach(userEntity -> {
                if (!nL.contains(userEntity)) {
                    changedContent
                            .append(userEntity.getUserName())
                            .append(" đã bị xóa.<br>");
                }
            });
            nL.forEach(userEntity -> {
                if (!oL.contains(userEntity)) {
                    changedContent
                            .append(userEntity.getUserName())
                            .append(" đã được thêm.<br>");
                }
            });
        }

        historyEntity.setContent(changedContent.toString());
        return historyEntity;
    }

    @Override
    public void logCreate(Long objectId, Object object, Const.tableName category, String nameObject) {
        UserEntity user = SecurityUtils.getCurrentUser().getUser();

        // annotation on class
        HistoryLogTitle annotationClass = object.getClass().getAnnotation(HistoryLogTitle.class);
        if (annotationClass == null)
            throw new CustomHandleException(371);

        StringBuilder content = new StringBuilder()
                .append("<p> đã thêm mới ")
                .append(annotationClass.title() + " ")
                .append("<b>"+nameObject+"</b>.</p>");


        this.historyLogRepository.saveAndFlush(HistoryEntity
                .builder()
                .ObjectId(objectId)
                .category(category.name())
                .content(content.toString())
                .userId(user)
                .build());

        iNotificationService.add(NotificationModel.builder()
                .title(user.getFullName() + " đã tạo mới " + annotationClass.title() + " " + nameObject)
                .content(content.toString())
                .objectId(objectId)
                .category(category.name())
                .build());
    }

    @Override
    public boolean logUpdate(Long objectId, Object original, Object newObj, Const.tableName category) {
        UserEntity user = SecurityUtils.getCurrentUser().getUser(); // get current user
        List<Field> originalInsFsList = null; // get list fields from object

        if (original.getClass().getSuperclass().getName().equals(ProjectBaseEntity.class.getName())) { // check if the class has super class is ProjectBaseEntity class
            originalInsFsList = (List<Field>) Const.combineMultipleArrays(original.getClass().getDeclaredFields(), original.getClass().getSuperclass().getDeclaredFields());
        } else
            originalInsFsList = (List<Field>) Const.combineMultipleArrays(original.getClass().getDeclaredFields());

        // if 2 object is not the same class, it's not possible to compare
        if (!original.getClass().getName().equals(newObj.getClass().getName())
                || originalInsFsList.size() == 0)
            throw new CustomHandleException(372);

        // get field length from field list
        int fieldLength = originalInsFsList.size();

        // for check changed count from object
        AtomicInteger changedCount = new AtomicInteger(0);
        StringBuilder changedContent = new StringBuilder();

        // for check changed attach files
        AtomicInteger changedFiles = new AtomicInteger(0);
        StringBuilder checkFileChangeContent = new StringBuilder();

        HistoryEntity historyEntity = HistoryEntity
                .builder()
                .id(null)
                .ObjectId(objectId)
                .category(category.name())
                .userId(user)
                .build();
        HistoryLogTitle annotationClass = original.getClass().getAnnotation(HistoryLogTitle.class);

        for (int i = 0; i < fieldLength; ++i) {
            changedContent.setLength(0);
            Field field = originalInsFsList.get(i);
            // IGNORE STATIC FIELDS
            if (field.getModifiers() == 9 || field.getModifiers() == 10)
                continue;

//            // IGNORE WHEN CHANGE GT 1
//            if (changedCount.get() > 1)
//                break;

            // annotation on field
            HistoryLogTitle annotationField = field.getAnnotation(HistoryLogTitle.class);
            ;
            if (annotationField == null) {
                throw new CustomHandleException(371);
            }
            if (annotationField.ignore())
                continue;

            if (field.getModifiers() == PUBLIC) {
                compareObjectFields(annotationField, field, original, newObj, changedCount, changedContent, checkFileChangeContent, changedFiles, historyEntity);

            } else {
                field.setAccessible(true); // open access to private field
                compareObjectFields(annotationField, field, original, newObj, changedCount, changedContent, checkFileChangeContent, changedFiles, historyEntity);
                field.setAccessible(false); // close access to restore original
            }
        }

        if (changedCount.get() == 0)
            return false;

        else if (changedCount.get() == 1 && changedFiles.get() == 1)  // user only change attach files
            historyEntity.setContent(checkFileChangeContent.toString());
        else if (changedCount.get() == 1) {
            historyEntity.setContent(changedContent.toString());
        } else {// user only change 1 field
            // annotation on class
            if (annotationClass == null) {
                throw new CustomHandleException(371);
            }
            changedContent.setLength(0);
            changedContent
                    .append("<p> đã cập nhật ")
                    .append(annotationClass.title())
                    .append(".</p>");
            historyEntity.setContent(changedContent.toString());
        }
        iNotificationService.add(NotificationModel.builder()
                .title(user.getFullName() + " đã thay đổi " + annotationClass.title())
                .content(historyEntity.getContent())
                .objectId(objectId)
                .category(historyEntity.getCategory())
                .build());

//        this.historyLogRepository.saveAndFlush(historyEntity);
        return true;
    }

    @Override
    public void logDelete(Long objectId, Object object, Const.tableName category, String nameObject) {
        UserEntity user = SecurityUtils.getCurrentUser().getUser();

        // annotation on class
        HistoryLogTitle annotationClass = object.getClass().getAnnotation(HistoryLogTitle.class);
        if (annotationClass == null) {

            throw new CustomHandleException(371);
        }

        StringBuilder content = new StringBuilder()
                .append("<p> đã xóa ")
                .append(category.name().toLowerCase())
                .append(" <b>"+nameObject+"</b>.</p>");

        this.historyLogRepository.saveAndFlush(HistoryEntity
                .builder()
                .ObjectId(objectId)
                .category(category.name())
                .content(content.toString())
                .userId(user)
                .build());

        iNotificationService.add(NotificationModel.builder()
                .title(user.getFullName() + " đã xóa " + annotationClass.title())
                .content(content.toString())
                .objectId(objectId)
                .category(category.name())
                .build());
    }

    @Override
    public void log(Long objectId, String content, Const.tableName category) {
        UserEntity user = SecurityUtils.getCurrentUser().getUser();

        this.historyLogRepository.saveAndFlush(HistoryEntity
                .builder()
                .ObjectId(objectId)
                .category(category.name())
                .content(content)
                .userId(user)
                .build());
    }

    @Override
    public Page<HistoryLogDto> getAllHistoryCreateObject(Const.tableName category, Pageable pageable) {
        return null;
    }

    @Override
    public Page<HistoryLogDto> getAllHistoryOfBug(Long idProject, Pageable pageable) {
        return this.historyLogRepository.getAllHistoryOfBug(idProject,pageable).map(data -> HistoryLogDto.toDto(data));
    }

    private void compareObjectFields(HistoryLogTitle annotation,
                                     Field field,
                                     Object original,
                                     Object newObj,
                                     AtomicInteger changedCount,
                                     StringBuilder changedContent,
                                     StringBuilder checkFileChangeContent,
                                     AtomicInteger changedFiles,
                                     HistoryEntity historyEntity) {

        //            field.getGenericType().getTypeName()
        String className = field.getType().getName();


        try {
            Object val1 = field.get(original);  // gia tri gia tri cu
            Object val2 = field.get(newObj); // gia tri moi

            if (val1 == null && val2 == null) { // nothing do
            } else if (val1 == null || val2 == null) { // for only 1 or 2 changed
                changedCount.incrementAndGet();
                if (annotation.isMultipleFiles()) {
                    changedContent = checkMultipleFileUpdate(changedContent,historyEntity,val1,val2);
                } else if (annotation.isListType()) { // for list user
                    if (field.getGenericType().getTypeName().equals("java.util.List<cy.entities.UserEntity>")) {
                        changedContent = checkUserEntityUpdate(annotation,changedContent,val1,val2);
                    }
                } else if (annotation.isTagFields()) { // for list tag
                    if (field.getGenericType().getTypeName().equals("java.util.List<cy.entities.TagEntity>")) {
                        changedContent = checkTagEntityUpdate(annotation,changedContent,val1,val2);
                    }
                } else if (annotation.isDateType()) { // for date
                    changedContent = checkDateUpdate(annotation,changedContent,val1,val2);
                }
                else if (annotation.title().equals("trạng thái")) {
                    if (!val1.equals(val2)) {
                        String title = annotation.getClass().getAnnotation(HistoryLogTitle.class).title();
                        if (!title.equals("project") && !title.equals("feature")){
                            HistoryEntity newHistoryEntity = HistoryEntity
                                    .builder()
                                    .id(null)
                                    .ObjectId(historyEntity.getObjectId())
                                    .category(historyEntity.getCategory())
                                    .userId(null)
                                    .content("<p> đã được cập nhật trạng thái.</p>")
                                    .build();
                            this.historyLogRepository.saveAndFlush(newHistoryEntity);
                        }
                    }
                }
                else if (className.equals(FileEntity.class.getName())) { // for avatar
                    changedContent = setChangedContentToUpdated(annotation,changedContent);
//                    if (val1 != null) {
//                        changedContent.append(createHtmlATag((FileEntity) field.get(original), " đã bị xóa"));
//                    }
                } else {
                    if (val1 == null && val2 != null) {
                        changedContent.append("<p> đã thêm ")
                                .append(annotation.title())
                                .append(" mới.</p>");
                    } else {
                        changedContent.append("<p> đã xóa ")
                                .append(annotation.title())
                                .append(".</p>");
                    }
                }
            } else if (!val1.equals(val2)) {
                changedCount.incrementAndGet();
                if (annotation.isMultipleFiles()) { // for multiple file
                    changedContent = checkMultipleFileUpdate(changedContent,historyEntity,val1,val2);
                } else if (annotation.isListType()) {
                    if (field.getGenericType().getTypeName().equals("java.util.List<cy.entities.UserEntity>")) {
                       changedContent = checkUserEntityUpdate(annotation,changedContent,val1,val2);
                    }
                } else if (annotation.isTagFields()) { // for list tag
                    if (field.getGenericType().getTypeName().equals("java.util.List<cy.entities.project.TagEntity>")) {
                        changedContent = checkTagEntityUpdate(annotation,changedContent,val1,val2);
                    }
                } else if (annotation.isDateType()) { // for date
                    changedContent = checkDateUpdate(annotation,changedContent,val1,val2);
                } else if (annotation.title().equals("trạng thái")) {
                    if (!val1.equals(val2)) {

                        HistoryEntity newHistoryEntity = HistoryEntity
                                .builder()
                                .id(null)
                                .ObjectId(historyEntity.getObjectId())
                                .category(historyEntity.getCategory())
                                .userId(null)
                                .content("<p> đã được cập nhật trạng thái.</p.")
                                .build();
                        this.historyLogRepository.saveAndFlush(newHistoryEntity);
                    }
                } else if (className.equals(FileEntity.class.getName())) { // FOR FILE avatar
                    changedContent = setChangedContentToUpdated(annotation,changedContent);
                } else {
                    changedContent.append("<p> đã thay đổi ")
                            .append(annotation.title())
                            .append(".</p>");
                }
            }

            createNewHistory(historyEntity, changedContent);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new CustomHandleException(373);
        }
    }
    private StringBuilder checkUserEntityUpdate(HistoryLogTitle annotation,StringBuilder changedContent, Object val1, Object val2){
        List<UserEntity> originalUserList = val1 != null ? (List<UserEntity>) val1 : new ArrayList<>();
        List<UserEntity> newUserList = val2 != null  ? (List<UserEntity>) val2 : new ArrayList<>();
        if (!new HashSet<>(originalUserList).equals(new HashSet<>(newUserList))){
            return changedContent.append("<p> đã cập nhật ")
                    .append(annotation.title())
                    .append(".</p>");
        }
        return new StringBuilder();
    }
    private StringBuilder checkTagEntityUpdate(HistoryLogTitle annotation,StringBuilder changedContent, Object val1, Object val2){
        List<TagEntity> originalTagList = (List<TagEntity>) val1;
        List<TagEntity> newTagList = (List<TagEntity>) val2;

        if (originalTagList.size() == newTagList.size()){
            List<String> originalTagListName = new ArrayList<>();
            List<String> newTagListName = new ArrayList<>();

            originalTagList.stream().forEach(data -> originalTagListName.add( data.getName()));
            newTagList.stream().forEach(data -> newTagListName.add( data.getName()));

            if (!new HashSet<>(originalTagListName).equals(new HashSet<>(newTagListName))){
                return changedContent.append("<p> đã cập nhật ")
                        .append(annotation.title())
                        .append(".</p>");
            }
            return new StringBuilder();
        }else {
            return changedContent.append("<p> đã cập nhật ")
                    .append(annotation.title())
                    .append(".</p>");
        }
    }
    private StringBuilder checkDateUpdate(HistoryLogTitle annotation,StringBuilder changedContent, Object val1, Object val2){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if (!simpleDateFormat.format(val1).equals(simpleDateFormat.format(val2))) {
            return changedContent.append("<p> đã cập nhật ")
                    .append(annotation.title())
                    .append(".</p>");
        }
        return new StringBuilder();
    }
    private StringBuilder checkMultipleFileUpdate(StringBuilder changedContent,HistoryEntity historyEntity, Object val1, Object val2){
        List<FileEntity> originalFiles = (List<FileEntity>) val1;
        List<FileEntity> newFiles = (List<FileEntity>) val2;

        if (!new HashSet<>(originalFiles).equals(new HashSet<>(newFiles))){
            for (FileEntity entity : originalFiles) {
                boolean haveDeleteFile = false;
                if (!newFiles.contains(entity)){
                    changedContent.append("<p> đã xóa file đính kèm.</p>");
                    createNewHistory(historyEntity, changedContent);
                    changedContent.setLength(0);
                    haveDeleteFile = true;
                }
                if(haveDeleteFile) break;
            }
            for (FileEntity entity: newFiles) {
                boolean haveNewFile = false;
                if (!originalFiles.contains(entity)){
                    changedContent.append("<p> đã thêm mới file đính kèm.</p>");
                    createNewHistory(historyEntity, changedContent);
                    changedContent.setLength(0);
                    haveNewFile = true;
                }
                if(haveNewFile) break;
            }

        }
//        if (val1 == null && val2 != null) {
//            changedContent.append(" đã cập nhật file đính kèm!");
//            newFiles.forEach(file -> {
//                changedContent.append(createHtmlATag(file, " đã được thêm"));
//            });
//
//        } else {
//            changedContent.append(" đã xóa file đính kèm!");
//            originalFiles.forEach(file -> {
//                changedContent.append(createHtmlATag(file, " đã bị xóa"));
//            });
//        }

        return new StringBuilder();
    }
    private void createNewHistory(HistoryEntity historyEntity, StringBuilder changedContent){
        if (!changedContent.toString().equals("")) {
            HistoryEntity newHistoryEntity = HistoryEntity
                    .builder()
                    .id(null)
                    .ObjectId(historyEntity.getObjectId())
                    .category(historyEntity.getCategory())
                    .userId(historyEntity.getUserId())
                    .content(changedContent.toString())
                    .build();
            this.historyLogRepository.saveAndFlush(newHistoryEntity);
        }
    }
    private StringBuilder setChangedContentToUpdated(HistoryLogTitle annotation,StringBuilder changedContent){
        return changedContent.append("<p> đã cập nhật ")
                .append(annotation.title())
                .append(".</p>");
    }

    private StringBuilder createHtmlATag(FileEntity file, String suffix) {
        StringBuilder aHtmlTag = new StringBuilder("<br><a download href='");
        aHtmlTag.append(file.getLink()); // add url at here
        aHtmlTag.append("'>");

        aHtmlTag.append(file.getFileName()); // add file name at here
        aHtmlTag.append(suffix); // add suffix at here
        aHtmlTag.append(".</a>"); // end a tag
        return aHtmlTag;
    }


//    public static void main(String[] args) {
//        ProjectEntity p = new ProjectEntity();
//        ProjectEntity p1 = new ProjectEntity();
//    }
}
