use crate::vx_events_store;

multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait AdminTools: vx_events_store::EventsStore {
    #[only_owner]
    #[endpoint(setContractSettings)]
    fn set_contract_settings(&self) {}

    #[only_owner]
    #[endpoint(setEgldProcessingPercentage)]
    fn settings_set_token_processing_percentage(&self, egld_percentage: u64) {
        let egld_id = EgldOrEsdtTokenIdentifier::egld();
        self.store_token_processing_percentage(egld_id)
            .set(egld_percentage)
    }
}
