# Using the Server

This chapter will familiarise you with how to use the Apache ActiveMQ Artemis server.

We'll show where it is, how to start and stop it, and we'll describe the
directory layout and what all the files are and what they do.

For the remainder of this chapter when we talk about the Apache ActiveMQ Artemis server
we mean the Apache ActiveMQ Artemis standalone server, in its default configuration
with a JMS Service enabled.

This document will refer to the full path of the directory where the ActiveMQ
distribution has been extracted to as `${ARTEMIS_HOME}` directory.

Installation
============

After downloading the distribution, the following highlights some important folders on the distribution:

             |___ bin
             |
             |___ web
             |      |___ user-manual
             |      |___ api
             |
             |___ examples
             |      |___ core
             |      |___ javaee
             |      |___ jms
             |
             |___ lib
             |
             |___ schema


-   `bin` -- binaries and scripts needed to run ActiveMQ Artemis.

-   `web` -- The folder where the web context is loaded when ActiveMQ Artemis runs.

-   `user-manual` -- The user manual is placed under the web folder.

-   `api` -- The api documentation is placed under the web folder

-   `examples` -- JMS and Java EE examples. Please refer to the 'running
    examples' chapter for details on how to run them.

-   `lib` -- jars and libraries needed to run ActiveMQ Artemis

-   `licenses` -- licenses for ActiveMQ Artemis

-   `schemas` -- XML Schemas used to validate ActiveMQ Artemis configuration
    files


## Creating a Broker Instance

A broker instance is the directory containing all the configuration and runtime
data, such as logs and data files, associated with a broker process.  It is recommended that
you do *not* create the instance directory under `${ARTEMIS_HOME}`.  This separation is
encouraged so that you can more easily upgrade when the next version of ActiveMQ Artemis is released.

On Unix systems, it is a common convention to store this kind of runtime data under
the `/var/lib` directory.  For example, to create an instance at '/var/lib/mybroker', run
the following commands in your command line shell:

    cd /var/lib
    ${ARTEMIS_HOME}/bin/activemq create mybroker

A broker instance directory will contain the following sub directories:

 * `bin`: holds execution scripts associated with this instance.
 * `etc`: hold the instance configuration files
 * `data`: holds the data files used for storing persistent messages
 * `log`: holds rotating log files
 * `tmp`: holds temporary files that are safe to delete between broker runs

At this point you may want to adjust the default configuration located in
the `etc` directory.

###Options
There are several options you can use when creating an instance.

For a full list of updated properties always use:

```
 $./artemis help create
 NAME
         artemis create - creates a new broker instance

 SYNOPSIS
         artemis create [--allow-anonymous]
                 [--cluster-password <clusterPassword>] [--cluster-user <clusterUser>]
                 [--clustered] [--data <data>] [--encoding <encoding>] [--force]
                 [--home <home>] [--host <host>] [--java-options <javaOptions>]
                 [--password <password>] [--port-offset <portOffset>] [--replicated]
                 [--role <role>] [--shared-store] [--silent] [--user <user>] [--]
                 <directory>

 OPTIONS
         --allow-anonymous
             Enables anonymous configuration on security (Default: input)

         --cluster-password <clusterPassword>
             The cluster password to use for clustering. (Default: input)

         --cluster-user <clusterUser>
             The cluster user to use for clustering. (Default: input)

         --clustered
             Enable clustering

         --data <data>
             Directory where ActiveMQ Data is used. Path are relative to
             artemis.instance/bin

         --encoding <encoding>
             The encoding that text files should use

         --force
             Overwrite configuration at destination directory

         --home <home>
             Directory where ActiveMQ Artemis is installed

         --host <host>
             The host name of the broker (Default: 0.0.0.0 or input if clustered)

         --java-options <javaOptions>
             Extra java options to be passed to the profile

         --password <password>
             The user's password (Default: input)

         --port-offset <portOffset>
             Off sets the default ports

         --replicated
             Enable broker replication

         --role <role>
             The name for the role created (Default: amq)

         --shared-store
             Enable broker shared store

         --silent
             It will disable all the inputs, and it would make a best guess for
             any required input

         --user <user>
             The username (Default: input)

         --
             This option can be used to separate command-line options from the
             list of argument, (useful when arguments might be mistaken for
             command-line options

         <directory>
             The instance directory to hold the broker's configuration and data
```


Some of these properties may be mandatory in certain configurations and the system may ask you for additional input.

```
    ./artemis create /usr/server
    Creating ActiveMQ Artemis instance at: /user/server

    --user: is mandatory with this configuration:
    Please provide the default username:
    admin

    --password: is mandatory with this configuration:
    Please provide the default password:


    --allow-anonymous: is mandatory with this configuration:
    Allow anonymous access? (Y/N):
    y

    You can now start the broker by executing:

       "/user/server/bin/artemis" run

    Or you can run the broker in the background using:

       "/user/server/bin/artemis-service" start
```


