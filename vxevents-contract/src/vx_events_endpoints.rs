use crate::vx_events_store;
multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait EventsEndpoints: vx_events_store::EventsStore {
    #[payable("*")]
    #[endpoint(registerPayment)]
    fn register_payment(&self) {
        let _payment = self.call_value().egld_or_single_esdt();
    }

    //#[endpoint(createEvent)]
    fn create_event_v1(&self, event_id: ManagedBuffer) {
        let _event_owner = self.blockchain().get_caller();
        sc_print!("debug line: Creating event with id: {}", event_id);
        self.store_event_owner_wallet(event_id)
            .set(_event_owner);
    }

    #[endpoint(createEvent)]
    fn create_event(&self, event_id: ManagedBuffer) {
        let _event_owner = self.blockchain().get_caller();
        let _last_id = self.get_last_id();
        sc_print!("debug line: last id = {}", _last_id);
        
    }
}
