import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ApplicationTest {
    @Test
    @Ignore
    public void encodePassword() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        System.out.println("Encoded: " + encoder.encode("spring"));
    }

}
