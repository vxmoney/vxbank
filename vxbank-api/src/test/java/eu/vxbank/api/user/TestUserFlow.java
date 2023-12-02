package eu.vxbank.api.user;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Test;

public class TestUserFlow {

    private static UserRecord createUserWithEmail(FirebaseAuth firebaseAuth, String email) throws
            FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        return firebaseAuth.createUser(request);
    }

    @Test
    void testOauth() throws Exception {

        String projectId = "vxbank-eu-dev";
        FirebaseApp.initializeApp();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String email = "user@example.com";

        UserRecord userRecord = createUserWithEmail(firebaseAuth, email);
        String customToken = generateCustomToken(firebaseAuth, userRecord.getUid());

        System.out.println("Custom Token: " + customToken);

        System.out.println("End of test");
    }

    private static String generateCustomToken(FirebaseAuth firebaseAuth, String uid) throws FirebaseAuthException {
        return firebaseAuth.createCustomToken(uid);
    }
}
