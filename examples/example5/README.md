Duke's Bank example #5
======================

This example shows how to automate continuous build and deployment with emphasis on using [Rerun](http://rerun.github.com/rerun/) as the modular automation tool.

Application
-----------
 
Duke's Bank sample J2EE application running on JBoss with MySQL.

Infrastructure
--------------

A single pre-provisioned Red Hat Linux (tested on "CentOS release 6.2") instance for build, repository, deployment, application and database services.

Tool-chain
----------

* Souce code management: GitHub/Git
* Build tool: Ant and Rerun
* Build console: Jenkins
* Package format: RPM
* Package repository: Nexus
* Deployment console: Rundeck
* Modular automation: Rerun

Anthony Shortland
anthony@dtosolutions.com
