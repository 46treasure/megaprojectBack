package okten.megaproject.Configurations;



import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@PropertySource("classpath:application.properties")
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    Environment env;

    public void send(String email, User user){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try{
            mimeMessage.setFrom(new InternetAddress(env.getProperty("spring.mail.username")));
            mimeMessageHelper.setTo(email);
            String message = String.format("Hello %s! \n"  +
                            "Visit this link: http://localhost:4200/finishReg/%s or pick here ",
                    user.getUsername(),
                    user.getUserKey());
            mimeMessageHelper.setText(message, true);
            mimeMessageHelper.setSubject("Registration message");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
}
