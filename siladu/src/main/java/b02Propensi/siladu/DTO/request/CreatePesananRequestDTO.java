package b02Propensi.siladu.DTO.request;

import java.util.*;

import b02Propensi.siladu.model.Event;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.model.Training;
import b02Propensi.siladu.user.model.UserModel;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

public class CreatePesananRequestDTO {
    @NotNull
    private Date waktuPesanan;

    @NotNull
    private String statusPesanan;

    @NotNull
    private Long totalHarga;

    private PaketLayanan paketLayanan;
    private Training training;
    private Event event;
    // private Date waktuKonfirmasi;

    @NotNull
    private String jenisPesanan;

   

  
    private UserModel user;
    private UUID idPembayaran;

}
