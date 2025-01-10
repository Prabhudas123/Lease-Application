package lease.Approval.Service;

import lease.Approval.Exception.ApprovedException;
import lease.Approval.Exception.LeaseNotFoundException;
import lease.Approval.Model.Approval;
import lease.Approval.Model.Lease;
import lease.Approval.Repository.ApprovalRepository;
import lease.Approval.Repository.LeaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LeaseApprovalService {

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private ApprovalRepository approvalRepository;

    private static final Logger logger = LoggerFactory.getLogger(LeaseApprovalService.class);

    public Lease createLease(Lease lease) {
        logger.info("creating new lease request...!");
        lease.setStatus("PENDING");
        lease.setCreatedAt(LocalDateTime.now());
        logger.info("created new lease request...!");
        return leaseRepository.save(lease);
    }

    public String approveLease(Long leaseId, String approver, String comments) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new LeaseNotFoundException("Lease not found"));

        if (!lease.getStatus().equals("PENDING")) {
            return "Lease is already " + lease.getStatus();
        }

        lease.setStatus("APPROVED");
        leaseRepository.save(lease);

        Approval approval = new Approval();
        approval.setLeaseId(leaseId);
        approval.setApprovedBy(approver);
        approval.setStatus("APPROVED");
        approval.setComments(comments);
        approval.setTimestamp(LocalDateTime.now());

        approvalRepository.save(approval);

        return "Lease approved successfully!";
    }

    public String denyLease(Long leaseId, String approver, String comments) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new LeaseNotFoundException("Lease not found"));

        if (!lease.getStatus().equals("PENDING")) {
            return "Lease is already " + lease.getStatus();
        }

        lease.setStatus("DENIED");
        leaseRepository.save(lease);

        Approval approval = new Approval();
        approval.setLeaseId(leaseId);
        approval.setApprovedBy(approver);
        approval.setStatus("DENIED");
        approval.setComments(comments);
        approval.setTimestamp(LocalDateTime.now());

        approvalRepository.save(approval);

        return "Lease denied successfully!";
    }

    // First-level approval
    public Lease approveFirstLevel(String leaseId, String approver) {
        Lease lease = getLeaseById(leaseId); // Fetch lease by ID
        if (!"PENDING".equals(lease.getStatus())) {
            throw new ApprovedException("Lease must be in PENDING state for first-level approval.");
        }
        lease.setStatus("FIRST_LEVEL_APPROVED");
        lease.setApprovedBy(approver);
        lease.setApprovalLevel(1);
        saveLease(lease); // Persist changes


        Approval approval = new Approval();
        approval.setLeaseId(Long.valueOf(leaseId));
        approval.setApprovedBy(approver);
        approval.setStatus("APPROVED");
        approval.setComments("FIRST_LEVEL_APPROVED");
        approval.setTimestamp(LocalDateTime.now());
        approvalRepository.save(approval);

        return lease;
    }

    // Second-level approval
    public Lease approveSecondLevel(String leaseId, String approver) {
        Lease lease = getLeaseById(leaseId);
        if (!"FIRST_LEVEL_APPROVED".equals(lease.getStatus())) {
            throw new ApprovedException("Lease must be first-level approved for second-level approval.");
        }
        lease.setStatus("SECOND_LEVEL_APPROVED");
        lease.setApprovedBy(approver);
        lease.setApprovalLevel(2);
        saveLease(lease);

        Approval approval = new Approval();
        approval.setLeaseId(Long.valueOf(leaseId));
        approval.setApprovedBy(approver);
        approval.setStatus("APPROVED");
        approval.setComments("SECOND_LEVEL_APPROVED");
        approval.setTimestamp(LocalDateTime.now());
        approvalRepository.save(approval);

        return lease;
    }

    // Reject lease
    public Lease rejectLease(String leaseId, String approver) {
        Lease lease = getLeaseById(leaseId);
        if (!"PENDING".equals(lease.getStatus()) && !"FIRST_LEVEL_APPROVED".equals(lease.getStatus())) {
            throw new ApprovedException("Lease can only be rejected if it's PENDING or FIRST_LEVEL_APPROVED.");
        }
        lease.setStatus("REJECTED");
        lease.setApprovedBy(approver);
        lease.setApprovalLevel(0);
        saveLease(lease);
        return lease;
    }

    private Lease getLeaseById(String leaseId) {
        return leaseRepository.findById(Long.valueOf(leaseId))
                .orElseThrow(() -> new IllegalArgumentException("Lease not found"));
    }

    private void saveLease(Lease lease) {
        // Implementation to persist lease
        leaseRepository.save(lease);
    }

    public List<Lease> searchLeases(String partnerName, String assetType, String status, LocalDateTime startDate, LocalDateTime endDate,Integer pageNumber,
                                    Integer pageSize,
                                    String sortBy,
                                    String dir) {
        Sort sort = dir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        return leaseRepository.searchLeases(partnerName, assetType, status, startDate, endDate,pageable).getContent();
    }

}
