package lease.Approval.Service;

import lease.Approval.Exception.ApprovedException;
import lease.Approval.Exception.LeaseNotFoundException;
import lease.Approval.Model.Approval;
import lease.Approval.Model.Lease;
import lease.Approval.Repository.ApprovalRepository;
import lease.Approval.Repository.LeaseRepository;
import lease.Approval.auth.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lease.Approval.Utils.AppConstants.FIRST_LEVEL_APPROVAL;
import static lease.Approval.Utils.AppConstants.SECOND_LEVEL_APPROVAL;

@Service
@Slf4j
public class LeaseApprovalService {

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;


    private static final Logger logger = LoggerFactory.getLogger(LeaseApprovalService.class);

    public Lease createLease(Lease lease) throws ExecutionException, InterruptedException {
        logger.info("Checking Credit Score from Another Microservice");
        //  String response = getCreditReport();
        //  System.out.println("RESPONSE : " + response + " *********");
        logger.info("creating new lease request...!");
        lease.setStatus("PENDING");
        lease.setCreatedAt(LocalDateTime.now());
        logger.info("created new lease request...and sent for first level approval..!");
        sendApprovalEmail(Long.valueOf(lease.getId()), FIRST_LEVEL_APPROVAL);
        return leaseRepository.save(lease);
    }


    public String getCreditReport() {
        String url = "http://localhost:8081/1/creditReport";  // Your service URL
        return restTemplate.getForObject(url, String.class);
    }

    // Fallback method (same signature as main method)
    public String fallbackCreditReport(Exception ex) {
        // Log the fallback invocation
        System.out.println("Fallback invoked: Service is unavailable.");
        return "Fallback: Service is currently unavailable. Reason: " + ex.getMessage();
    }

    public Lease renewLease(Long id, String approver) {
        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        // Check if the lease can be renewed (check if it's not already renewed)
        if ("RENEWED".equals(lease.getStatus())) {
            throw new RuntimeException("Lease is already renewed.");
        }

        // Update lease details for renewal
        lease.setStatus("RENEWED");
        lease.setRenewedBy(approver);
        lease.setRenewalTimestamp(LocalDateTime.now());
        lease.setRenewalCount(lease.getRenewalCount() + 1);

        // Extend the lease end date (example: add 1 year)
        lease.setRenewalEndDate(lease.getLeaseEndDate().plusYears(1));

        // Save the renewed lease
        leaseRepository.save(lease);

        // Initiate first-level approval
        Approval approval = new Approval();
        approval.setLeaseId(lease.getId());
        approval.setApprovedBy(approver);
        approval.setStatus("PENDING");
        approval.setComments("Lease renewal awaiting approval.");
        approval.setTimestamp(LocalDateTime.now());
        approval.setIsRenewal(true);
        approvalRepository.save(approval);

        return lease;
    }


   /* public String approveLease(Long leaseId, String approver, String comments) {
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
    */

    // First-level approval
    public Lease approveFirstLevel(String leaseId, String approver) {

        //calling credit score api
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

        // sending email to second level for approval
        sendApprovalEmail(Long.valueOf(leaseId), SECOND_LEVEL_APPROVAL);

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
        return lease;
    }

    public Approval approveLease(Long leaseId, String approver, String approvalLevel) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        // Find the pending approval for the lease
        Approval approval = approvalRepository.findTopByLeaseIdAndStatusAndIsRenewal(leaseId, "PENDING", true)
                .orElseThrow(() -> new RuntimeException("No pending renewal approval found"));

        if ("FIRST_LEVEL".equals(approvalLevel) && "PENDING".equals(approval.getStatus())) {
            approval.setStatus("APPROVED");
            approval.setApprovedBy(approver);
            approval.setTimestamp(LocalDateTime.now());

            // Save first-level approval
            approvalRepository.save(approval);

            // Check if the second-level approval is required
            if (lease.getApprovalLevel() == 1) {
                lease.setStatus("APPROVED");
                leaseRepository.save(lease);
            }

            return approval;
        }

        if ("SECOND_LEVEL".equals(approvalLevel) && "APPROVED".equals(approval.getStatus())) {
            approval.setStatus("APPROVED");
            approval.setApprovedBy(approver);
            approval.setTimestamp(LocalDateTime.now());

            // Save second-level approval
            approvalRepository.save(approval);

            // Update the lease to final approved status
            lease.setStatus("APPROVED");
            leaseRepository.save(lease);

            return approval;
        }

        throw new RuntimeException("Invalid approval sequence or status.");
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

    public List<Lease> searchLeases(String partnerName, String assetType, String status, LocalDateTime startDate, LocalDateTime endDate, Integer pageNumber,
                                    Integer pageSize,
                                    String sortBy,
                                    String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return leaseRepository.searchLeases(partnerName, assetType, status, startDate, endDate, pageable).getContent();
    }

    public void sendApprovalEmail(Long id, String level) {
        if (FIRST_LEVEL_APPROVAL.equals(level)) {
            List<String> listOfManagersEmail = userRepository.findAll().stream()
                    .filter(e -> e.getRole().equals("MANAGER"))
                    .map(e -> e.getEmail())
                    .collect(Collectors.toList());
            for (String email : listOfManagersEmail) {
                emailService.sendEmail(email, "Please give the first level approval...!");
            }
        } else {
            List<String> listOfManagersEmail = userRepository.findAll().stream()
                    .filter(e -> e.getRole().equals("ADMIN"))
                    .map(e -> e.getEmail())
                    .collect(Collectors.toList());
            for (String email : listOfManagersEmail) {
                emailService.sendEmail(email, "Please give the second level approval...!");
            }
        }

    }

}
