package cy.models.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestAttendByNameAndYearMonth {
    String name;
    Date date;
}
