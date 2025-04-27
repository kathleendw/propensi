package b02Propensi.siladu.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import b02Propensi.siladu.model.Pesanan;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_model", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "premium", nullable = true)
    private Boolean premium;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(nullable = false)
    private Boolean isPassUpdated;

    @Column(name = "telephone", nullable = true)
    private String telephone;

    @Column(name = "umkm", nullable = true)
    private String umkm;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pesanan> listPesanan;

    @Column(name="last_sign_in", nullable = true)
    private Date lastSignIn;
    @Column(name = "tanggal_upgrade_premium", nullable = true)
    private LocalDate tanggalUpgradePremium; // Tanggal ketika pengguna menjadi anggota premium

}
