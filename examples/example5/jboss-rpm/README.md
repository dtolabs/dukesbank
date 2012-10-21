Building JBoss as an RPM
========================

This section of the example source demonstrates building JBoss as an RPM package using rpmbuild invoked from the Rerun rpm module's build command:

<pre>
[anthony@centos62-dukesbank-rerun example5]$ rerun rpm:build --topdir jboss-rpm --name jboss --version 4.0.3SP1 --release 8
Executing(%prep): /bin/sh -e /var/tmp/rpm-tmp.edPs9F
+ umask 022
+ cd /home/anthony/src/dtolabs/dukesbank/examples/example5/jboss-rpm/BUILD
+ cd /home/anthony/src/dtolabs/dukesbank/examples/example5/jboss-rpm/BUILD
+ rm -rf jboss-4.0.3SP1
.
.
.
Wrote: /home/anthony/src/dtolabs/dukesbank/examples/example5/jboss-rpm/RPMS/noarch/jboss-4.0.3SP1-8.noarch.rpm
Executing(%clean): /bin/sh -e /var/tmp/rpm-tmp.pmiOei
+ umask 022
+ cd /home/anthony/src/dtolabs/dukesbank/examples/example5/jboss-rpm/BUILD
+ cd jboss-4.0.3SP1
+ exit 0
</pre>

The specification file attempts to follow JBoss conventions as closely as possible while ensuring that the default instance will be available using the default port set and manageable using the system service (init.d) script provided with the distribution.
