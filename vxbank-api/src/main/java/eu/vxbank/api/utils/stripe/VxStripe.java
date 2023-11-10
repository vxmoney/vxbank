package eu.vxbank.api.utils.stripe;


public class VxStripe {
    private String publicKey = "pk_test_51O93vKB6aHGAQTGCGfDwFgWwX0oZdWg55cLxZTtCyyvDsH7DfxvKeR2bQQtNZOzZTFDbTI1IILr38UuQdnZplNZ500QeHsOY5r";
    private String secretKey = "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";

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
