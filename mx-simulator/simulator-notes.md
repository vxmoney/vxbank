# simulator notes
## simulator
simulator adder: https://github.com/multiversx/mx-chain-simulator-go/blob/main/examples/contracts/adder/adder.py
github yaml: https://github.com/multiversx/mx-chain-simulator-go/blob/main/.github/workflows/run-examples.yml
github actions: https://github.com/multiversx/mx-chain-simulator-go/blob/main/Makefile

```bash
git clone git@github.com:multiversx/mx-chain-simulator-go.git
cd mx-chain-simulator-go

docker stop simulator_image
docker rm simulator_image

# python env part
python3 -m venv testEnv
source testEnv/bin/activate
pip install -r examples/requirements.txtpi


make docker-build
make run-examples

# again python env part
deactivate
```


## my make related commands
```bash
make setup-env
make run-repo-examples

make setup-env
make run-adder-example
```
