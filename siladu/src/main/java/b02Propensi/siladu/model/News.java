package b02Propensi.siladu.model;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {
    @Id
    private String idNews;

    @NotNull
    @Column(name = "judul")
    private String judul;

    @NotNull
    @Column(name = "deskripsiNews", columnDefinition = "TEXT")
    private String deskripsiNews;

    @NotNull
    @Column(name = "tanggalDibuat")
    private LocalDate tanggalDibuat;

    @Lob
    @Column(name = "gambar News")
    @Basic(fetch = FetchType.LAZY)
    private byte[] gambarNews;


}
