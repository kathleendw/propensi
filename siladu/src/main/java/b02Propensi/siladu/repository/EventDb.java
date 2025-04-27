package b02Propensi.siladu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import b02Propensi.siladu.model.Event;

@Repository
public interface EventDb extends JpaRepository<Event, String>{
    
}
