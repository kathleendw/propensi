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
@Table(name = "layanan")
public class Layanan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idLayanan;

    @NotNull
    @Column(name = "nama_layanan", nullable = false)
    private String namaLayanan;

    @NotNull
    @Column(name = "kategori_layanan", nullable = false)
    private String kategoriLayanan;

    @NotNull
    @Column(name = "deskripsi_singkat", nullable = false)
    private String deskripsiSingkat; 

    @OneToMany(mappedBy = "layanan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PaketLayanan> listPaketLayanan;
}
