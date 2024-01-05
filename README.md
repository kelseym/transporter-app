# XNAT Transporter Application



The XNAT Transporter supports the creation of Data Snapshots and the transport of those snapshots to a remote host/client by way of the *SCP protocol.

Transporter functionality is implemented across two components: the [XNAT Plugin](https://github.com/kelseym/transporter-plugin) and the [Transporter App](https://github.com/kelseym/transporter-app
).

![Transporter](https://drive.google.com/uc?id=1jQ01d_IpH4SPsQsTrAaDmZbfAF5J6PMi)

Snapshot creation is currently supported via [python script](https://github.com/kelseym/transporter-plugin/tree/main/snapshot-container) and the Docker container (`xnat/data-snapshot:0.1`).

## Beta Version Application
* This appliaction is in Beta release and is subject to breaking changes without notice.
* Building this application relies on Maven Central as well as a Maven repository hosted by the XNAT team

## Transporter App Usage

*This application supports a sub-set of the SCP protocol along with a special command structure used to list and download user-owned data snapshots from an XNAT instance.  Snapshot data is accessible from any network location that can reach the address and port of the application and by any XNAT user with access to snapshot data.  
  
Note that this service is download-only, using the following SCP command format:
  
`scp -P scp_port -rp -O username@host_url:snapshot_label /destination/folder`
  
For example, to list the snapshots available to the admin user on the XNAT site demo.xnat.org, issue the command below with the snapshot label left blank:

`scp -P 2222 -rp -0 admin@demo.xnat.org: /destination/folder`

```
User$ scp -P 2222 -rp -O  admin@demo.xnat.org: ~/Desktop/
Password authentication
(admin@demo.xnat.org) Password:
Received disconnect from ::1 port 2222:1: No valid snapshots requested.
Requested snapshots: [.]
Available snapshots: [SimpleSnapName, SomeOtherSnapshot]
Disconnected from ::1 port 2222
```

The application reports that two snapshots are available: `SimpleSnapName` and `SomeOtherSnapshot`.  To download snapshot data, issue the modified command:
`scp -P 2222 -rp -O  admin@demo.xnat.org:SimpleSnapName ~/Desktop/`
and provide a password.

```
User$ scp -P 2222 -rp -O  admin@demo.xnat.org:SimpleSnapName ~/Desktop/
Password authentication
(admin@demo.xnat.org) Password:
1.MR.head_DHead.4.129.20061214.091206.156000.1328618492.dcm                                                               100%  188KB  45.0MB/s   00:00
1.MR.head_DHead.4.45.20061214.091206.156000.2458818153.dcm                                                                100%  188KB  39.0MB/s   00:00
1.MR.head_DHead.4.42.20061214.091206.156000.1863818141.dcm                                                                100%  188KB  46.4MB/s   00:00
1.MR.head_DHead.4.5.20061214.091206.156000.3433817992.dcm                                                                 100%  188KB  40.5MB/s   00:00
1.MR.head_DHead.4.82.20061214.091206.156000.5539618302.dcm                                                                100%  188KB  45.7MB/s   00:00
1.MR.head_DHead.4.14.20061214.091206.156000.0554818030.dcm                                                                100%  188KB  44.8MB/s   00:00
1.MR.head_DHead.4.102.20061214.091206.156000.6488818382.dcm                                                               100%  188KB  45.1MB/s   00:00
1.MR.head_DHead.4.20.20061214.091206.156000.7316618054.dcm                                                                100%  188KB  46.0MB/s   00:00
1.MR.head_DHead.4.88.20061214.091206.156000.6805818328.dcm                                                                100%  188KB  45.0MB/s   00:00
1.MR.head_DHead.4.17.20061214.091206.156000.2700418040.dcm                                                                100%  188KB  40.5MB/s   00:00
```

In this example, the console reports all snapshot files that are downloaded to the specified destination folder. The downloaded data will retain the same directory hiearchy as specified in the snapshot definition, for example:
```
SIMPLE_PROJECT_ID/
SIMPLE_PROJECT_ID/arc001
SIMPLE_PROJECT_ID/arc001/Patient_007382
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.129.20061214.091206.156000.1328618492.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.45.20061214.091206.156000.2458818153.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.42.20061214.091206.156000.1863818141.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.5.20061214.091206.156000.3433817992.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.82.20061214.091206.156000.5539618302.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.14.20061214.091206.156000.0554818030.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.102.20061214.091206.156000.6488818382.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.20.20061214.091206.156000.7316618054.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.88.20061214.091206.156000.6805818328.dcm
SIMPLE_PROJECT_ID/arc001/Patient_007382/SCANS/4/DICOM/1.MR.head_DHead.4.17.20061214.091206.156000.2700418040.dcm
```
where `SIMPLE_PROJECT_ID` is the snapshot path root.


## Building the Application
The Transporter Application is implemented as a Java Spring Boot application. The build process can generate a `.jar` file as well as a Docker image.

1. Run `./gradlew clean build` to package the application jar. You will find the resultant jar under `./build/libs/xnat-transporter-app-[version]-all.jar`.
1. Run `./gradlew clean buildImage` to package the application and build a Docker image containing the required Java runtime.

## Configuration

:The applications.properties file contains several configuration parameters that you may need to customize.
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

