use multiversx_sc_scenario::*;

fn world() -> ScenarioWorld {
    ScenarioWorld::vm_go()
}

#[test]
fn t_000_deploy_go() {
    world().run("scenarios/t_000_deploy.scen.json");
}

#[test]
fn t_002_register_payment_go() {
    world().run("scenarios/t_002_register_payment.scen.json");
}

#[test]
fn t_003_set_egld_percentage_go() {
    world().run("scenarios/t_003_set_egld_percentage.scen.json");
}

#[test]
fn t_004_set_token_percentage_go() {
    world().run("scenarios/t_004_set_token_percentage.scen.json");
}

#[test]
fn t_005_get_token_percentage_go() {
    world().run("scenarios/t_005_get_token_percentage.scen.json");
}
