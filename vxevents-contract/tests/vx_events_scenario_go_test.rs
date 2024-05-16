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
