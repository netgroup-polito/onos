# ONOS Driver and Application for router Tiesse Imola 5262-IKF
____


This branch contains the driver and the application to configure the interfaces/VLAN of the router Tiesse Imola 5262-IKF using the NETCONF protocol and YANG data models.

The YANG data models describe the entities necessary to configure the router. For example the model "tiesse-vlan.yang" contains all the information  needed to set and assign VLANs in the Tiesse Imola.

The driver contains all the methods to start a NETCONF session with the router and send XML messages with the interfaces and VLAN configuration data.

The application waits until a configuration file (JSON) is sent to it, then parse the information contained and calls the driver's methods needed to send the data to the router.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
### Prerequisites

Firstly clone the branch "netconf_app_tiesse" on your PC. 

If you want to use this ONOS version then see the next step of this guide to know how to build ONOS with BUCK and run it.

Otherwise if you already have a version of ONOS on your PC and want to add the driver and the application to it:

* Copy the folder "examplenetconfdriver" in "onos/drivers" and add it in your "onos/drivers" folder.
* Copy the folder "tiessemanager" in "onos/apps" and add it in your "onos/apps" folder.
* Copy the folder "examplenetconfdriver" in "onos/models/" and add it in your "onos/models" folder.
* Edit the file "onos/modules.def" and add the entries:

```
ONOS_DRIVERS = [
'//drivers/examplenetconfdriver:onos-drivers-examplenetconfdriver-oar'
]

ONOS_APPS = [
 '//apps/tiessemanager:onos-apps-tiessemanager-oar'
]

MODELS = [
'//models/examplenetconfdriver:onos-models-examplenetconfdriver-oar'
]

```

### Building ONOS

The building process will automatically generates the Java Classes from the YANG data models.

To build ONOS with BUCK go to your ONOS folder:


```bash
cd onos
```

And then type the following command:

```
tools/build/onos-buck build onos --show-output
```

Wait until the process is completed, it can take a few minutes.
If the build is successful the auto-generated Java Classes from the YANG data models will be created in the folder:

```
onos/buck-out/gen/models/examplenetconfdriver/onos-models-examplenetconfdriver-yang#srcs__yang-gen
```



### Running ONOS

To run ONOS with BUCK go to your ONOS folder:


```bash
cd onos
```

And then type the following command:

```
tools/build/onos-buck run onos-local -- clean debug
```

### Accessing to ONOS CLI

To access the ONOS CLI go to your ONOS folder:


```bash
cd onos
```

And then type the following command:

```
tools/test/bin/onos localhost
```

## Activate driver and application in ONOS

Inside the ONOS CLI type the following commands:

```
app activate org.onosproject.drivers.examplenetconfdriver
app activate org.onosproject.tiessemanager
```

These commands will activate the driver and the application, making them ready to be used.

## Connect router Tiesse Imola to ONOS

To connect the router Tiesse Imola to ONOS it is needed to send ONOS information through the Network Configuration service by injecting a JSON file.

In this example the device used had the NETCONF server set at port "831". 

The IP address refers to interface "*eth1*", the interface to which the PC is connected in this example.

Create a JSON file, name it "netconf_tiesse.json" and copy in it the following content:

```json
{
  "devices": {
    "netconf:10.10.77.29:831": {
      "netconf": {
        "ip": "10.10.77.29",
        "port": 831,
        "username": "root",
        "password": "tiesseadm"
      },
      "basic": {
        "driver": "examplenetconfdriver-netconf"
      }
    }
  }
}
```

Then open a terminal, move to the folder containing the JSON file just created and send it to ONOS through the Network Configuration service to make ONOS capable to see the device.

To inject the JSON file use the following command:

```
onos-netcfg localhost netconf_tiesse.json
```

If this process was successful then the device should appear in the list of devices.

To check this list access the ONOS CLI and then type:

```
devices
```


## Send the configuration to the Tiesse Imola

To inject the VLAN and interfaces configuration in ONOS it has to be used the Network Configuration service.

Create a JSON file, name it "tiessemanager-config.json" and write in it the interfaces/VLAN configuration.

The following example shows the configuration to set 2 interfaces in *ACCESS mode* and 1 in *TRUNK mode* and assign them a VLAN.
```json
{  
  "apps":{  
    "org.onosproject.tiessemanager":{  
      "vlansconfig":{  
		"vlans": [
			{ 
				"mode": "ACCESS", 
				"interface":"eth2",
				"port":"2",
				"vlan":"222",
				"ipaddress": "192.168.20.2",
				"netmask" : "255.255.255.0",
				"broadcast" : "192.168.20.255"
			},
			{ 
				"mode": "ACCESS", 
				"interface":"eth4",
				"port":"4",
				"vlan":"444",
				"ipaddress": "192.168.40.4",
				"netmask" : "255.255.255.0",
				"broadcast" : "192.168.40.255"
			},
			{
				"mode": "TRUNK", 
				"interface" : "eth3",
				"port":"3",
				"vlan":"222",
				"ipaddress": "192.168.30.31",
				"netmask" : "255.255.255.0",
				"broadcast" : "192.168.30.255"
			},
			{ 
				"mode": "TRUNK",
				"interface" : "eth3",
				"port":"3",
				"vlan":"444",
				"ipaddress": "192.168.30.32",
				"netmask" : "255.255.255.0",
				"broadcast" : "192.168.30.255"
			}
		]
      }
    }
  }
}
```

Then open a terminal, move to the folder containing the JSON file just created and send it to ONOS through the Network Configuration service to send it to the application "tiessemanager".

To inject the JSON file to the application use the following command:

```
onos-netcfg localhost tiessemanager-config.json
```

If this process was successful then the application will start to parse the JSON file and will call the driver's methods needed to start a NETCONF session with the router Tiesse Imola and send it the XML messages with the configuration to apply.

The Tiesse Imola will proceed then to apply the configuration to its interfaces. This process will take a few seconds to be completed.