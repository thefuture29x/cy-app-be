package cy.dtos.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllBugDto {
    private Long idObject;
    private String name;
    private String category;
    private List<AllBugDto> childDto;
    private int countBug;
}
