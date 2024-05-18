use multiversx_sc_scenario::*;

fn world() -> ScenarioWorld {
    ScenarioWorld::vm_go()
}

#[test]
fn t_000_deploy_go() {
    world().run("scenarios/t_000_deploy.scen.json");
}

#[test]
fn t_001_set_settings_go() {
    world().run("scenarios/t_001_set_settings.scen.json");
}

#[test]
fn t_002_register_payment_go() {
    world().run("scenarios/t_002_register_payment.scen.json");
}

#[test]
fn t_003_register_payment_with_token_go() {
    world().run("scenarios/t_003_register_payment_with_token.scen.json");
}

#[test]
fn t_004_register_payment_with_egld_go() {
    world().run("scenarios/t_004_register_payment_with_egld.scen.json");
}
