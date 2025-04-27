package b02Propensi.siladu.DTO.request;
import java.util.*;

import b02Propensi.siladu.model.Layanan;
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
public class CreatePaketLayananRequestDTO {
    @NotNull
    private String namaPaket;

    @NotNull
    private String kategoriLayanan;

    @NotNull
    private Long hargaPaket;

    @NotNull
    private int rating;
    
    @NotNull
    private String deskripsi;

    @NotNull
    private String manfaat;


    private Layanan layanan;
}