### Starting and Stopping a Broker Instance

Assuming you created the broker instance under `/var/lib/mybroker` all you need
to do start running the broker instance is execute:

    /var/lib/mybroker/bin/activemq run

Now that the broker is running, you can optionally run some of the included
examples to verify the the broker is running properly.

To stop the Apache ActiveMQ Artemis instance you will use the same `activemq` script, but with
the `stop argument`.  Example:

    /var/lib/mybroker/bin/activemq stop

Please note that Apache ActiveMQ Artemis requires a Java 7 or later runtime to run.

By default the `etc/bootstrap.xml` configuration is
used. The configuration can be changed e.g. by running
`./activemq run -- xml:path/to/bootstrap.xml` or another
config of your choosing.

Environment variables are used to provide ease of changing ports, hosts and
data directories used and can be found in `etc/activemq.profile` on linux and
`etc\activemq.profile.cmd` on Windows.

## Server JVM settings

The run scripts set some JVM settings for tuning the garbage collection
policy and heap size. We recommend using a parallel garbage collection
algorithm to smooth out latency and minimise large GC pauses.

By default Apache ActiveMQ Artemis runs in a maximum of 1GiB of RAM. To increase the
memory settings change the `-Xms` and `-Xmx` memory settings as you
would for any Java program.

If you wish to add any more JVM arguments or tune the existing ones, the
run scripts are the place to do it.

## Pre-configured Options

The distribution contains several standard configuration sets for
running:

-   Non clustered stand-alone.

-   Clustered stand-alone

-   Replicated stand-alone

-   Shared-store stand-alone

You can of course create your own configuration and specify any
configuration when running the run script.

## Library Path

If you're using the [Asynchronous IO Journal](libaio.md) on Linux,
you need to specify `java.library.path` as a property on your Java
options. This is done automatically in the scripts.

If you don't specify `java.library.path` at your Java options then the
JVM will use the environment variable `LD_LIBRARY_PATH`.

You will need to make sure libaio is installed on Linux. For more information refer to the libaio chapter at
[Runtime Dependencies](libaio.html#runtime-dependencies)

## System properties

Apache ActiveMQ Artemis can take a system property on the command line for configuring
logging.

For more information on configuring logging, please see the section on
[Logging](logging.md).

## Configuration files

The configuration file used to bootstrap the server (e.g.
`bootstrap.xml` by default) references the specific broker configuration
files.

-   `broker.xml`. This is the main ActiveMQ
    configuration file. All the parameters in this file are
    described [here](configuration-index.md)

It is also possible to use system property substitution in all the
configuration files. by replacing a value with the name of a system
property. Here is an example of this with a connector configuration:

    <connector name="netty">tcp://${activemq.remoting.netty.host:localhost}:${activemq.remoting.netty.port:61616}</connector>

Here you can see we have replaced 2 values with system properties
`activemq.remoting.netty.host` and `activemq.remoting.netty.port`. These
values will be replaced by the value found in the system property if
there is one, if not they default back to localhost or 61616
respectively. It is also possible to not supply a default. i.e.
`${activemq.remoting.netty.host}`, however the system property *must* be
supplied in that case.

## Bootstrap File

The stand-alone server is basically a set of POJOs which are
instantiated by Airline commands.

The bootstrap file is very simple. Let's take a look at an example:

    <broker xmlns="http://activemq.org/schema">

       <file:core configuration="${activemq.home}/config/stand-alone/non-clustered/broker.xml"></core>

       <basic-security/>

    </broker>

-   core - Instantiates a core server using the configuration file from the
    `configuration` attribute. This is the main broker POJO necessary to
    do all the real messaging work.  In addition all JMS objects such as:
    Queues, Topics and ConnectionFactory instances are configured here.

## The main configuration file.

The configuration for the Apache ActiveMQ Artemis core server is contained in
`broker.xml`. This is what the FileConfiguration bean
uses to configure the messaging server.

There are many attributes which you can configure Apache ActiveMQ Artemis. In most
cases the defaults will do fine, in fact every attribute can be
defaulted which means a file with a single empty `configuration` element
is a valid configuration file. The different configuration will be
explained throughout the manual or you can refer to the configuration
reference [here](configuration-index.md).

Windows Server
==============

On windows you will have the option to run ActiveMQ Artemis as a service.
Just use the following command to install it:

```
 $ ./artemis-service.exe install
```


The create process should give you a hint of the available commands available for the artemis-service.exe