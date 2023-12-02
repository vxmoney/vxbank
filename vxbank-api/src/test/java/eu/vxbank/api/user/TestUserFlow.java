package eu.vxbank.api.user;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestUserFlow {

    private static UserRecord createUserWithEmail(FirebaseAuth firebaseAuth, String email) throws
            FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        return firebaseAuth.createUser(request);
    }

    @Test
    void testOauth() throws Exception {

        String projectId = "vxbank-eu-dev";
        FirebaseApp.initializeApp();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        long timeStamp = (new Date()).getTime();
        String email = String.format("user_%s@example.com", timeStamp);

        System.out.println("EMIL = " + email);
        UserRecord userRecord = createUserWithEmail(firebaseAuth, email);



        String customToken = generateCustomToken(firebaseAuth, userRecord.getUid());

        System.out.println("Custom Token: " + customToken);

        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(customToken);
        Assertions.assertEquals(userRecord.getUid(), decodedToken.getUid());

        System.out.println("End of test");
    }

    private static String generateCustomToken(FirebaseAuth firebaseAuth, String uid) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        //claims.put("aud", "vxbank-eu-dev");
        return firebaseAuth.createCustomToken(uid, claims);
    }

    private static String getIdTokenFromSessionCookie(FirebaseAuth auth, String customToken)
            throws FirebaseAuthException {
        // Create a session cookie from the custom token
        long expiresIn = TimeUnit.DAYS.toMillis(5);
        SessionCookieOptions options = SessionCookieOptions.builder()
                .setExpiresIn(expiresIn)
                .build();
        String sessionCookie = auth.createSessionCookie(customToken,options);

        // Verify the session cookie to obtain the ID token
        FirebaseToken decodedToken = auth.verifySessionCookie(sessionCookie, true);
        throw new IllegalStateException("Please implement this");
    }
}
