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

deploy i-29572552   (Rundeck)
   * ElasticIP:  75.101.135.205    deploy.dukesbank.dtolabs.com
   * Rundeck Url:  [http://deploy.dukesbank.dtolabs.com:4440/](http://deploy.dukesbank.dtolabs.com:4440/)
   * Credentials:  factory default

app i-1b5b2960 (JBoss)
   * ElasticIP:  75.101.135.206    app.dukesbank.dtolabs.com
   * JBoss Url:  [http://app.dukesbank.dtolabs.com:8180/](http://app.dukesbank.dtolabs.com:8180/)
   * Bank Url:  [http://app.dukesbank.dtolabs.com:8180/bank](http://app.dukesbank.dtolabs.com:8180/bank)

spare i-a78e11dc
   * ElasticIP:  75.101.135.167    dukesbank.dtolabs.com

Jenkins Jobs:
   * All jobs use:  git://github.com/connaryscott/dukesbank.git
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
   * [Start](http://deploy.dukesbank.dtolabs.com:4440/job/show/7a1d11a9-db8c-4a6f-a329-575a798e04e2): Start JBoss
   * [Stop](http://deploy.dukesbank.dtolabs.com:4440/job/show/9f86cbaf-f49c-4f53-8228-0d9416328e0a): Stop JBoss
   * [DeployFromJenkins](http://deploy.dukesbank.dtolabs.com:4440/job/show/5dac0b0c-ae39-4e54-8eaf-bab65bcf3bc9): deploys the latest DukesBank ear (only) from Jenkins onto the app node
   * [DeployFromNexus](http://deploy.dukesbank.dtolabs.com:4440/job/show/acb74c94-fab5-41bd-9c13-26aca5a3e8d4): deploys the selected DukesBank ear, config, and JBoss Container from Nexus onto the app node
   * [Deploy](http://deploy.dukesbank.dtolabs.com:4440/job/show/44ab3706-7b86-45c0-a464-5159079b3abd): broken but appears to issue with reusing DeployFromNexus without any parameters being passed.

NOTES:


$ANT_HOME/lib needs:
   ivy-2.3.0-rc1.jar
