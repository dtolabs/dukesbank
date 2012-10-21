Summary: JBoss EJB container
Name: jboss
Version: %{version}
Release: %{release} 
Source0: jboss-%{version}.zip
 
License: LGPL
Group: Applications/System

BuildArch: noarch
 
# Disables debug packages and stripping of binaries:
%global _enable_debug_package 0
%global debug_package %{nil}
%global __os_install_post %{nil}
 
%description
JBoss EJB container
 
%prep

%setup
# Overlay the raw package contents with the customized files and directories:
cp -R $RPM_SOURCE_DIR/%{name}-%{version}/* $RPM_BUILD_DIR/%{name}-%{version}

%build
 
%install
rm -rf %{buildroot}
install -d -m 755 %{buildroot}/usr/local/jboss
cp -R . %{buildroot}/usr/local/jboss
# Makes sure that the key shell scripts are executable:
chmod +x %{buildroot}/usr/local/jboss/bin/*.sh
# Makes sure the default instance's lof directory exists:
mkdir -p %{buildroot}/usr/local/jboss/server/default/log
# Install the default init script included with JBoss:
mkdir -p %{buildroot}/etc/init.d
cp %{buildroot}/usr/local/jboss/bin/jboss_init_redhat.sh %{buildroot}/etc/init.d/jboss

%clean

%files
%defattr(-,jboss,jboss)
%attr(755,root,root) /etc/init.d/jboss
/usr/local/jboss
 
%changelog

%pre
if id jboss > /dev/null 2>&1
then
  :
else
  groupadd -f jboss
  useradd -rd /usr/local/jboss -gjboss jboss
  passwd -l jboss
fi

%post
chkconfig --add jboss
chkconfig --level 345 jboss on
