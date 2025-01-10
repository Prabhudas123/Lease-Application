package lease.Approval.scheduler;

import lease.Approval.Service.LeaseAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaseAlertScheduler {

    @Autowired
    private LeaseAlertService leaseAlertService;

    @Scheduled(cron = "* * * * * *") // Every day at 9 AM
    public void scheduleLeaseAlertCheck() throws Exception {
       // System.out.println("=========TESTING SCHEDULER============");
        leaseAlertService.checkAndSendAlerts();
    }
}
