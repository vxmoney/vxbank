package vxbank.datastore.exceptions;

public class VxBankDatastoreException extends RuntimeException{
    public VxBankDatastoreException(String errorMessage){
        super(errorMessage);
    }
}
