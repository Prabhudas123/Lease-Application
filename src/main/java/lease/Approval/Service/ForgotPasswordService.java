package lease.Approval.Service;

import lease.Approval.Model.PasswordResetToken;
import lease.Approval.Repository.PasswordResetTokenRepository;
import lease.Approval.auth.user.User;
import lease.Approval.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public void sendResetLink(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setEmail(email);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
            tokenRepository.save(resetToken);

            String resetLink = "http://localhost:8080/leases/reset-password?token=" + token;
            emailService.sendEmail(email,resetLink);
        }
    }


    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);

        if (resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            Optional<User> user = userRepository.findByEmail(resetToken.get().getEmail());

            if (user.isPresent()) {
                User existingUser = user.get();
                existingUser.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                userRepository.save(existingUser);
            }
        } else {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}
