package cy.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.AuthenticationException;

import java.lang.Exception;


@Data
public class CustomHandleException extends AuthenticationException {
    private int code = 0;
    private String mess;

    public CustomHandleException(int code) {
        super(null);
        this.code = code;
    }

    public CustomHandleException(int code,String mess) {
        super(null);
        this.code = code;
        this.mess= mess;
    }
}
