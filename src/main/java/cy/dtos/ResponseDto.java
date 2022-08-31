package cy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDto {
    private int code = 0;
    private Object data;
    private Object otherData;

    public static ResponseDto of(int code, Object data) {
        return ResponseDto.builder()
                .code(code)
                .data(data)
                .build();
    }

    public static ResponseDto of(Object data) {
        return ResponseDto.builder()
                .data(data)
                .build();
    }

    public static ResponseDto otherData(Object data,Object otherData) {
        return ResponseDto.builder()
                .data(data)
                .otherData(otherData)
                .build();
    }


    public static ResponseDto ofError(int code) {
        return ResponseDto.builder().code(code).build();
    }

}
