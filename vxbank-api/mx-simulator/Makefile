# Makefile

CHAIN_SIMULATOR_IMAGE_NAME=chainsimulator
CHAIN_SIMULATOR_IMAGE_TAG=latest
DOCKER_FILE=Dockerfile
IMAGE_NAME=simulator_image
SIMULATOR_REPO_URL=https://github.com/multiversx/mx-chain-simulator-go
SIMULATOR_REPO_BRANCH=main
CONTAINER_NAME=chainsimulator_container
REPO_TEMP_DIR=$(HOME)/.temp_simulator_repo
PIP_ENV_DIR=$(HOME)/.simulatorPipEnv

docker-build:
	docker build \
		--build-arg SIMULATOR_REPO_URL=${SIMULATOR_REPO_URL} \
		--build-arg SIMULATOR_REPO_BRANCH=${SIMULATOR_REPO_BRANCH} \
		-t ${CHAIN_SIMULATOR_IMAGE_NAME}:${CHAIN_SIMULATOR_IMAGE_TAG} \
		-f ${DOCKER_FILE} \
		.

docker-run:
	docker run -d --name ${CONTAINER_NAME} ${CHAIN_SIMULATOR_IMAGE_NAME}:${CHAIN_SIMULATOR_IMAGE_TAG}

docker-stop:
	docker stop ${CONTAINER_NAME}

docker-rm:
	docker rm ${CONTAINER_NAME}

setup-env:
	rm -rf ${REPO_TEMP_DIR}
	git clone --branch ${SIMULATOR_REPO_BRANCH} ${SIMULATOR_REPO_URL} ${REPO_TEMP_DIR}
	python3 -m venv ${PIP_ENV_DIR}
	. ${PIP_ENV_DIR}/bin/activate && pip install -r ${REPO_TEMP_DIR}/examples/requirements.txt
	rm -rf ${REPO_TEMP_DIR}

run-examples: setup-env
	$(MAKE) docker-build
	docker run -d --name "${IMAGE_NAME}" -p 8085:8085 ${CHAIN_SIMULATOR_IMAGE_NAME}:${CHAIN_SIMULATOR_IMAGE_TAG}
	echo "Running script in ${REPO_TEMP_DIR}/scripts/run-examples"
	. ${PIP_ENV_DIR}/bin/activate && /bin/bash ${REPO_TEMP_DIR}/scripts/run-examples/script.sh
	docker stop "${IMAGE_NAME}"
	docker rm ${IMAGE_NAME}
