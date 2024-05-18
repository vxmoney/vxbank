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
fn t_001_set_settings_rs() {
    world().run("scenarios/t_001_set_settings.scen.json");
}

#[test]
fn t_002_register_payment_rs() {
    world().run("scenarios/t_002_register_payment.scen.json");
}

#[test]
fn t_003_register_payment_with_token_rs() {
    world().run("scenarios/t_003_register_payment_with_token.scen.json");
}
