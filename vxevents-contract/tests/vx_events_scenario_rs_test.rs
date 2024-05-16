use multiversx_sc_scenario::*;

fn world() -> ScenarioWorld {
    let mut blockchain = ScenarioWorld::new();
    // blockchain.set_current_dir_from_workspace("relative path to your workspace, if applicable");

    blockchain.register_contract("mxsc:output/vx-events.mxsc.json", vx_events::ContractBuilder);
    blockchain
}

#[test]
fn empty_rs() {
    world().run("scenarios/vx_events.scen.json");
}
