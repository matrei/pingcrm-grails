# Ping CRM on Grails
A demo application built with **[Grails](https://grails.org)** and **[Vue.js](https://vuejs.org)** to illustrate how **[Inertia.js](https://inertiajs.com/)** works.\
This is a port to Grails/Groovy of the [original Ping CRM](https://github.com/inertiajs/pingcrm) written in Laravel/PHP.\
It uses the [Grails Adapter for Inertia.js](https://github.com/matrei/grails-inertia-plugin) plugin.

![Screenshot of the Ping CRM application](screenshot.png)

> There is a hosted installation of this demo application available at:\
> https://pingcrm.mattiasreichel.com
> 
> The demo is running in a container that is recreated and the database is wiped and reseeded every hour.\
>**Please be respectful when editing data**.

## Requirements
- Java 17+
- Npm

## Installation
Clone the repo locally
```shell
git clone https://github.com/matrei/pingcrm-grails.git
cd pingcrm-grails
```
Install client dependencies
```shell
npm install
```
## Running
### In development mode ...
Serve client files with [hot module replacement](https://vitejs.dev/guide/features.html#hot-module-replacement)
```shell
npm run serve
```
and start the grails application
```shell
./gradlew bootRun
```
###  ... or in production mode
with bundled/minified client files
```shell
./gradlew -Dgrails.env=production bootRun
```

### You're ready to go!
Visit Ping CRM in your browser - http://localhost:8080

## Running tests
To run the Ping CRM test suite, run:
```shell
./gradlew check
```

## Build for production
To create a runnable war for production (in `~/pingcrm-grails/build/libs`)
```shell
./gradlew assemble
```
that can be run with:
```shell
java -jar build/libs/pingcrm-grails-2.0.4-SNAPSHOT.war
```

## Credits
* Port to Grails by Mattias Reichel (@mattias_reichel)
* Original work by Jonathan Reinink (@reinink) and contributors
