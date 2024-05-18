multiversx_sc::imports!();

#[multiversx_sc::module]


pub trait EventsStore {

    #[view(getTokenProcessingPercentage)]
    #[storage_mapper("tokenProcessingPercentage")]
    fn store_token_processing_percentage(&self, identifier: EgldOrEsdtTokenIdentifier) -> SingleValueMapper<u64>;
}