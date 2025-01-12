package lease.Approval.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
public class Lease {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public Double getLeaseAmount() {
        return leaseAmount;
    }

    public void setLeaseAmount(Double leaseAmount) {
        this.leaseAmount = leaseAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public int getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(int approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partnerName;
    private String assetType;
    private Double leaseAmount;
    private String status; // PENDING, APPROVED, DENIED

    private LocalDateTime createdAt;

    private String renewedBy;  // User who approved the renewal
    private LocalDateTime renewalTimestamp;  // Timestamp when the renewal was approved
    private LocalDateTime renewalEndDate;  // New lease end date after renewal (optional)
    private int renewalCount;

    public String getRenewedBy() {
        return renewedBy;
    }

    public void setRenewedBy(String renewedBy) {
        this.renewedBy = renewedBy;
    }

    public LocalDateTime getRenewalTimestamp() {
        return renewalTimestamp;
    }

    public void setRenewalTimestamp(LocalDateTime renewalTimestamp) {
        this.renewalTimestamp = renewalTimestamp;
    }

    public LocalDateTime getRenewalEndDate() {
        return renewalEndDate;
    }

    public void setRenewalEndDate(LocalDateTime renewalEndDate) {
        this.renewalEndDate = renewalEndDate;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(int renewalCount) {
        this.renewalCount = renewalCount;
    }

    public LocalDateTime getLeaseEndDate() {
        return leaseEndDate;
    }

    public void setLeaseEndDate(LocalDateTime leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }

    private LocalDateTime leaseEndDate;
    private String approvedBy; // User who approved the lease
    private int approvalLevel; // 0 = none, 1 = first-level, 2 = second-level

    public Lease() {
    }

    public Lease(Long id, String partnerName, String assetType, Double leaseAmount, String status, LocalDateTime createdAt, String renewedBy, LocalDateTime renewalTimestamp, LocalDateTime renewalEndDate, int renewalCount, LocalDateTime leaseEndDate, String approvedBy, int approvalLevel) {
        this.id = id;
        this.partnerName = partnerName;
        this.assetType = assetType;
        this.leaseAmount = leaseAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.renewedBy = renewedBy;
        this.renewalTimestamp = renewalTimestamp;
        this.renewalEndDate = renewalEndDate;
        this.renewalCount = renewalCount;
        this.leaseEndDate = leaseEndDate;
        this.approvedBy = approvedBy;
        this.approvalLevel = approvalLevel;
    }
}