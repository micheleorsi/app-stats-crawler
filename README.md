app-stats-crawler
=================

A crawler to fetch statistics for your **iOs** and **Android** apps. 
It connects to **iTunes Connect** and **Google Play** to fetch data, then store the raw files somewhere in the cloud. 

[![Build Status](https://travis-ci.org/micheleorsi/app-stats-crawler.png?branch=master)](https://travis-ci.org/micheleorsi/app-stats-crawler)

**app-stats-crawler** is an **OPEN Open Source Project**, see the <a href="#contributing">Contributing</a> section to find out what this means.

The problem
-----------
There are already a lot of products and APIs in the market, **BUT** 
* you have to pay for them money for **services you probably never use**
* it is not possible to **access raw-data**
* you don't own your data

The solution
-----------
It is a simple project, ready to deploy into **[Google App Engine](http://cloud.google.com/products/app-engine)**, that stores files into **[Google Cloud Storage](http://cloud.google.com/products/cloud-storage)**.

Requirements
------------

* [mvn](http://maven.apache.org) in order to build and deploy the project
* Google account in order to create a Google App Engine project and a Google Cloud Storage bucket
    
Configuration
-------------

* create a Google App Engine [project](https://cloud.google.com/console?getstarted=https://cloud.google.com/products/app-engine)
* create a Google Cloud Storage [bucket](https://cloud.google.com/console?getstarted=https://cloud.google.com/products/cloud-storage)
* create a file similar to [account.properties.sample](https://github.com/micheleorsi/app-stats-crawler/blob/master/src/main/resources/account.properties.sample) and substitute your values 
    
Deployment
----------

    mvn clean appengine:update
    
Testing
-------

To run the tests:

    mvn clean test

Contributing
------------

See the [CONTRIBUTING.md](https://github.com/micheleorsi/app-stats-crawler/blob/master/CONTRIBUTING.md) file for more details.
