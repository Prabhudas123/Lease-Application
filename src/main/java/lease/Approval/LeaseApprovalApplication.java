package lease.Approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class LeaseApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaseApprovalApplication.class, args);
    }

}
