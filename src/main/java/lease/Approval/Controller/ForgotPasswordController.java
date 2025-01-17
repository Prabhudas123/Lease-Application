package lease.Approval.Controller;


import lease.Approval.Service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leases")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        forgotPasswordService.sendResetLink(email);
        return "Password reset link sent to " + email;
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        forgotPasswordService.resetPassword(token, newPassword);
        return "Password has been reset successfully";
    }
}
