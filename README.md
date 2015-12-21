
# Anypoint Template: MUnit testing of Scatter Gather in the Salesforce and SAP Aggregation template

This application illustrates how to using mocks to test the Scatter Gather through an example of integrating Salesforce and SAP.

### Assumptions ###

This document assumes that you are familiar with Mule and the [Anypoint™ Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial). Further, this example assumes that you have a basic understanding of [Mule flows](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture), [Mule Global Elements](http://www.mulesoft.org/documentation/display/current/Global+Elements), Scatter Gather component [Scatter Gather](http://www.mulesoft.org/documentation/display/current/scatter-gather) and the [MUnit](https://docs.mulesoft.com/mule-user-guide/v/3.7/munit).
This document describes the details of the example within the context of Anypoint Studio, Mule ESB’s graphical user interface.

### Use Case ###

This application aggregates Products from a Salesforce Instance and Materials from SAP, compares the records to avoid duplication and then tranfers it to a CSV file which is then sent as an attachment via email. This application also shows you how to use MUnit to test the solution using mocks.

# Considerations

To make this Anypoint Template run, there are certain preconditions that must be considered. All of them deal with the preparations in both, that must be made in order for all to run smoothly.
**Failling to do so could lead to unexpected behavior of the template.**

Before continue with the use of this Anypoint Template, you may want to check out this [Documentation Page](http://www.mulesoft.org/documentation/display/current/SAP+Connector#SAPConnector-EnablingYourStudioProjectforSAP), that will teach you how to work 
with SAP and Anypoint Studio.

## Disclaimer

This Anypoint template uses a few private Maven dependencies in oder to work. If you intend to run this template with Maven support, please continue reading.

You will find that there are three dependencies in the pom.xml file that begin with the following group id: 
	**com.sap.conn.jco** 
These dependencies are private for Mulesoft and will cause you application not to build from a Maven command line. You need to replace them with "provided" scope and copy the libraries into the build path.


### Application Configuration ###
To make the application run, it's required to configure the different end points involved. The configuration files is located for the chosen environment in the [src/main/resources/mule.env.properties]
The following is an example to show you what vales are expected from the user during the configuration. To run the MUnit tests, the configuration file is located in the [src/test/resources/mule.test.properties]

+ http.port `9090` 
+ sap.maxrows `100`
+ page.size `100`
 
### Salesforce `Connector configuration`
+ sfdc.username `bob.dylan@sfdc`
+ sfdc.password `DylanPassword123`
+ sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`
+ sfdc.url `https://test.salesforce.com/services/Soap/u/32.0`

### SAP Connector configuration
+ sap.jco.ashost `your.sap.address.com`
+ sap.jco.user `SAP_USER`
+ sap.jco.passwd `SAP_PASS`
+ sap.jco.sysnr `14`
+ sap.jco.client `800`
+ sap.jco.lang `EN`

### SMTP Services configuration
+ smtp.host `smtp.gmail.com`
+ smtp.port `587`
+ smtp.user `exampleuser@gmail.com`
+ smtp.password `ExamplePassword456`

### Mail details
+ mail.from `exampleuser@gmail.com`
+ mail.to `woody.guthrie@gmail.com`
+ mail.subject `SFDC Products Report`
+ mail.body `Report comparing Products from SFDC and SAP Materials`
+ attachment.name `OrderedReport.csv`
 

### How it Works ###
The application is structured in the several files based on the functional aspect.

## config.xml
Configuration for Connectors and [Properties Place Holders](http://www.mulesoft.org/documentation/display/current/Configuring+Properties) are set in this file. **Even you can change the configuration here, all parameters that can be modified here are in properties file, and this is the recommended place to do it so.** Of course if you want to do core changes to the logic you will probably need to modify this file.

In the visual editor they can be found on the *Global Element* tab.


## businessLogic.xml
Functional aspect of the Template is implemented on this XML, directed by one flow responsible of conducting the aggregation of data, comparing records and finally formating the output, in this case being a report.
The *mainFlow* organises the job in three different steps and finally invokes the *outboundFlow* that will deliver the report to the corresponding outbound endpoint.
This flow has Exception Strategy that basically consists on invoking the *defaultChoiseExceptionStrategy* defined in *errorHandling.xml* file.

### Gather Data Flow
Mainly consisting of two calls (Queries) to Salesforce and SAP and storing each response on the Invocation Variable named *productsFromSalesforce* or *productsFromSap* accordingly.

[Scatter Gather](http://www.mulesoft.org/documentation/display/current/Scatter-Gather) is responsible for aggregating the results from the two collections of Opportunities.
Criteria and format applied:
+ Scatter Gather component implements an aggregation strategy that results in List of Maps with keys: **Name**, **IDInSalesforce** and **IDInSap**.
+ Products will be matched by name, that is to say, a record in both organisations with same name is considered the same product.

### Format Output Flow
+ [Java Transformer](http://www.mulesoft.org/documentation/display/current/Java+Transformer+Reference) responsible for sorting the list of products in the following order:

1. Products only in Salesforce
2. Products only in SAP
3. Products in both Salesforce and SAP

All records ordered alphabetically by name within each category.
If you want to change this order then the *compare* method should be modified.

+ CSV Report [DataWeaver](https://developer.mulesoft.com/docs/dataweave) transforming the List of Maps in CSV with headers **Name**, **IDInSalesforce** and **IDInSap**.
+ An [Object to string transformer](http://www.mulesoft.org/documentation/display/current/Transformers) is used to set the payload as an String.


## endpoints.xml
This is the file where you will found the inbound and outbound sides of your integration app.
This Template has an [HTTP Inbound Endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Endpoint+Reference) as the way to trigger the use case and an [SMTP Transport](http://www.mulesoft.org/documentation/display/current/SMTP+Transport+Reference) as the outbound way to send the report.

### Trigger Flow
**HTTP Inbound Endpoint** - Start Report Generation
+ `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
+ The path configured by default is `generatereport` and you are free to change for the one you prefer.
+ The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub will then route requests from your application domain URL to the endpoint.

### Outbound Flow
**SMTP Outbound Endpoint** - Send E-Mail
+ Both SMTP Server configuration and the actual e-mail to be sent are defined in this endpoint.
+ This flow is going to be invoked from the flow that does all the functional work: *mainFlow*, the same that is invoked from the Inbound Flow upon triggering of the HTTP Endpoint.


## errorHandling.xml
This is the right place to handle how your integration will react depending on the different exceptions. 
This file holds a [Choice Exception Strategy](http://www.mulesoft.org/documentation/display/current/Choice+Exception+Strategy) that is referenced by the main flow in the business logic.


## businessLogic-test-suite.xml
The [MUnit](https://docs.mulesoft.com/mule-user-guide/v/3.7/munit) tests are implemented on this XML file. For testing we are using mocks to simulate the real world behavior of the application. 
