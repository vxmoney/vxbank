#![no_std]

#[allow(unused_imports)]
use multiversx_sc::imports::*;

mod vx_events_admin_endpoints;
mod vx_events_endpoints;
mod vx_events_store;
mod vx_events_data;

/// An empty contract. To be used as a template when starting a new contract from scratch.
#[multiversx_sc::contract]
pub trait VxEvents:
    vx_events_admin_endpoints::AdminTools
    + vx_events_endpoints::EventsEndpoints
    + vx_events_store::EventsStore
{
    #[init]
    fn init(&self) {}

    #[upgrade]
    fn upgrade(&self) {}
}
