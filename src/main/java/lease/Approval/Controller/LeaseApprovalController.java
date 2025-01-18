package lease.Approval.Controller;

import lease.Approval.Model.Lease;
import lease.Approval.Service.LeaseApprovalService;
import lease.Approval.Utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/leases")
@PreAuthorize("hasRole('ADMIN')")
public class LeaseApprovalController {

    @Autowired
    private LeaseApprovalService leaseApprovalService;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public Lease createLease(@RequestBody Lease lease) throws ExecutionException, InterruptedException {
        return leaseApprovalService.createLease(lease);
    }

    @PostMapping("/{id}/approve")
    public void approveLease(@PathVariable Long id, @RequestParam String levelOfApproval) {
        leaseApprovalService.sendApprovalEmail(id, levelOfApproval);
    }

//    @PostMapping("/{id}/deny")
//    public String denyLease(@PathVariable Long id, @RequestParam String approver, @RequestParam String comments) {
//        return leaseApprovalService.denyLease(id, approver, comments);
//    }


    @PostMapping("/{id}/renew")
    public ResponseEntity<Lease> renewLease(@PathVariable Long id, @RequestParam String approver) {
        Lease lease = leaseApprovalService.renewLease(id, approver);
        return ResponseEntity.ok(lease);
    }

    @PostMapping("/{leaseId}/approve/first-level")
    @PreAuthorize("hasAuthority('MANAGER_CREATE')")
    public ResponseEntity<Lease> approveFirstLevel(@PathVariable String leaseId, @RequestParam String approver) {
        Lease lease = leaseApprovalService.approveFirstLevel(leaseId, approver);
        return ResponseEntity.ok(lease);
    }

    @PostMapping("/{leaseId}/approve/second-level")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<Lease> approveSecondLevel(@PathVariable String leaseId, @RequestParam String approver) {
        Lease lease = leaseApprovalService.approveSecondLevel(leaseId, approver);
        return ResponseEntity.ok(lease);
    }

    @PostMapping("/{leaseId}/reject")
    public ResponseEntity<Lease> rejectLease(@PathVariable String leaseId, @RequestParam String approver) {
        Lease lease = leaseApprovalService.rejectLease(leaseId, approver);
        return ResponseEntity.ok(lease);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Lease>> searchLeases(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortDir,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Lease> leases = leaseApprovalService.searchLeases(partnerName, assetType, status, startDate, endDate, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(leases);
    }

}
