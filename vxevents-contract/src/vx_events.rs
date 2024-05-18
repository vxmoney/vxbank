#![no_std]

#[allow(unused_imports)]
use multiversx_sc::imports::*;

mod admin_tools;
mod vx_events_endpoints;

/// An empty contract. To be used as a template when starting a new contract from scratch.
#[multiversx_sc::contract]
pub trait VxEvents: admin_tools::AdminTools + vx_events_endpoints::EventsEndpoints {
    #[init]
    fn init(&self) {}

    #[upgrade]
    fn upgrade(&self) {}

    #[view(getTokenConfiguration)]
    #[storage_mapper("tokenConfiguration")]
    fn token_configuration(&self, identifier: EgldOrEsdtTokenIdentifier) -> SingleValueMapper<u64>;
}
