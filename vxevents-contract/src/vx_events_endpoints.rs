multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait EventsEndpoints {
    #[payable("*")]
    #[endpoint(registerPayment)]
    fn register_payment(&self) {
        let _payment = self.call_value().egld_or_single_esdt();
    }

    #[endpoint(createEvent)]
    fn create_event(&self, event_id: ManagedBuffer) {
        let _event_owner = self.blockchain().get_caller();
        sc_print!("Creating event with id: {}", event_id);
    }
}
