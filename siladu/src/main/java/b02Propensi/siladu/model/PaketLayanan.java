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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paket")
public class PaketLayanan {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(name = "nama_paket", nullable = false)
    private String namaPaket;

    @NotNull
    @Column(name = "kategori_layanan", nullable = false)
    private String kategoriLayanan;

    @NotNull
    @Column(name = "harga_paket", nullable = false)
    private Long hargaPaket;

    @NotNull
    @Column(name = "deskripsi", nullable = false)
    private String deskripsi;

    @NotNull
    @Column(name = "manfaat", nullable = false)
    private String manfaat;

    @NotNull
    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "jumlah_rating")
    private int jumlahRating = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_layanan", referencedColumnName = "idLayanan")
    private Layanan layanan;

    @OneToMany(mappedBy = "paketLayanan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pesanan> listPesanan;
}
