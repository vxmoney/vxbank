package eu.vxbank.api.testutils;

import vxbank.datastore.data.models.VxUser;

public class BuildUtils {

    public static VxUser buildVxUserEmailOnly(String email) {
        return VxUser.builder()
                .email(email)
                .build();
    }
}
