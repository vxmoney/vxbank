package vxbank.datastore.data.models;

import com.googlecode.objectify.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;

import java.util.Optional;
import java.util.UUID;

class VxUserTest {

    @Test
    void testCreateGet() {
        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        VxUser vxUser = new VxUser();
        Key<VxUser> key = ds.ofy.save()
                .entity(vxUser)
                .now();
        Assertions.assertNotNull(key);
        Assertions.assertNotNull(vxUser.id);
    }

    @Test
    void testGetById() {
        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);

        Key<VxUser> key = ds.ofy.save()
                .entity(vxUser)
                .now();
        Long userId = vxUser.id;

        Assertions.assertNotNull(userId);

        VxUser storedUser = ds.ofy.load()
                .type(VxUser.class)
                .id(userId)
                .now();
        Assertions.assertEquals(vxUser.email, storedUser.email);
    }

    @Test
    void testGetByEmail() {
        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);

        ds.ofy.save()
                .entity(vxUser)
                .now();

        Optional<VxUser> validUser = VxDsService.getUserByEmail(vxUser.email, ds);
        Assertions.assertTrue(validUser.isPresent());

        Optional<VxUser> invalidUser = VxDsService.getUserByEmail("fake-email", ds);
        Assertions.assertTrue(invalidUser.isEmpty());
    }


    @Test
    void testGetUserById() {
        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);

        ds.ofy.save()
                .entity(vxUser)
                .now();

        VxUser user = VxDsService.getById(vxUser.id, ds, VxUser.class);
        Assertions.assertNotNull(user);
    }

    @Test
    void testPersistViaVxService() {
        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);

        VxUser persistedUser = VxDsService.persist(vxUser, ds, VxUser.class);
        Assertions.assertNotNull(persistedUser);
        Assertions.assertNotNull(persistedUser.id);
        Assertions.assertNotNull(vxUser.id);
    }

}