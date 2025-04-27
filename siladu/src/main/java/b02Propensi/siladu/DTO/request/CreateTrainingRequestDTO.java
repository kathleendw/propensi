package b02Propensi.siladu.DTO.request;
import java.util.UUID;

import b02Propensi.siladu.model.Training;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTrainingRequestDTO {

    @NotNull
    private String judulTraining;

    @NotNull
    private String pengajarTraining;

    @NotNull
    private String deskripsiTraining;

    @NotNull
    private Long hargaTraining;

    @NotNull
    private String videoTraining;

    @NotNull
    private int ratingTraining;

}
