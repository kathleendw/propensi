package b02Propensi.siladu.DTO.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CreateEventRequestDTO {

    private String idEvent;
 
    private String namaEvent;

    private String deskripsiEvent;

    private long hargaEvent;

    private String namaPembicara;

    private String lokasiEvent;

    private LocalDate jadwalEvent;

    private String durasiEvent;

    private byte[] gambarEvent;

    private int rating;

    private String jenisEvent;

    private boolean isDeleted = Boolean.FALSE;
}
