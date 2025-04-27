package b02Propensi.siladu.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ReadTrainingResponseDTO {
    private UUID idTraining;
    private String judulTraining;
    private String pengajarTraining;
    private String deskripsiTraining;
    private Long hargaTraining;
    private String videoTraining;
    private int ratingTraining;
}
