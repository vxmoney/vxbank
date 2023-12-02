package eu.vxbank.api.user;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;

public class TestUserFlow {

    @Test
    void deprecatedPaymentTest() throws Exception {

        String projectId = "my-project-id";

        // Initialize the Firebase Admin SDK
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance(projectId));


        System.out.println("End of test");
    }
}
