This is a collection of build and deployment automation examples built around the Duke's Bank J2EE sample  

Amazon/EC2 Console and Auth Info:
   * Amazon EC2 [Console Login](https://console.aws.amazon.com/ec2/home?region=us-east-1#s=Instances)
   * username:  anthony@dtosolutions.com
   * password:  NOT_SHOWN (can send if you need it)
   * SSH Key:  dukesbank.dtolabs.com_rsa  (can send if you need it)
   * Search for "dukesbank.dtolabs.com" in instances search bar will list the dukesbank instances: build, repo, deploy, app

Start the EC2 Instances

Configure elastic IPs for:  build, repo, deploy, and app as shown here via the EC2 Console:

(NOTE:  elastic IPs will be disassociated from these instances then they are stopped, so when we start them they must be re-associated)

build i-276d1f5c (Jenkins)
   * ElasticIP:  75.101.135.178    build.dukesbank.dtolabs.com
   * Jenkins Url:  [http://build.dukesbank.dtolabs.com:8080/](http://build.dukesbank.dtolabs.com:8080/)
   * Credentials not necessary

repo i-3f512344   (Nexus)
   * ElasticIP:  75.101.135.190    repo.dukesbank.dtolabs.com
   * Nexus Url:  [http://repo.dukesbank.dtolabs.com:8081/nexus/](http://repo.dukesbank.dtolabs.com:8081/nexus/)
   * Credentials:  factory default

deploy i-29572552   (Rundeck/Yum)
   * ElasticIP:  75.101.135.205    deploy.dukesbank.dtolabs.com
   * Rundeck Url:  [http://deploy.dukesbank.dtolabs.com:4440/](http://deploy.dukesbank.dtolabs.com:4440/)
   * Yum Url: [http://deploy.dukesbank.dtolabs.com/yum/](http://deploy.dukesbank.dtolabs.com/yum/)
   * Credentials:  factory default

app i-1b5b2960 (JBoss)
   * ElasticIP:  75.101.135.206    app.dukesbank.dtolabs.com
   * JBoss Url:  [http://app.dukesbank.dtolabs.com:8180/](http://app.dukesbank.dtolabs.com:8180/)
   * Bank Url:  [http://app.dukesbank.dtolabs.com:8180/bank](http://app.dukesbank.dtolabs.com:8180/bank)

spare i-a78e11dc
   * ElasticIP:  75.101.135.167    dukesbank.dtolabs.com

Jenkins Jobs:
   * All jobs are committed into source control: workshop/jenkins/
   * All jobs use:  git://github.com/dtolabs/dukesbank.git
   * [Jboss_403](http://build.dukesbank.dtolabs.com:8080/job/Jboss_403/): Produces JBoss RPM
   * [DukesBank](http://build.dukesbank.dtolabs.com:8080/job/DukesBank/): Produces the DukesBank EAR RPM
   * [DukesBank_Config](http://build.dukesbank.dtolabs.com:8080/job/DukesBank_Config/): Produces the DukesBank Configuration RPM


Login to the repo node and start Nexus:
<pre>
  Charles-Scotts-DTO-MacBook-Pro:2013Jan chuck$ ssh -i dukesbank.dtolabs.com_rsa -l ec2-user repo.dukesbank.dtolabs.com
  Last login: Tue Jan  8 18:02:07 2013 from c-24-130-174-168.hsd1.ca.comcast.net
  [ec2-user@ip-10-144-14-176 ~]$ sudo su - repouser
  [repouser@ip-10-144-14-176 ~]$ cd nexus/nexus
  [repouser@ip-10-144-14-176 nexus]$ bin/nexus start
</pre>

Rundeck Jobs: 
   * All jobs are committed into source control: workshop/rundeck/
   * [Start](http://deploy.dukesbank.dtolabs.com:4440/job/show/7a1d11a9-db8c-4a6f-a329-575a798e04e2): Start JBoss
   * [Stop](http://deploy.dukesbank.dtolabs.com:4440/job/show/9f86cbaf-f49c-4f53-8228-0d9416328e0a): Stop JBoss
   * [DeployFromJenkins](http://deploy.dukesbank.dtolabs.com:4440/job/show/5dac0b0c-ae39-4e54-8eaf-bab65bcf3bc9): deploys the latest DukesBank ear (only) from Jenkins onto the app node
   * [DeployFromNexus](http://deploy.dukesbank.dtolabs.com:4440/job/show/acb74c94-fab5-41bd-9c13-26aca5a3e8d4): deploys the selected DukesBank ear, config, and JBoss Container from Nexus onto the app node
   * [Deploy](http://deploy.dukesbank.dtolabs.com:4440/job/show/44ab3706-7b86-45c0-a464-5159079b3abd): broken but appears to issue with reusing DeployFromNexus without any parameters being passed.

NOTES:

Yum Server is empty but available on the deploy server. Requires apache server and installed as follows with /var/www/html/yum as the yum root:

<pre>
[ec2-user@deploy ~]$ hostname
deploy.dukesbank.dtolabs.com
[ec2-user@deploy ~]$ sudo yum -y install mod_ssl
Failed to set locale, defaulting to C
Loaded plugins: fastestmirror, security
Determining fastest mirrors
 * base: mirror.symnds.com
 * extras: mirrors-pa.sioru.com
 * updates: mirrors.advancedhosters.com
base                                             
...
...
Running Transaction
  Installing : httpd-tools-2.2.15-15.el6.centos.1.x86_64                                                                                                                                        1/5 
  Installing : apr-util-ldap-1.3.9-3.el6_0.1.x86_64                                                                                                                                             2/5 
  Installing : mailcap-2.1.31-2.el6.noarch                                                                                                                                                      3/5 
  Installing : httpd-2.2.15-15.el6.centos.1.x86_64                                                                                                                                              4/5 
  Installing : 1:mod_ssl-2.2.15-15.el6.centos.1.x86_64                                                                                                                                          5/5 

Installed:
  mod_ssl.x86_64 1:2.2.15-15.el6.centos.1                                                                                                                                                           

Dependency Installed:
  apr-util-ldap.x86_64 0:1.3.9-3.el6_0.1            httpd.x86_64 0:2.2.15-15.el6.centos.1            httpd-tools.x86_64 0:2.2.15-15.el6.centos.1            mailcap.noarch 0:2.1.31-2.el6           

Complete!
bash-4.1$ pwd
/var/www/html
bash-4.1$ ls -ld yum
drwxr-xr-x 2 apache root 4096 Jan 10 22:09 yum
[ec2-user@deploy html]$  sudo service httpd start
Starting httpd: httpd: Could not reliably determine the server's fully qualified domain name, using deploy.dukesbank.dtolabs.com for ServerName
                                                           [  OK  ]
</pre>

Jenkins/Java Configuration

Development OpenJDK installed:
<pre>
[ec2-user@build ~]$ hostname
build.dukesbank.dtolabs.com
[ec2-user@build ~]$ rpm -q java-1.6.0-openjdk-devel
java-1.6.0-openjdk-devel-1.6.0.0-1.48.1.11.3.el6_2.x86_64
</pre>

Jenkins/Ant Configuraiton, see Jenkins Global Settings which is how jenkins finds Ant.

Ant is installed in:  /opt/apache-ant-1.8.4
<pre>
[ec2-user@build ~]$ hostname
build.dukesbank.dtolabs.com
[ec2-user@build ~]$ cd /opt/apache-ant-1.8.4/
</pre>
requires ivy jar in ANT_HOME:
<pre>
[ec2-user@build apache-ant-1.8.4]$ ls -l lib/ivy-2.3.0-rc1.jar 
-rw-r--r-- 1 root root 1214376 Aug 16 21:21 lib/ivy-2.3.0-rc1.jar
</pre>

Obtain ivy jar from:  http://www.linuxtourist.com/apache//ant/ivy/2.3.0-rc2/apache-ivy-2.3.0-rc2-bin.tar.gz
<pre>
Charles-Scotts-DTO-MacBook-Pro:Downloads chuck$ gunzip -c apache-ivy-2.3.0-rc2-bin.tar.gz |tar tf - |grep \.jar$
apache-ivy-2.3.0-rc2/ivy-2.3.0-rc2.jar
</pre>
and drop into $ANT_HOME/lib
   
Trigger a CI build and deploy with the Rundeck/Jenkins plugin:

In a DukesBank checkout, we will change Bank message from Duke to Acme by committing a change to the WebMessages.properties file:

<pre>
[chuck@app dukesbank]$ pwd
/home/chuck/workspace/dtolabs/dukesbank
[chuck@app dukesbank]$  find . -name WebMessages.properties -print
./src/j2eetutorial14/examples/bank/web/WebMessages.properties
[chuck@app dukesbank]$ vi ./src/j2eetutorial14/examples/bank/web/WebMessages.properties
[chuck@app dukesbank]$ git diff ./src/j2eetutorial14/examples/bank/web/WebMessages.properties
diff --git a/src/j2eetutorial14/examples/bank/web/WebMessages.properties b/src/j2eetutorial14/examples/bank/web/WebMessages.properties
index a8fed89..269f687 100644
--- a/src/j2eetutorial14/examples/bank/web/WebMessages.properties
+++ b/src/j2eetutorial14/examples/bank/web/WebMessages.properties
@@ -25,7 +25,7 @@ LogonReturn=Return to log in page.
 CustomerId=Customer ID:
 Password=Password:
 
-Welcome=Welcome to Duke's Bank.
+Welcome=Welcome to Acme's Bank.
 Farewell=Thank you for banking with Anthony's Bank.
 
 AccountName=Account
 [chuck@app dukesbank]$ git commit -m 'change welcome msg' ./src/j2eetutorial14/examples/bank/web/WebMessages.properties
 [chuck@app dukesbank]$ git commit --all
</pre>
