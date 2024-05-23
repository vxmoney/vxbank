# Makefile

CHAIN_SIMULATOR_IMAGE_NAME=chainsimulator
CHAIN_SIMULATOR_IMAGE_TAG=latest
DOCKER_FILE=Dockerfile
IMAGE_NAME=simulator_image
SIMULATOR_REPO_URL=https://github.com/multiversx/mx-chain-simulator-go
SIMULATOR_REPO_BRANCH=main

docker-build:
	docker build \
		--build-arg SIMULATOR_REPO_URL=${SIMULATOR_REPO_URL} \
		--build-arg SIMULATOR_REPO_BRANCH=${SIMULATOR_REPO_BRANCH} \
		-t ${CHAIN_SIMULATOR_IMAGE_NAME}:${CHAIN_SIMULATOR_IMAGE_TAG} \
		-f ${DOCKER_FILE} \
		.

setup-env:
	python3 -m venv testEnv
	. testEnv/bin/activate && pip install -r examples/requirements.txt


