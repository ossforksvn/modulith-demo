Server
==

This folder contains the modules required to build our core modulith application.

Dependencies
=== 

Given our demo is a Maven based project, the build tool will download the required binaries for our platform, we just need Java 21.
* Java 21 LTS (minimum)

Build
===
```bash
mvn clean install
``` 

Deploy
=== 

To deploy, launch Apache Karaf, and add our features repo to the container, then install the modulith-demo.

If all dependencies successful install, and start, then you'll see a console listing similar to below:

```text
jgoodyear@Jamies-MacBook-Pro-2 bin % ./karaf       
        __ __                  ____      
       / //_/____ __________ _/ __/      
      / ,<  / __ `/ ___/ __ `/ /_        
     / /| |/ /_/ / /  / /_/ / __/        
    /_/ |_|\__,_/_/   \__,_/_/         

  Apache Karaf (4.4.6)

Hit '<tab>' for a list of available commands
and '[cmd] --help' for help on a specific command.
Hit '<ctrl-d>' or type 'system:shutdown' or 'logout' to shutdown Karaf.

karaf@root()> feature:repo-add mvn:com.savoir.modulith/features/1.0.0-SNAPSHOT/xml/features
Adding feature url mvn:com.savoir.modulith/features/1.0.0-SNAPSHOT/xml/features
karaf@root()> feature:install modulith-demo 
karaf@root()> list
START LEVEL 100 , List Threshold: 50
 ID │ State  │ Lvl │ Version        │ Name
────┼────────┼─────┼────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
 33 │ Active │  80 │ 4.4.6          │ Apache Karaf :: OSGi Services :: Event
115 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: admin :: API
116 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: admin :: Impl
117 │ Active │  80 │ 2.18.0         │ Jackson-annotations
118 │ Active │  80 │ 2.18.0         │ Jackson-core
119 │ Active │  80 │ 2.18.0         │ jackson-databind
120 │ Active │  80 │ 2.18.0         │ Jackson-JAXRS: base
121 │ Active │  80 │ 2.18.0         │ Jackson-JAXRS: JSON
122 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: datastore :: API
123 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: datastore :: Impl
124 │ Active │  80 │ 0              │ file__Users_jgoodyear_.m2_repository_com_savoir_modulith_adminWar_1.0.0-SNAPSHOT_adminWar-1.0.0-SNAPSHOT.war
125 │ Active │  80 │ 0              │ file__Users_jgoodyear_.m2_repository_com_savoir_modulith_homeWar_1.0.0-SNAPSHOT_homeWar-1.0.0-SNAPSHOT.war
126 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: game :: API
127 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: game :: Impl
128 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: home :: API
129 │ Active │  80 │ 1.0.0.SNAPSHOT │ Modulith Demo :: home :: Impl
karaf@root()> 
```
