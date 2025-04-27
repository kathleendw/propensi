package b02Propensi.siladu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
import b02Propensi.siladu.model.Layanan;
@Repository
public interface LayananDb extends JpaRepository<Layanan, Long>{
    List<Layanan> findAll();
    Layanan findByIdLayanan(Long idLayanan);
}
