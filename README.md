# TechTube
An application to share good technical videos with the community.

## Tech stack:

* Kotlin
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* Thymeleaf
* Bootstrap CSS, HTMX
* Testcontainers
* Awaitility

## Prerequisites
* JDK 21
* Docker and Docker Compose
* Your favourite IDE (Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

Install JDK using [SDKMAN](https://sdkman.io/)

```shell
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk install java 21.0.7-tem
$ sdk install gradle
```

Verify the prerequisites

```shell
$ java -version
$ docker info
$ docker compose version
```

## How to?

```shell
# Clone the repository
$ git clone https://github.com/sivaprasadreddy/techtube.git
$ cd techtube

# Run tests
$ ./gradlew test

# Automatically format code using spotless plugin
$ ./gradlew spotlessApply

# Run/Debug application from IDE
Run `src/main/kotlin/com/sivalabs/techtube/TechTubeApplication.kt` from IDE.

# Run application using Gradle
./gradlew bootRun
```

* Application: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui/index.html

## Using [Taskfile](https://taskfile.dev/) utility
Task is a task runner that we can use to run any arbitrary commands in easier way.

### Installation

```shell
$ brew install go-task
(or)
$ go install github.com/go-task/task/v3/cmd/task@latest

#verify task version
$ task --version
Task version: 3.35.1
```

### Using `task` to perform various tasks:

```shell
# Run tests
$ task test

# Automatically format code using spotless plugin
$ task format

# Build docker image
$ task build_image

# Run application in docker container
$ task start
$ task stop
$ task restart
```

* Application: http://localhost:8080
