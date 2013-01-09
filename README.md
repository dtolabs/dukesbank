This is a collection of build and deployment automation examples built around the Duke's Bank J2EE sample application.  You can find documention at the DTO Solutions Knowledge Base Wiki (http://kb.dtosolutions.com/wiki/Duke%27s_Bank_J2EE_examples).

Amazon/EC2 Console and Auth Info:
   * Amazon EC2 (Console Login)[https://console.aws.amazon.com/ec2/home?region=us-east-1#s=Instances]

Start DukesBank Instances matching:  dukesbank.dtolabs.com
   build
   repo
   deploy
   app

Configure elastic IPs for:  build, repo, deploy, and app as shown here:

build i-276d1f5c (Jenkins)
   * ElasticIP:  75.101.135.178    build.dukesbank.dtolabs.com
   * Jenkins Url:  http://build.dukesbank.dtolabs.com:8080/
   * Credentials not necessary
   * Build Jobs:  JBoss package, DukesBank (mysteriously called TestBuild) and DukesBank Config)

repo i-3f512344   (Nexus)
   * ElasticIP:  75.101.135.190    repo.dukesbank.dtolabs.com
   * Nexus Url:  http://repo.dukesbank.dtolabs.com:8081/nexus/
   * Credentials:  admin/admin123

deploy i-29572552   (Rundeck)
   * ElasticIP:  75.101.135.205    deploy.dukesbank.dtolabs.com
   * Rundeck Url:  http://deploy.dukesbank.dtolabs.com:4440/
   * Credentials:  admin/admin

app i-1b5b2960 (JBoss)
   * ElasticIP:  75.101.135.206    app.dukesbank.dtolabs.com
   * JBoss Url:  http://app.dukesbank.dtolabs.com:8180/
   * Bank Url:  http://app.dukesbank.dtolabs.com:8180/bank

spare i-a78e11dc
   * ElasticIP:  75.101.135.167    dukesbank.dtolabs.com



NOTES:

After Forking.....

$ANT_HOME/lib needs:
   ivy-2.3.0-rc1.jar
