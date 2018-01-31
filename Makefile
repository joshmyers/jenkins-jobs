.DEFAULT_GOAL := help
SHELL := /bin/bash

DOCKER_CONTAINER_NAME ?= jenkins
DOCKER_IMAGE ?= hmpo-jenkins
DOCKER_PROJECT ?= hmpo-pex

JENKINS_JOBS_PATTERN ?= *.groovy
JENKINS_PASS ?= admin
JENKINS_PORT ?= 8080
JENKINS_USER ?= admin
JENKINS_VERSION ?= 2.92
JENKINS_DSL_VERSION ?= 1.67

help:
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: prepare-docker-image
prepare-docker-image: ## Build Jenkins container
	@echo "==> Building image: $(DOCKER_PROJECT):$(DOCKER_IMAGE)"
	@docker pull `grep "FROM " Dockerfile | cut -d ' ' -f 2` || true
	@docker build \
          --build-arg JENKINS_VERSION="${JENKINS_VERSION}" \
	  -t $(DOCKER_PROJECT)/$(DOCKER_IMAGE) .

.PHONY: jenkins
jenkins: prepare-docker-image ## Run Jenkins container
	@echo "==> Running Jenkins container"
	@docker run -idt \
	  --name $(DOCKER_CONTAINER_NAME) \
	  -p $(JENKINS_PORT):$(JENKINS_PORT) \
	  -e JENKINS_USER=$(JENKINS_USER) \
	  -e JENKINS_PASS=$(JENKINS_PASS) \
	  $(DOCKER_PROJECT)/$(DOCKER_IMAGE)

.PHONY: clean
clean: ## Clean container
	@echo "==> Cleaning $(DOCKER_CONTAINER_NAME)"
	@docker ps | grep $(DOCKER_CONTAINER_NAME) | awk '{print $$1 }' | xargs -I {} docker stop {} > /dev/null 2>&1
	@docker ps -a | grep $(DOCKER_CONTAINER_NAME) | awk '{print $$1 }' | xargs -I {} docker rm {} > /dev/null 2>&1

.PHONY: build
build: ## Build Gradle project
	@echo "==> Building Gradle project"
	@./gradlew build

.PHONY: jobs
jobs: ## Inject Jenkins jobs to local running Jenkins
	@echo "==> Injecting jobs via rest interface to local Jenkins"
	@echo "==> Waiting for Jenkins to be up and running..."
	@sleep 20
	@./gradlew rest \
	  -Dpattern=jobs/$(JENKINS_JOBS_PATTERN) \
	  -DbaseUrl=http://localhost:$(JENKINS_PORT) \
	  -Dusername=$(JENKINS_USER) \
	  -Dpassword=$(JENKINS_PASS)

.PHONY: all
all: clean build jenkins jobs ## All the above
