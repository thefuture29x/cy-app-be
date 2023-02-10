package cy.entities.project;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryLogTitle {

    String title();

    boolean isMultipleFiles() default false;
    boolean isListType() default false;
    boolean isTagFields() default false;

    boolean ignore() default false;

    boolean isDateType() default false;

}
