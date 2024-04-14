package eu.vxbank.api.user;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.testutils.OauthUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class TokenTest {

    private static UserRecord createUserWithEmail(FirebaseAuth firebaseAuth, String email) throws
            FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        return firebaseAuth.createUser(request);
    }

    @Test
    void testOauthValidationTools() throws Exception {

        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase has not been initialized yet, so initialize it
            FirebaseApp.initializeApp();
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        long timeStamp = (new Date()).getTime();
        String email = String.format("user_%s@example.com", timeStamp);

        UserRecord userRecord = createUserWithEmail(firebaseAuth, email);
        String userId = userRecord.getUid();

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = OauthUtils.swapCustomTokenForIdToken(customToken);


        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        Assertions.assertEquals(userRecord.getUid(), decodedToken.getUid());
    }


}
