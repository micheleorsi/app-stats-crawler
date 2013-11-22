app-stats-crawler
=================

A crawler to fetch stats for your **iOs** and **Android** apps. It connects to **iTunes Connect** and **Google Play** to fetch data, then store files in the raw format somewhere in the cloud. 

I know: there are already a lot of products and API in the market, **BUT** 
* you have to pay for them a lot of money for **services you probably don't need**
* it is not possible to **access raw-data**

I want to create a simple project, ready to deploy into **[Google App Engine](http://cloud.google.com/products/app-engine)**, that stores files into **[Google Cloud Storage](http://cloud.google.com/products/cloud-storage)**.

**app-stats-crawler** is an **OPEN Open Source Project**, see the <a href="#contributing">Contributing</a> section to find out what this means.

Requirements
------------

* [mvn](http://maven.apache.org) in order to build and deploy the project
* Google account in order to create a Google App Engine project
    
Configuration
-------------

* create a Google App Engine [project](https://cloud.google.com/console?getstarted=https://cloud.google.com/products/app-engine)
* create a file similar to [account.properties.sample](https://github.com/micheleorsi/app-stats-crawler/blob/master/src/main/resources/account.properties.sample) and subtitute your values 
    
Deployment
----------

    mvn clean deploy
    
Testing
-------

To run the tests:

    mvn clean test

Contributing
------------

See the [CONTRIBUTING.md](https://github.com/micheleorsi/app-stats-crawler/blob/master/CONTRIBUTING.md) file for more details.
