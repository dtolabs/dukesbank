Duke's Bank example #2
======================

This example shows how to automate continuous build and deployment of the Duke's Bank sample J2EE application running on JBoss and MySQL or Oracle.

The key design constraint for this example is the requirement to avoid any use of super-user privileges while still achieving a high-quality reliable service-delivery platform.

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
