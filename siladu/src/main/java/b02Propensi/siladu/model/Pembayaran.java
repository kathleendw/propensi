package b02Propensi.siladu.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import b02Propensi.siladu.user.model.UserModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pembayaran")
public class Pembayaran {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(name = "total_bayar", nullable = false)
    private long totalBayar ;

    @NotNull
    @Column(name = "nama_pemilik", nullable = false)
    private String namaPemilik ;

    @NotNull
    @Column(name = "metode_pembayaran", nullable = false)
    private String metodePembayaran;


    @Column(name = "nomor_telepon", nullable = false)
    private String nomorTelepon;

    @NotNull
    @Column(name = "status_pembayaran", nullable = false)
    private String statusPembayaran;//MENUNGGU PEMBAYARAN KEMBALI, PERLU KONFIRMASI, SUKSES, GAGAL

    @Lob
    @Column(name = "bukti_pembayaran")
    @Basic(fetch = FetchType.LAZY)
    private byte[] buktiPembayaran;

    @NotNull
    @Column(name = "jenis_kegiatan", nullable = false)
    private String jenisKegiatan; // PREMIUM, EVENT, LAYANAN, TRAINING
    
    @NotNull
    @Column(name = "waktu_pembayaran", nullable = false)
    private Date waktuPembayaran ;

    private UUID idPesanan;
    
}

