package b02Propensi.siladu.DTO.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.*;


import b02Propensi.siladu.model.Layanan;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadPaketLayananResponseDTO {
    private UUID id;

    private String namaPaket;

    private String kategoriLayanan;

    private Long hargaPaket;

    private String deskripsi;

    private String manfaat;

    private int rating;

    private Layanan layanan;
}
