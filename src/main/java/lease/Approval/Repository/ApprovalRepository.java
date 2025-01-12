package lease.Approval.Repository;

import lease.Approval.Model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovalRepository  extends JpaRepository<Approval, Long> {
    Optional<Approval> findTopByLeaseIdAndStatusAndIsRenewal(Long leaseId, String status, Boolean isRenewal);
}
