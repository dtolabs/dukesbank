Building third-party software as RPMS
=====================================

This section of the example source demonstrates building 3rd-party software as RPM packages using Ant.

* Here is an example invocation of the Ant build of the JBoss RPM showing the three required properties that must be provided to selectively build each of the packages specified as SPECS templates:

<pre>
[anthony@centos62 rpmbuild]$ ant -Dname=jboss -Dversion=4.0.3SP1 -Drelease=8
Buildfile: build.xml

build:
     [copy] Copying 1 file to /mnt/hgfs/anthony/src/dukesbank/examples/example2/src/rpmbuild/SPECS
      [rpm] Building the RPM based on the jboss-4.0.3SP1.spec file
      [rpm] Executing(%prep): /bin/sh -e /var/tmp/rpm-tmp.Pe6W45
      [rpm] + umask 022
      [rpm] + cd /mnt/hgfs/anthony/src/dukesbank/examples/example2/src/rpmbuild/BUILD
      [rpm] + cd /mnt/hgfs/anthony/src/dukesbank/examples/example2/src/rpmbuild/BUILD
      [rpm] + rm -rf jboss-4.0.3SP1
.
.
.
      [rpm] + rm -rf jboss-4.0.3SP1
      [rpm] + exit 0
   [delete] Deleting: /mnt/hgfs/anthony/src/dukesbank/examples/example2/src/rpmbuild/SPECS/jboss-4.0.3SP1.spec

BUILD SUCCESSFUL
Total time: 1 minute 6 seconds
[anthony@centos62 rpmbuild]$ rpm -qi -p RPMS/noarch/jboss-4.0.3SP1-8.noarch.rpm 
Name        : jboss                        Relocations: /opt 
Version     : 4.0.3SP1                          Vendor: (none)
Release     : 8                             Build Date: Thu Apr  5 12:23:45 2012
Install Date: (not installed)               Build Host: centos62.localdomain
Group       : Applications/System           Source RPM: jboss-4.0.3SP1-8.src.rpm
Size        : 82070250                         License: LGPL
Signature   : (none)
Summary     : JBoss application server
Description :
JBoss application Server
</pre>

* Here is example of setting up an alternate RPM database, installing JBoss in an alternate location, and verifying the package is correctly installed:

<pre>
[anthony@centos62 tmp]$ sh -x doit 
+ rm -rf /home/anthony/tmp/rpm
+ rm -rf /home/anthony/tmp/jboss-4.0.3SP1
+ rpm --dbpath /home/anthony/tmp/rpm --initdb
+ rpm --dbpath /home/anthony/tmp/rpm --relocate /opt=/home/anthony/tmp -Uvh /home/anthony/src/dukesbank/examples/example2/src/rpmbuild/RPMS/noarch/jboss-4.0.3SP1-8.noarch.rpm
Preparing...                ########################################### [100%]
   1:jboss                  ########################################### [100%]
+ rpm --dbpath /home/anthony/tmp/rpm -qi jboss
Name        : jboss                        Relocations: /opt 
Version     : 4.0.3SP1                          Vendor: (none)
Release     : 8                             Build Date: Thu Apr  5 12:23:45 2012
Install Date: Thu Apr  5 12:40:22 2012         Build Host: centos62.localdomain
Group       : Applications/System           Source RPM: jboss-4.0.3SP1-8.src.rpm
Size        : 82070250                         License: LGPL
Signature   : (none)
Summary     : JBoss application server
Description :
JBoss application Server
+ rpm --dbpath /home/anthony/tmp/rpm -V jboss
</pre>
