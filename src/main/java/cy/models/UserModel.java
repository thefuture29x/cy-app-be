package cy.models;

import cy.entities.UserEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    @ApiModelProperty(notes = "User ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    @Email
    @NotNull
    private String email;

    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    @NotBlank
    @NotNull
    private String sex;

    @ApiModelProperty(notes = "User full name", dataType = "String", example = "Nguyen Van A")
    private String fullName;

    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    private String password;

    @ApiModelProperty(notes = "User birthdate", dataType = "Date", example = "2002-04-29")
    private Date birthDate;

    @ApiModelProperty(notes = "User roles", dataType = "array", example = "1,3,5")
    private List<Long> roles;

    @ApiModelProperty(notes = "receiver's phone, phone must format follow vietnam", dataType = "String", example = "0952888888")
    @NotNull
    @NotBlank
    @Pattern(
            regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",
            message = "Phone number must be in format: 84xxxxxxxx"
    )
    private String phone;

    public static UserEntity toEntity(UserModel model) {
        if (model == null) throw new RuntimeException("UserModel is null");
        return UserEntity.builder()
                .fullName(model.getFullName())
                .email(model.getEmail())
                .password(model.getPassword())
                .birthDate(model.getBirthDate())
                .phone(model.getPhone())
                .sex(model.getSex())
                .userId(model.getId()).build();
    }
}
