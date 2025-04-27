package b02Propensi.siladu.DTO.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePaketLayananRequestDTO extends CreatePaketLayananRequestDTO{
    private UUID id;
}
