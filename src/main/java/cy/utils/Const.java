package cy.utils;

import cy.dtos.CustomHandleException;
import cy.entities.project.FileEntity;
import cy.entities.project.ProjectEntity;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.PUBLIC;

public class Const {
    /**
     * combine multiple list array into 1 list
     *
     * @return new collection
     * @Param list array
     **/
    public static Collection<?> combineMultipleArrays(Object[]... arrays) {
        Collection<Object> result = new ArrayList<>();
        for (Object[] array : arrays) {
            Collections.addAll(result, array);
        }
        return result;
    }

    /**
     * Copy properties from source
     *
     * @return new object with properties from source
     * @throws CustomHandleException cannot copy properties
     * @Param source is source object
     * @Note properties from new object will not reference to source
     **/
    public static Object copy(Object source) {

        try {
            Object newObj = initializeObject(source); // initialize new object
            BeanUtils.copyProperties(source, newObj); // copy properties from source to new object
            List<Field> defaultFields; // get all fields of source object
            List<Field> listTypeFs = new ArrayList<>(); // list field is List class type
            List<Field> setTypeFs = new ArrayList<>(); // list field is Set class type

            if (!source.getClass().getSuperclass().getName().equals(Object.class.getName())) { // check if the class has super class is ProjectBaseEntity class
                defaultFields = (List<Field>) Const.combineMultipleArrays(source.getClass().getDeclaredFields(), source.getClass().getSuperclass().getDeclaredFields());
            } else defaultFields = (List<Field>) Const.combineMultipleArrays(source.getClass().getDeclaredFields());

            // separate list and set field
            for (Field f : defaultFields) {
                if (f.getType().getName().equals(List.class.getName())) {
                    listTypeFs.add(f);
                } else if (f.getType().getName().equals(java.util.Set.class.getName())) {
                    setTypeFs.add(f);
                }
            }

            // copy list field
            if (listTypeFs.size() > 0) {
                listTypeFs.forEach(f -> {
                    if (f.getModifiers() != 9 && f.getModifiers() != 10) {
                        try {
                            if (f.getModifiers() != PUBLIC) f.setAccessible(true);
                            List list = (List) f.get(source);

                            if (list != null) f.set(newObj, list.stream().collect(Collectors.toList()));

                            if (f.getModifiers() != PUBLIC) f.setAccessible(false);

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new CustomHandleException(6);
                        }
                    }
                });
            }

            // copy set field
            if (setTypeFs.size() > 0) {
                setTypeFs.forEach(f -> {
                    if (f.getModifiers() != 9 && f.getModifiers() != 10) {
                        try {
                            if (f.getModifiers() != PUBLIC) f.setAccessible(true);
                            Set setL = (Set) f.get(source);
                            if (setL != null) f.set(newObj, setL.stream().collect(Collectors.toSet()));
                            if (f.getModifiers() != PUBLIC) f.setAccessible(false);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new CustomHandleException(6);
                        }
                    }
                });
            }
            return newObj;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomHandleException(6);
        }
    }

    private static Object initializeObject(Object source) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return source.getClass().getDeclaredConstructor(null).newInstance(null);
    }

    public static void main(String[] args) {
        ProjectEntity project = new ProjectEntity();
        List<FileEntity> files = new ArrayList<>();
        files.add(FileEntity.builder().id(1L).build());
        files.add(FileEntity.builder().id(2L).build());
        files.add(FileEntity.builder().id(3L).build());
        project.setAttachFiles(files);

        ProjectEntity project2 = (ProjectEntity) copy(project);
        project2.getAttachFiles().remove(0);

        System.out.println("project2 = " + project2.getAttachFiles().size());
        System.out.println("project = " + project.getAttachFiles().size());

    }


    public enum tableName {
        PROJECT, FEATURE, TASK, SUBTASK, BUG, BUG_HISTORY, FILE, TAG, HISTORY, COMMENT, USER_PROJECT, TAG_RELATION,LIST_BUG
    }


    public enum type {
        TYPE_DEV, TYPE_FOLLOWER, TYPE_VIEWER, TYPE_REVIEWER,

    }

    public enum status {
        TO_DO, IN_PROGRESS, PENDING, IN_REVIEW, DONE, FIX_BUG,
    }

    public enum priority {
        CRITICAL, HIGH, MEDIUM, LOW,
    }
}
