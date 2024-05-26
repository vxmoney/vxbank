# Makefile

# https://hub.docker.com/r/multiversx/chainsimulator
CHAIN_SIMULATOR_IMAGE_NAME=multiversx/chainsimulator
CHAIN_SIMULATOR_IMAGE_TAG=latest

IMAGE_NAME=chainsimulator_container
SIMULATOR_REPO_URL=https://github.com/multiversx/mx-chain-simulator-go
SIMULATOR_REPO_BRANCH=main
CONTAINER_NAME=chainsimulator_container
REPO_TEMP_DIR=$(HOME)/.temp_simulator_repo
PIP_ENV_DIR=$(HOME)/.simulatorPipEnv

open-example:
	cd ~/workspace/mx-simulator-repo && intellij-idea-community .

open-mx-chain-simulator-go:
	cd ~/workspace/mx-chain-simulator-go && intellij-idea-community .

setup-env:
	chmod -R u+w /home/bogdan/.simulatorPipEnv
	rm -rf ${REPO_TEMP_DIR}
	git clone --branch ${SIMULATOR_REPO_BRANCH} ${SIMULATOR_REPO_URL} ${REPO_TEMP_DIR}
	python3 -m venv ${PIP_ENV_DIR}
	. ${PIP_ENV_DIR}/bin/activate && pip install -r ${REPO_TEMP_DIR}/examples/requirements.txt

start: setup-env
	docker stop "${IMAGE_NAME}" || true
	docker rm "${IMAGE_NAME}" || true
	docker run -d --name "${IMAGE_NAME}" -p 8085:8085 ${CHAIN_SIMULATOR_IMAGE_NAME}:${CHAIN_SIMULATOR_IMAGE_TAG}
	./utils/wait-simulator-to-start.sh


stop:
	docker stop "${IMAGE_NAME}" || true
	docker stop "${IMAGE_NAME}" || true

remove:
	docker stop "${IMAGE_NAME}" || true
	docker rm "${IMAGE_NAME}" || true

build-and-deploy-vxevents: start
	sc-meta all build
	. ${PIP_ENV_DIR}/bin/activate && \
        pip install -r ${REPO_TEMP_DIR}/examples/requirements.txt && \
        python3 ./utils/issue-usdt.py --get-token-identifier

echo-token: build-and-deploy-vxevents
	. ${PIP_ENV_DIR}/bin/activate && \
        token_identifier_string=$$(cat ./utils/token_identifier.txt) && \
        echo "Using token identifier: $$token_identifier_string"
        # some other commands that use $$token_identifier_string