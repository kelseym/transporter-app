# XNAT Transporter Application



The XNAT Transporter supports the creation of Data Snapshots and the transport of those snapshots to a remote host/client by way of the SCP protocol.

Transporter functionality is implemented across two components: the [XNAT Plugin](https://github.com/kelseym/transporter-plugin) and the [Transporter App](https://github.com/kelseym/transporter-app
).

![Transporter](https://drive.google.com/uc?id=1jQ01d_IpH4SPsQsTrAaDmZbfAF5J6PMi)

Snapshot creation is currently supported via [python script](https://github.com/kelseym/transporter-plugin/tree/main/snapshot-container) and the Docker container (`xnat/data-snapshot:0.1`).

## Alpha Version Application
* This appliaction is in Alpha release and is subject to breaking changes without notice.
* Building this application relies on Maven Central as well as a Maven repository hosted by the XNAT team

## Building the Application
The Transporter Application is implemented as a Java Spring Boot application. The build process can generate a `.jar` file as well as a Docker image.

1. Run `./gradlew clean build` to package the application jar. You will find the resultant jar under `./build/libs/xnat-transporter-app-[version]-all.jar`.
1. Run `./gradlew clean buildImage` to package the application and build a Docker image containing the required Java runtime.

## Transporter Usage

`scp -P 2222 -rp -O  -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null username@host_url:snapshot_label /destination/folder`
