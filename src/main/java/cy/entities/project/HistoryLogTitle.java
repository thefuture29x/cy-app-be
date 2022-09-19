package cy.entities.project;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryLogTitle {

    String title();

    boolean isMultipleFiles() default false;
    boolean isTagFields() default false;

    boolean ignore() default false;
}
