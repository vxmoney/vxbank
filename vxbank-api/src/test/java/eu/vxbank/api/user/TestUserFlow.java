package eu.vxbank.api.user;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
}
