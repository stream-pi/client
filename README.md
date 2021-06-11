# Stream-Pi Client

![version](https://img.shields.io/badge/Version-1.0.0-green)

## Prerequisites

- Java >= 11
- Maven >= 3.6.3

Note: If compiling the Android binary (apk) then GraalVM 21.1.0 (Java 11) should be used as JDK, or the path to it should be
set as value of environment variable `GRAALVM_HOME`.

## Quick Start

This project depends on the following Stream-Pi modules:

- [Stream-Pi Action API](https://github.com/stream-pi/action-api)
- [Stream-Pi Theme API](https://github.com/stream-pi/theme-api)
- [Stream-Pi Utilities](https://github.com/stream-pi/util) - **This needs to be installed first**


## Download binaries

Downloadable Binaries for available platforms are available [here](https://github.com/stream-pi/client/releases).

## Compile and Run from source 

Run `mvn clean javafx:run`
