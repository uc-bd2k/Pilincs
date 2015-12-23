# Pilincs - interface to [panoramaweb.org](http://www.panoramaweb.org)

See running piLINCS website on University of Cincinnati server:
[eh3.uc.edu/pilincs](http://eh3.uc.edu/pilincs)

## Goals

* Provide interface and seamless access to LINCS proteomics datasets (P100, GCP, etc.) available in Panorama
* Build intermediate API layer to enable direct and simple querying and access to proteomics datasets by tools developed within BD2K community
* Facilitating data integration by creating a global view of L1000 and proteomic data through iLINCS and related portals

## Getting Started

In order to run your own piLINCS you need to have current Java installed. Gradle and git are used to ease building sources.

### Download sources

If git is installed just type:

```
git clone https://github.com/sajmmon/Pilincs.git pilincs
cd pilincs
```
Otherwise, download sources as a zip file directly from GitHub.

### Build project

If you have gradle installed, type:
```
gradle clean build
```
Otherwise, run scripts gradlew or gradle.bat.

### Run application

Application is bundled as a jar file. Run it with java by:
```
java -jar build/libs/*.jar
```

In the background Tomcat is started as well as H2 database. Your application is available in:
[localhost:8080/pilincs](http://www.localhost:8080/pilincs)

### Modify configuration

 To adjust the application to your needs look in:
 ```
 /src/main/resource/application.yml
 ```
 After changing properties file you need to build sources again. To run `mysql` profile type: 
 
 ```
 java --spring.profiles.active=mysql -jar build/libs/*.jar
 ```
 Consider changing database engine (a template of MySQL configuration is included). You may also change deployment path (currently set to /pilincs). Or just select Panorama folders that are relevant to your research.