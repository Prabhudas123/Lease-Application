package lease.Approval.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String from;

    public boolean sendMail(String to,
                            String cc[],
                            String bcc[],
                            String subject,
                            String text,
                            Resource files[]) {

        boolean sent = false;

        //1. Create Empty Email object

        MimeMessage message = sender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, files!=null && files.length>0);

            helper.setTo(to);

            if(cc!=null) {
                helper.setCc(cc);
            }
            if(bcc!=null) {
                helper.setBcc(bcc);
            }
            helper.setFrom(from);

            helper.setSubject(subject);
            //helper.setText(text);
            helper.setText(text,true);

            //filename, file data
            if(files!=null && files.length>0) {
                for(Resource file : files)
                    helper.addAttachment(file.getFilename(), file);
            }

            //3. Click on send Button
            sender.send(message);
            sent = true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return sent;
    }

}