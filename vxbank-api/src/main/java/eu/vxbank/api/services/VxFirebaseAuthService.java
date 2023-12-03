package eu.vxbank.api.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import eu.vxbank.api.endpoints.user.dto.TokenInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class VxFirebaseAuthService {

    @Autowired
    JwtEncoder encoder;

    @PostConstruct
    public void initializeFirebaseApp() {
        FirebaseApp.initializeApp();
    }


    public TokenInfo validateFirebaseToken(String firebaseToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance()
                .verifyIdToken(firebaseToken);
        String email = decodedToken.getEmail();
        String uid = decodedToken.getUid();
        String message = String.format("email=%s, uid=%s", email, uid);
        System.out.println("DEBUG message: " + message);

        Instant now = Instant.now();
        long expiry = 36000L;
        Instant expiresAt = now.plusSeconds(expiry);


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(uid)
                .claim("email", email)
                .claim("uid",uid)
                .build();

        String vxToken = this.encoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();


        TokenInfo tokenInfo = TokenInfo.builder()
                .firebaseId(uid)
                .email(email)
                .expiresAt(expiresAt.toEpochMilli())
                .vxToken(vxToken)

                .build();

        return tokenInfo;
    }

}
