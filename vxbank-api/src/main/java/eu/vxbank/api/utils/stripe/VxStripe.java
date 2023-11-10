package eu.vxbank.api.utils.stripe;


public class VxStripe {
    private String publicKey;
    private String secretKey;

    private boolean initialized = false;


    public boolean  init(String publicKey, String secretKey){

        this.publicKey = publicKey;
        this.secretKey = secretKey;
        this.initialized = true;
        return true;
    }

    public void checkInit(){
        if (publicKey == null){
            throw new IllegalStateException("publicKey == null");
        }
        if (secretKey == null){
            throw new IllegalStateException("secretKey == null");
        }
        if (!initialized){
            throw new IllegalStateException("initialize == false");
        }
    }
}
