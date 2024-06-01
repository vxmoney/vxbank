use multiversx_sc_scenario::*;

fn world() -> ScenarioWorld {
    let mut blockchain = ScenarioWorld::new();
    // blockchain.set_current_dir_from_workspace("relative path to your workspace, if applicable");

    blockchain.register_contract("mxsc:output/vx-events.mxsc.json", vx_events::ContractBuilder);
    blockchain
}

#[test]
fn t_000_deploy_rs() {
    world().run("scenarios/t_000_deploy.scen.json");
}

#[test]
fn t_002_register_payment_rs() {
    world().run("scenarios/t_002_register_payment.scen.json");
}

#[test]
fn t_003_set_egld_percentage_rs() {
    world().run("scenarios/t_003_set_egld_percentage.scen.json");
}

#[test]
fn t_004_set_token_percentage_rs() {
    world().run("scenarios/t_004_set_token_percentage.scen.json");
}

#[test]
fn t_006_create_event_rs() {
    world().run("scenarios/t_006_create_event.scen.json");
}
