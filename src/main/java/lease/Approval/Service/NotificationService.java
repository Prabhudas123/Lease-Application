package lease.Approval.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    EmailService emailService;
    public void sendNotification(String recipient, String message) throws Exception {
        // For now, just log the notification
        UrlResource file2 = new UrlResource("https://img.jagranjosh.com/imported/images/E/GK/sachin-records.png");
        emailService.sendMail("krishna13210@gmail.com",
                new String[] {
                        "prabhuece025@gmail.com",
                        "prabhudas1531@gmail.com"
                },
                new String[] {
                        "prabhuece025@gmail.com"
                }, "OFFER LETTER - INFOSYS", "CONGRATULATIONS, YOU HAVE SELECTED",
                new Resource[] {
                        file2
                });
        System.out.println("Sending notification to: " + recipient);
        System.out.println("Message: " + message);
    }
}
