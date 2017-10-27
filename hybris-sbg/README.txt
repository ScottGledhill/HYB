hybris Subscription Billing Gateway (SBG) Version 5.6 08/06/2015 - Integration Guide
====================================================================================

The hybris Subscription Billing Gateway (SBG) functionality is built on hybris Core+ technology using a CIS design pattern and is designed to be deployed as a .war file.
This document covers integration of the out-of-the-box and your generated .war file with the hybris Telco Accelerator.

Note:
This document uses the {HYBRIS_HOME} term to refer to the hybris-commerce-suite-5.6.0.0 directory which is created when you unzip the hybris-commerce-suite-5.6.0.0.zip file.


Integrate the Out-of-the-Box SBG Server with Telco Accelerator
--------------------------------------------------------------

Customers can use an out-of-the-box .war file provided with the Subscription Billing Gateway to mock a subscription billing provider system. 
In order to use the .war file delivered with the SBG package, perform the following steps:

1.) Follow the steps 1 to 5 described in the "Install and Initialize" section of the Telco Accelerator Quick Installation Guide document - https://wiki.hybris.com/x/r7XTDw.
  
2.) Navigate to the {HYBRIS_HOME}/hybris/installer/recipes/b2c_telco_full and copy all files to the {HYBRIS_HOME}/hybris/bin/platform/tomcat/lib folder.
 
3.) Start the hybris Server by following the steps described in the Start the hybris Server section of the Quick Installation document -  https://wiki.hybris.com/x/vpVzC.



Build Your Own SBG Server WebApp and Integrate it with Telco Accelerator
------------------------------------------------------------------------

Build Your Own SBG Server WebApp and Integrate it with Telco Accelerator

If you want to generate a .war file from your own Subscription Billing Gateway provider-specific implementation, follow the steps below:
Open the command prompt window and navigate to the directory where you created your SBG module. For more information on creating your own SBG module, see the Tips for Configuring New Subscription Billing Management Provider Implementation document - https://wiki.hybris.com/x/39fdDQ. 

If your SBG module is named, let's say, hybris-sbg-provider-demo, you should see a pom.xml file and the following folders in this directory: 

hybris-sbg-provider-demo-api
hybris-sbg-provider-demo-integration-test
hybris-sbg-provider-demo-service
hybris-sbg-provider-demo-test-configuration
hybris-sbg-provider-demo-web
 
Run the mvn clean install -Dmaven.repo.local={HYBRIS_HOME}/hybris-dependencies command from within this directory. Running this command results in creating a .war file.
 
You can now find the .war file in the hybris-sbg-provider-demo-web/target directory.
 
In order for the hybris Server to automatically deploy and start your .war file, copy the path to the .war file and navigate to the {HYBRIS_HOME}/hybris/config directory and open the localextensions.xml file.
 
In the localextensions.xml file, add the path to your .war file inside the <extensions> tags. Assuming that the artifactId parameter value is hybris-sbg-provider-demo, the version is 1.0, and your .war file is located in the C:/hybris-sbg-provider-demo/hybris-sbg-provider-demo-web/target directory, your localextensions.xml webapp entry should look as follows:

localextensions.xml
<extensions>
 
    <webapp contextroot="hybris-sbg-provider-demo" path="C:/hybris-sbg-provider-demo/hybris-sbg-provider-demo-web/target/hybris-sbg-provider-demo-web-1.0.war"/>
 
</extensions>
 
Note:
You have to include the web app at the end of the localextensions.xml file, after all regular extensions. For more information, see the Deployment of External WAR Files section of the hybris Server document.
You also have to make sure the cis.client.subscription.mock property in the local.properties file is set to false. Otherwise, the Subscription Billing Gateway mock implementation from the cissubscription extension is used. For more information, see the cissubscription Extension - Technical Guide document - https://wiki.hybris.com/x/mUydDQ.


Start the hybris Server by following the steps from the Start the hybris Server section of the Quick Installation document - https://wiki.hybris.com/x/vpVzC.


Contents of Release
---------------------

This section contains a general summary of the files and directories in the hybris Subscription Billing Gateway (SBG) Version 5.6.0.0 release.

--> Binary: Contains pre-packaged MOCK web application (WAR). Use this for demonstration or testing purposes.		
	
--> Documentation: Contains generated API and REST documentation.

[y] hybris Platform
Copyright (c) 2000-2015 hybris AG. All rights reserved.
This software is the confidential and proprietary information of hybris ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with hybris.

