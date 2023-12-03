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

public class TestUserFlow {

    private static UserRecord createUserWithEmail(FirebaseAuth firebaseAuth, String email) throws
            FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        return firebaseAuth.createUser(request);
    }

    @Test
    void testOauthValidationTools() throws Exception {

        try {
            FirebaseApp.initializeApp();
        }catch (Exception e){
            // no need to initialize again
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        long timeStamp = (new Date()).getTime();
        String email = String.format("user_%s@example.com", timeStamp);

        System.out.println("EMIL = " + email);
        UserRecord userRecord = createUserWithEmail(firebaseAuth, email);
        String userId = userRecord.getUid();
        System.out.println("userId=" + userId);

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = OauthUtils.swapCustomTokenForIdToken(customToken);

        System.out.println("idToken Token: " + idToken);

        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        Assertions.assertEquals(userRecord.getUid(), decodedToken.getUid());
    }

}
