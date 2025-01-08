package lease.Approval.Repository;

import lease.Approval.Model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository  extends JpaRepository<Approval, Long> {
}
