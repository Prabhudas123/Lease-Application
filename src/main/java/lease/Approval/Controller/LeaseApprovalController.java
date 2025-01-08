package lease.Approval.Controller;

import lease.Approval.Model.Lease;
import lease.Approval.Service.LeaseApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leases")
public class LeaseApprovalController {

    @Autowired
    private LeaseApprovalService leaseApprovalService;

    @PostMapping("/create")
    public Lease createLease(@RequestBody Lease lease) {
        return leaseApprovalService.createLease(lease);
    }

    @PostMapping("/{id}/approve")
    public String approveLease(@PathVariable Long id, @RequestParam String approver, @RequestParam String comments) {
        return leaseApprovalService.approveLease(id, approver, comments);
    }

    @PostMapping("/{id}/deny")
    public String denyLease(@PathVariable Long id, @RequestParam String approver, @RequestParam String comments) {
        return leaseApprovalService.denyLease(id, approver, comments);
    }

    @PostMapping("/{leaseId}/approve/first-level")
    public ResponseEntity<Lease> approveFirstLevel(@PathVariable String leaseId, @RequestParam String approver) {
        Lease lease = leaseApprovalService.approveFirstLevel(leaseId, approver);
        return ResponseEntity.ok(lease);
    }

    @PostMapping("/{leaseId}/approve/second-level")
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
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Lease> leases = leaseApprovalService.searchLeases(partnerName, assetType, status, startDate, endDate);
        return ResponseEntity.ok(leases);
    }

}
