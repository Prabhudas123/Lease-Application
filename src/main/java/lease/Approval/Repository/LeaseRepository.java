package lease.Approval.Repository;

import lease.Approval.Model.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {


    @Query("SELECT l FROM Lease l " +
            "WHERE (:partnerName IS NULL OR l.partnerName LIKE %:partnerName%) " +
            "AND (:assetType IS NULL OR l.assetType = :assetType) " +
            "AND (:status IS NULL OR l.status = :status) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR l.createdAt BETWEEN :startDate AND :endDate)")
    Page<Lease> searchLeases(
            @Param("partnerName") String partnerName,
            @Param("assetType") String assetType,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT l FROM Lease l WHERE l.leaseEndDate BETWEEN :start AND :end AND l.status = 'APPROVED'")
    List<Lease> findExpiringLeases(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}