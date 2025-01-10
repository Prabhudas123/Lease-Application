package lease.Approval.Service;

import lease.Approval.Model.Lease;
import lease.Approval.Repository.LeaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaseAlertService {

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private NotificationService notificationService;

    public void checkAndSendAlerts() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime alertThreshold = now.plusDays(7); // Alert for leases expiring in 7 days

        // Fetch leases expiring soon
        List<Lease> expiringLeases = leaseRepository.findExpiringLeases(now, alertThreshold);

        // Send notifications for each expiring lease
        for (Lease lease : expiringLeases) {
            String message = String.format(
                    "Alert: Lease for partner '%s' (Asset: %s) is expiring on %s. Please take necessary action.",
                    lease.getPartnerName(), lease.getAssetType(), lease.getLeaseEndDate()
            );
            notificationService.sendNotification(lease.getPartnerName(), message);
        }
    }
}
