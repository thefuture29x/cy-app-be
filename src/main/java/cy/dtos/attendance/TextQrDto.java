package cy.dtos.attendance;

import cy.dtos.UserDto;
import cy.dtos.project.FileDto;
import cy.entities.attendance.TextQrEntity;
import cy.entities.project.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextQrDto {
    private Long id;
    private String image;
    private String name;
    private String email;
    private String address;
    private String company;
    private String telephone;
    private String fax;
    private String content;
    private UserDto uploadedBy;

    public static TextQrDto toDto(TextQrEntity textQrEntity) {
        if (textQrEntity == null)
            return null;
        return TextQrDto.builder()
                .id(textQrEntity.getId())
                .image(textQrEntity.getImage())
                .name(textQrEntity.getName())
                .email(textQrEntity.getEmail())
                .address(textQrEntity.getAddress())
                .company(textQrEntity.getCompany())
                .telephone(textQrEntity.getTelephone())
                .fax(textQrEntity.getFax())
                .content(textQrEntity.getContent())
                .uploadedBy(UserDto.toDto(textQrEntity.getUploadedBy()))
                .build();
    }
}
