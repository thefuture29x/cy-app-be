package cy.models.attendance;

import cy.entities.attendance.TextQrEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextQrModel {
    @ApiModelProperty(notes = "ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Image", dataType = "MultipartFile")
    private MultipartFile image;
    @ApiModelProperty(notes = "Name", dataType = "String")
    private String name;
    @ApiModelProperty(notes = "Email", dataType = "String")
    private String email;
    @ApiModelProperty(notes = "Address", dataType = "String")
    private String address;
    @ApiModelProperty(notes = "Company", dataType = "String")
    private String company;
    @ApiModelProperty(notes = "Telephone", dataType = "String")
    private String telephone;
    @ApiModelProperty(notes = "Fax", dataType = "String")
    private String fax;
    @ApiModelProperty(notes = "Content", dataType = "String")
    private String Content;

    private Long uploadedBy;

    public static TextQrEntity toEntity(TextQrModel textQrModel){
        if (textQrModel == null) return null;
        return TextQrEntity.builder()
                .id(textQrModel.getId())
                .name(textQrModel.getName())
                .email(textQrModel.getEmail())
                .address(textQrModel.getAddress())
                .company(textQrModel.getCompany())
                .telephone(textQrModel.getTelephone())
                .fax(textQrModel.getFax())
                .content(textQrModel.getContent())
                .build();
    }
}
