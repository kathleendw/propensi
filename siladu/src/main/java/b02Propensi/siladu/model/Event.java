package b02Propensi.siladu.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.github.javafaker.DateAndTime;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event {
    @Id
    private String idEvent;

    // @Size(max = 100)
    @Column(name = "nama_event",  nullable = false)
    private String namaEvent;

    @Column(name = "deskripsi_event",  nullable = false,  columnDefinition = "TEXT")
    private String deskripsiEvent;

    @Column(name = "harga_event",  nullable = false)
    private long hargaEvent;

    @Column(name = "nama_pembicara",  nullable = false)
    private String namaPembicara;

    @Column(name = "lokasi_event",  nullable = false, columnDefinition = "TEXT")
    private String lokasiEvent;

    @Column(name = "jadwal_event",  nullable = false)
    @FutureOrPresent(message = "Tanggal tidak valid")
    private LocalDate jadwalEvent;

    @Column(name = "durasi_event",  nullable = false)
    private String durasiEvent;

    @Column(name = "rating")
    private double rating;

    @Column(name = "jumlah_rating")
    private int jumlahRating = 0;

    @Column(name = "jenis_event")
    private String jenisEvent;

    @Column(name = "gambar_Event") 
    @Basic(fetch = FetchType.LAZY)
    private byte[] gambarEvent;
    
    
    @Column(name = "is_deleted")
    private boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pesanan> listPesanan;


    
}
