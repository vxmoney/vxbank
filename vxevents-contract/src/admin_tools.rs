multiversx_sc::imports!();

#[multiversx_sc::module]

pub trait AdminTools:{

    fn set_token_settings(
        percentage: u64
    ){
        let new_value: u64 = percentage;
        sc_print!("percentage = {}", percentage);
        sc_print!("percentage = {}", percentage);
    }
}