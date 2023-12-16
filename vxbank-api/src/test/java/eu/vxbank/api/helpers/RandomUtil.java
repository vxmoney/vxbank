package eu.vxbank.api.helpers;

import java.util.UUID;

public class RandomUtil {
    public static String generateRandomEmail(){
        String randomString = UUID.randomUUID()
                .toString();
        return String.format("%s@mail.com", randomString);
    }
}
