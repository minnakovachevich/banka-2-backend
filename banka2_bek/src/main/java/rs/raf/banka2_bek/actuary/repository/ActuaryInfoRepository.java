package rs.raf.banka2_bek.actuary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.banka2_bek.actuary.model.ActuaryInfo;
import rs.raf.banka2_bek.actuary.model.ActuaryType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActuaryInfoRepository extends JpaRepository<ActuaryInfo, Long> {

    Optional<ActuaryInfo> findByEmployeeId(Long employeeId);

    List<ActuaryInfo> findAllByActuaryType(ActuaryType actuaryType);

    // PG cast za null-safe parametre (vidi CLAUDE.md Runda 24.04).
    @Query("SELECT a FROM ActuaryInfo a WHERE a.actuaryType = :type " +
            "AND (cast(:email as string) IS NULL OR LOWER(a.employee.email) LIKE LOWER(CONCAT('%', cast(:email as string), '%'))) " +
            "AND (cast(:firstName as string) IS NULL OR LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', cast(:firstName as string), '%'))) " +
            "AND (cast(:lastName as string) IS NULL OR LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', cast(:lastName as string), '%'))) " +
            "AND (cast(:position as string) IS NULL OR LOWER(a.employee.position) LIKE LOWER(CONCAT('%', cast(:position as string), '%')))")
    List <ActuaryInfo> findByTypeAndFilters(
            @Param("type") ActuaryType type,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("position") String position
    );

    Optional<ActuaryInfo> findByEmployee_Email(String username);

    /**
     * Resetuje usedLimit na 0 za sve aktuare u jednom bulk UPDATE upitu.
     * Vraca broj azuriranih redova.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ActuaryInfo a SET a.usedLimit = 0")
    int resetAllUsedLimits();
}
