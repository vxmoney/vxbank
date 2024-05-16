multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait AdminTools:{

    #[only_owner]
    #[endpoint(setContractSettings)]
    fn set_contract_settings(
        percentage: u64
    ){
        let new_value: u64 = percentage;
        sc_print!("percentage = {}", percentage);
        sc_print!("percentage = {}", percentage);
    }
}