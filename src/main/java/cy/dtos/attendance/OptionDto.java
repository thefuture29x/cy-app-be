package cy.dtos.attendance;

import cy.entities.attendance.OptionEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OptionDto {
    private Long id;
    private String optionKey;
    private String optionValue;

   public static OptionDto toDto(OptionEntity entity){
       if(entity == null) return null;
       return OptionDto.builder()
               .id(entity.getId())
               .optionKey(entity.getOptionKey())
               .optionValue(entity.getOptionValue())
               .build();
   }
}
