package b02Propensi.siladu.DTO.request;

import java.util.Date;

import b02Propensi.siladu.model.Pesanan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class CreatePembayaranRequestDTO {

    @NotNull
    private long totalBayar ;

    @NotNull
    private String namaPemilik ;

    @NotNull
    private String metodePembayaran;

    @NotNull
    private String nomorTelepon;

    @NotNull
    private String statusPembayaran;

    private byte[] buktiPembayaran;

    @NotNull
    private String jenisKegiatan;
    
    @NotNull
    private Date waktuPembayaran ;

   
}
