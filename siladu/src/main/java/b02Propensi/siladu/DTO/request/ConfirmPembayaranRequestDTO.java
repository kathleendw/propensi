package b02Propensi.siladu.DTO.request;

import java.util.UUID;

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
public class ConfirmPembayaranRequestDTO {
    private UUID idPesanan;
    private UUID idPembayaran;
    private String status;
}
