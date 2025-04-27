package b02Propensi.siladu.DTO.response;

import java.time.LocalDate;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadNewsResponseDTO {
    private String idNews;

    private String judul;

    private String deskripsiNews;

    private LocalDate tanggalDibuat;

    private  byte[] gambarNews;

    private boolean isDeleted = false;
}
