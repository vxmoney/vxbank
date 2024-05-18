multiversx_sc::imports!();

#[multiversx_sc::module]



pub trait AdminTools {
    #[only_owner]
    #[endpoint(setContractSettings)]
    fn set_contract_settings(&self) {}

    #[only_owner]
    #[endpoint(setEgldProcessingPercentage)]
    fn settings_set_token_processing_percentage(&self,  vx_processing_percentage: u64){
        
    }
}
