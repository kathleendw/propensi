package b02Propensi.siladu.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditNewsRequestDTO extends AddNewsRequestDTO {
    private String idNews;
}
