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
@Table(name = "training")
public class Training {
    @Id
    private UUID idTraining = UUID.randomUUID();

    @NotNull
    @Column(name = "judul_training", nullable = false)
    private String judulTraining;

    @NotNull
    @Column(name = "pengajar_training", nullable = false)
    private String pengajarTraining;

    @NotNull
    @Column(name = "deskripsi_training", nullable = false)
    private String deskripsiTraining;

    @NotNull
    @Column(name = "harga_training", nullable = false)
    private Long hargaTraining;

    @NotNull
    @Column(name = "video_training", nullable = false)
    private String videoTraining;

    @NotNull
    @Column(name = "rating_training", nullable = false)
    private double ratingTraining;

    @Column(name = "jumlah_rating")
    private int jumlahRating = 0;

    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pesanan> listPesanan;

}
