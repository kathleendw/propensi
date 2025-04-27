package b02Propensi.siladu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import java.util.List;


@Repository
public interface PaketLayananDb extends JpaRepository<PaketLayanan, UUID>{
    List<PaketLayanan> findByNamaPaketContainingIgnoreCase(String paketLayanan);
    List<PaketLayanan> findByLayanan(Layanan layanan);
}
