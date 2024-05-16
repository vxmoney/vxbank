multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait AdminTools {
    #[only_owner]
    #[endpoint(setContractSettings)]
    fn set_contract_settings(&self) {}
}
