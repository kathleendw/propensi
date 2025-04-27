package b02Propensi.siladu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import b02Propensi.siladu.model.Pembayaran;

import java.util.*;



@Repository
public interface PembayaranDb extends JpaRepository<Pembayaran, UUID>{
    @Query("SELECT SUM(p.totalBayar) FROM Pembayaran p WHERE p.jenisKegiatan = 'PREMIUM' AND p.statusPembayaran = 'SUKSES'")
    Long sumTotalBayarForPremiumSuccess();
}