package b02Propensi.siladu.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

import b02Propensi.siladu.model.Pesanan;
import java.time.LocalDateTime;


@Repository
public interface PesananDb extends JpaRepository<Pesanan, UUID>{
    List<Pesanan> findByStatusPesananAndWaktuPesanan(String statusPesanan, Date waktuPesanan );
    List<Pesanan> findByJenisPesanan(String jenis);
}