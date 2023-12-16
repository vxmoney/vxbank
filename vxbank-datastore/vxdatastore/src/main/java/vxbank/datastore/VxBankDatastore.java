package vxbank.datastore;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import vxbank.datastore.data.models.*;
import vxbank.datastore.exceptions.VxBankDatastoreException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class VxBankDatastore {

    public static VxBankDatastore init(String projectId,
                                       ConnectionType connectionType,
                                       Optional<InputStream> optionalCredentialsInputStream) {
        VxBankDatastore vxBankDatastore = new VxBankDatastore();
        vxBankDatastore.initObjectify(projectId, connectionType, optionalCredentialsInputStream);
        return vxBankDatastore;
    }

    public enum ConnectionType {
        localhost, connectedToAppEngine
    }

    private String datastoreProjectId;
    private ConnectionType connectionType;

    public Objectify ofy;
    private Boolean initialized = false;

    private void initObjectify(String projectId,
                               ConnectionType connectionType,
                               Optional<InputStream> optionalCredentialsInputStream) throws VxBankDatastoreException {

        this.connectionType = connectionType;
        this.datastoreProjectId = projectId;
        ObjectifyFactory factory = createObjectifyFactory(connectionType, projectId, optionalCredentialsInputStream);
        registerDatastoreEntities(factory);
        this.ofy = factory.begin();
        initialized = true;
    }

    private ObjectifyFactory createObjectifyFactory(ConnectionType connectionType,
                                                    String projectId,
                                                    Optional<InputStream> optionalCredentialsInputStream) throws
            VxBankDatastoreException {
        if (connectionType == ConnectionType.localhost) {
            return initializeLocalhostFactory();
        } else {
            return initializeAppEngineFactory(projectId, optionalCredentialsInputStream.get());
        }
    }

    private ObjectifyFactory initializeAppEngineFactory(String projectId, InputStream credentialsInputStream) throws
            VxBankDatastoreException {
        try {
            Credentials defaultCredentials = GoogleCredentials.fromStream(credentialsInputStream);
            DatastoreOptions.Builder datastoreOptionsBuilder = DatastoreOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(defaultCredentials);
            ObjectifyFactory objectifyFactory = new ObjectifyFactory(datastoreOptionsBuilder.build()
                    .getService());
            return objectifyFactory;
        } catch (IOException e) {
            throw new VxBankDatastoreException("Not able to initializeAppEngineFactory");
        }
    }

    private ObjectifyFactory initializeLocalhostFactory() {
        DatastoreOptions localOptions = DatastoreOptions.newBuilder()
                .setHost("http://localhost:8081")
                .setProjectId(datastoreProjectId)
                .build();
        Datastore localDatastore = localOptions.getService();

        // Create an ObjectifyFactory with the local datastore
        ObjectifyFactory factoryLocal = new ObjectifyFactory(localDatastore);

        return factoryLocal;
    }


    void registerDatastoreEntities(ObjectifyFactory factory) {
        factory.register(ExampleModel.class);
        factory.register(VxPayment.class);

        factory.register(VxUser.class);
        factory.register(VxStripeConfig.class);
    }

    public Boolean getInitialized() {
        return initialized;
    }
}
