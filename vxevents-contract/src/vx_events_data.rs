multiversx_sc::imports!();
multiversx_sc::derive_imports!();

use multiversx_sc::{
    api::ManagedTypeApi,
    types::{ManagedAddress, ManagedBuffer,EgldOrEsdtTokenIdentifier},
};

#[derive(TypeAbi, TopEncode, TopDecode, NestedEncode, NestedDecode)]
pub struct Payment<M: ManagedTypeApi> {
    pub payment_id: ManagedBuffer<M>,
    pub event_id: ManagedBuffer<M>,
    pub payment_wallet: ManagedAddress<M>,
    pub payment_token: EgldOrEsdtTokenIdentifier<M>,
    pub payment_amount: BigUint<M>,
    pub payment_total_value: BigUint<M>,
    pub payment_event_owner_value: BigUint<M>,
    pub payment_event_fees: BigUint<M>,
}


