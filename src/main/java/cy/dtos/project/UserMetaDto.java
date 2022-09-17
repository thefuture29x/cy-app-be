package cy.dtos.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserMetaDto {

    private Long id;
    private String userName;
    private String fullName;

    public static UserMetaDto toDto(UserEntity userEntity) {
        if (userEntity == null) return null;
        return UserMetaDto.builder()
                .id(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .fullName(userEntity.getFullName())
                .build();

    }
}
