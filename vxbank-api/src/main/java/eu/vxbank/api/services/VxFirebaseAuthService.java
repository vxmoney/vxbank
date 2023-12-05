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
import java.util.Optional;

@Service
public class VxFirebaseAuthService {

    @Autowired
    JwtEncoder encoder;

    @PostConstruct
    public void initializeFirebaseApp() {
        try {
            FirebaseApp.initializeApp();
        } catch (IllegalStateException e) {
            System.out.println("Default app has already bean initialized " + e.getMessage());
        }
    }


    public TokenInfo validateFirebaseToken(String firebaseToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance()
                .verifyIdToken(firebaseToken);
        String email = decodedToken.getEmail();
        String uid = decodedToken.getUid();
        String message = String.format("email=%s, uid=%s", email, uid);


        Instant now = Instant.now();
        long expiry = 36000L;
        Instant expiresAt = now.plusSeconds(expiry);


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(uid)
                .claim("email", email)
                .claim("uid", uid)
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

    public String validateFirebaseIdTokenAndGetEmail(String firebaseIdToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance()
                .verifyIdToken(firebaseIdToken);
        String email = decodedToken.getEmail();
        String uid = decodedToken.getUid();

        return email;
    }

    public TokenInfo buildTokenForUser(Long userId, String email, Optional<Long> optionalExpirySeconds){

        // default expiration is 2 hours
        Long expirySeconds = 60L * 60L * 2; // 60 seconds * 60 minutes * 2 hours
        if (optionalExpirySeconds.isPresent()){
            expirySeconds = optionalExpirySeconds.get();
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirySeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("uid",userId)
                .build();

        String vxToken = this.encoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        TokenInfo tokenInfo = TokenInfo.builder()
                .vxUserId(userId)
                .email(email)
                .expiresAt(expiresAt.toEpochMilli())
                .vxToken(vxToken)
                .build();

        return tokenInfo;
    }

}
