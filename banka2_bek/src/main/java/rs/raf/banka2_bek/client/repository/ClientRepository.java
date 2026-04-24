package rs.raf.banka2_bek.client.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.raf.banka2_bek.client.model.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);

    // PG cast za null-safe parametre (vidi CLAUDE.md Runda 24.04).
    @Query("SELECT c FROM Client c WHERE "
            + "(cast(:firstName as string) IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', cast(:firstName as string), '%'))) AND "
            + "(cast(:lastName as string) IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', cast(:lastName as string), '%'))) AND "
            + "(cast(:email as string) IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', cast(:email as string), '%')))")
    Page<Client> findByFilters(@Param("firstName") String firstName,
                               @Param("lastName") String lastName,
                               @Param("email") String email,
                               Pageable pageable);
}
