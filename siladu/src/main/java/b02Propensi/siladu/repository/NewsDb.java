package b02Propensi.siladu.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import b02Propensi.siladu.model.News;

@Repository
public interface NewsDb extends JpaRepository<News, String>{
    
}
