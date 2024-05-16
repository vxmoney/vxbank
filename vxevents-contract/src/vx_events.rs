#![no_std]

#[allow(unused_imports)]
use multiversx_sc::imports::*;

mod admin_tools;

/// An empty contract. To be used as a template when starting a new contract from scratch.
#[multiversx_sc::contract]
pub trait VxEvents: admin_tools::AdminTools {
    #[init]
    fn init(&self) {}

    #[upgrade]
    fn upgrade(&self) {}
}
