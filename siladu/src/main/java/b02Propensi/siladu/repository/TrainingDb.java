package b02Propensi.siladu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
import b02Propensi.siladu.model.Training;
@Repository
public interface TrainingDb extends JpaRepository<Training, UUID>{
    List<Training> findByJudulTrainingContainingIgnoreCase(String judulTraining);
}
