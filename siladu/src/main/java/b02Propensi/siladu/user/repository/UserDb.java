package b02Propensi.siladu.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import b02Propensi.siladu.user.model.Role;
import b02Propensi.siladu.user.model.UserModel;

import java.util.Date;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserDb extends JpaRepository<UserModel, Long>{
    Optional<UserModel> findByEmail(String email);    

    Optional<UserModel> findById(Long id);

    long countByLastSignInBetween(Date startDate, Date endDate);

    long countByRoleAndLastSignInBetween(Role role, Date startDate, Date endDate);

    @Query("SELECT COUNT(u) FROM UserModel u WHERE u.tanggalUpgradePremium BETWEEN :startDate AND :endDate")
    Long countNewPremiumMembersByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Long countByRole(Role role);
}
