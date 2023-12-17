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

## Configuration

The applications.properties file contains several configuration parameters that you may need to customize.
```
server.port=8081
transporter.xnat_host=http://localhost
transporter.xnat_port=8081
transporter.default_scp_port=2222
transporter.xnat_app_path_mapping=/data/xnat/build/:/data/xnat/build/
transporter.scp-host-key-path=/tmp/host_key.pem
```
- server.port: The http port of the Transporter Application. Although there is no web UI, some interfaces are available via REST API or Swagger at [http://hostname:port/swagger-ui/]   
- transporter.xnat_host: The URL of the XNAT host. This is used by the Transporter Application to access XNAT metadat via REST API.
- transporter.xnat_port: The port of the XNAT host.
- transporter.default_scp_port: This is the port used to expose the SCP server on this application.
- transporter.scp-host-key-path: File path to an ssh host key. If no file exists at this path, a host key will be generated and saved at this path. 


## Transporter Usage

`scp -P 2222 -rp -O username@host_url:snapshot_label /destination/folder`
