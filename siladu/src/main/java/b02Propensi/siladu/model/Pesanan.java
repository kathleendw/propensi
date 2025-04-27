package b02Propensi.siladu.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import java.util.UUID;

import b02Propensi.siladu.user.model.UserModel;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pesanan")
public class Pesanan{
    @Id
    private UUID idPesanan = UUID.randomUUID();

    @NotNull
    @Column(name = "waktu_pesanan", nullable = false)
    private Date waktuPesanan;

    @Column(name = "waktu_konfirmasi", nullable = true)
    private Date waktuKonfirmasi;

    @NotNull
    @Column(name = "status_pesanan", nullable = false)
    private String statusPesanan;// BELUM BAYAR, DIPROSES, STATUS PEMBAYARAN

    @NotNull
    @Column(name = "jenis_pesanan", nullable = false)
    private String jenisPesanan;//"LAYANAN", "TRAINING", "EVENT", "PREMIUM"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_layanan", referencedColumnName = "id")
    private PaketLayanan paketLayanan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_training", referencedColumnName = "idTraining")
    private Training training;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_event", referencedColumnName = "idEvent")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private UserModel user;

   
    private UUID idPembayaran;

    @Column(name = "is_Rated", nullable = false)
    private boolean isRated = false;

    @Column(name = "rating")
    private Integer Rating;
    
    @NotNull
    @Column(name = "harga_pesanan", nullable = false)
    private Long hargaPesanan;

}
