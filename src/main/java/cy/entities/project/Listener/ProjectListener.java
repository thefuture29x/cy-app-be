package cy.entities.project.Listener;

import cy.entities.UserEntity;
import cy.entities.project.*;
import cy.utils.Const;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PostLoad;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ProjectListener {

    public static EntityManagerFactory emf;

    @PostLoad
    public void afterGet(ProjectBaseEntity o) {
        String category;
        EntityManager manager = emf.createEntityManager();
        if (o instanceof ProjectEntity) {
            category = Const.tableName.PROJECT.name();
            //Get People
            Query personQuery = manager.createQuery("SELECT u from UserEntity u join UserProjectEntity p on p.idUser = u.userId where p.objectId = :objectId and p.category = :category and p.type = :type");
            personQuery.setParameter("objectId", o.getId());
            personQuery.setParameter("category", category);
            personQuery.setParameter("type", Const.type.TYPE_DEV.name());
            List<UserEntity> devList = personQuery.getResultList();
            personQuery.setParameter("type", Const.type.TYPE_FOLLOWER.name());
            List<UserEntity> followList = personQuery.getResultList();
            personQuery.setParameter("type", Const.type.TYPE_VIEWER.name());
            List<UserEntity> viewList = personQuery.getResultList();
            ((ProjectEntity) o).setDevTeam(devList);
            ((ProjectEntity) o).setFollowTeam(followList);
            ((ProjectEntity) o).setViewTeam(viewList);
//
//
//            //Get Attached Files
//            Query projectFileQuery = manager.createQuery("SELECT f from FileEntity f join ProjectEntity p on p.id = f.objectId where f.objectId = :objectId and f.category =:category");
//            projectFileQuery.setParameter("objectId", o.getId());
//            projectFileQuery.setParameter("category", category);
//            List<FileEntity> fileEntityList = projectFileQuery.getResultList();
//            ((ProjectEntity) o).setAttachFiles(null);
//            ((ProjectEntity) o).setAttachFiles(fileEntityList);
        } else if (o instanceof FeatureEntity) {
            category = Const.tableName.FEATURE.name();
            //Get People
            Query featurePersonQuery = manager.createQuery("SELECT u from UserEntity u join UserProjectEntity p on p.idUser = u.userId where p.objectId = :objectId and p.category = :category");
            featurePersonQuery.setParameter("objectId", o.getId());
            featurePersonQuery.setParameter("category", category);
            List<UserEntity> devLists = featurePersonQuery.getResultList();
            ((FeatureEntity) o).setDevTeam(devLists);
//
//            //Get Attached Files
//            Query featureFileQuery = manager.createQuery("SELECT f from FileEntity f join FeatureEntity p on p.id = f.objectId where f.objectId = :objectId and f.category =:category");
//            featureFileQuery.setParameter("objectId", o.getId());
//            featureFileQuery.setParameter("category", category);
//            List<FileEntity> fileEntityList = featureFileQuery.getResultList();
//            ((FeatureEntity) o).setAttachFiles(fileEntityList);
        } else if (o instanceof TaskEntity) {
            category = Const.tableName.TASK.name();
            //Get People
            Query taskPersonQuery = manager.createQuery("SELECT u from UserEntity u join UserProjectEntity p on p.idUser = u.userId where p.objectId = :objectId and p.category = :category");
            taskPersonQuery.setParameter("objectId", o.getId());
            taskPersonQuery.setParameter("category", category);
            List<UserEntity> devLists = taskPersonQuery.getResultList();
            ((TaskEntity) o).setDevTeam(devLists);
//
//            //Get Attached Files
//            Query taskFileQuery = manager.createQuery("SELECT f from FileEntity f join TaskEntity p on p.id = f.objectId where f.objectId = :objectId and f.category =:category");
//            taskFileQuery.setParameter("objectId", o.getId());
//            taskFileQuery.setParameter("category", category);
//            List<FileEntity> fileEntityList = taskFileQuery.getResultList();
//            ((TaskEntity) o).setAttachFiles(fileEntityList);
        } else{
            category = Const.tableName.SUBTASK.name();
            //Get People
            Query subTaskPersonQuery = manager.createQuery("SELECT u from UserEntity u join UserProjectEntity p on p.idUser = u.userId where p.objectId = :objectId and p.category = :category");
            subTaskPersonQuery.setParameter("objectId", o.getId());
            subTaskPersonQuery.setParameter("category", category);
            List<UserEntity> devLists = subTaskPersonQuery.getResultList();
            ((SubTaskEntity) o).setDevTeam(devLists);
        }


        Field idField;
        try {
            idField = o.getClass().getSuperclass().getDeclaredField("id");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        idField.setAccessible(true);
        //Get Tag
        Query tagQuery = manager.createQuery("SELECT t FROM TagEntity t JOIN TagRelationEntity tr on tr.objectId = :objectId and tr.category = :category");
        try {
        tagQuery.setParameter("objectId", idField.get(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        tagQuery.setParameter("category", category);
        List<TagEntity> tagList = tagQuery.getResultList();
        o.setTagList(tagList);
        //Get File
        Query fileQuery = manager.createQuery("SELECT f from FileEntity f where f.objectId = :objectId and f.category =:category");
        fileQuery.setParameter("objectId", o.getId());
        fileQuery.setParameter("category", category);
        List<FileEntity> fileEntityList = fileQuery.getResultList();
        o.setAttachedFiles(fileEntityList);
        idField.setAccessible(false);
    }


//    public static void main(String[] args) throws NoSuchFieldException {
//        ProjectEntity p = new ProjectEntity();
//        Field idField = p.getClass().getSuperclass().getDeclaredField("id");
//
//        System.out.println("args = " + Arrays.deepToString(args));
//    }
}
