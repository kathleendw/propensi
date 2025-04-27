package b02Propensi.siladu.DTO.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddNewsRequestDTO {

    @NotNull
    private String judul;

    @NotNull
    private String deskripsiNews;

    @NotNull
    private LocalDate tanggalDibuat;

    private byte[] gambarNews;

    private boolean isDeleted = false;

    

}
