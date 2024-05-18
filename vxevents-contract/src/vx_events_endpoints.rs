multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait EventsEndpoints {
    #[payable("*")]
    #[endpoint(registerPayment)]
    fn register_payment(&self) {}
}
