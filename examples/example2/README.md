Duke's Bank example #2
======================

This example shows how to automate continuous build and deployment given the key design constraint of avoiding any use of super-user privileges while still achieving a high-quality reliable service-delivery platform.

Application
-----------
 
Duke's Bank sample J2EE application running on JBoss with MySQL or Oracle.

Infrastructure
--------------

Separate pre-provisioned Red Hat Linux (tested on "Red Hat Enterprise Linux Server release 5.6" and "CentOS release 6.2") instances for build, deployment, application and database services.

Tool-chain
----------

* Souce code management: Subversion
* Build tool: Ant
* Build console: Jenkins
* Package format: RPM
* Package repository: Nexus
* Deployment console: Rundeck
* Modular automation: Rerun

Anthony Shortland
anthony@dtosolutions.com
