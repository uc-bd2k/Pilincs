# Pilincs - interface to [panoramaweb.org](http://www.panoramaweb.org)

Visit running website on University of Cincinnati server:
[eh3.uc.edu/pilincs](http://eh3.uc.edu/pilincs)

## Project's goals

* Provide interface and seamless access to LINCS proteomics datasets (P100, GCP, etc.) available in Panorama
* Build intermediate API layer to enable direct and simple querying and access to proteomics datasets by tools developed within BD2K community
* Facilitating data integration by creating a global view of L1000 and proteomic data through iLINCS and related portals

## Getting Started

To start piLINCS website on your machine you need to: download sources, build project and run created jar.


```
git clone https://github.com/angular/angular-seed.git
cd angular-seed
```

## Develope your piLINCS

You need to have current Java installed. Having gradle and git is useful, but not necessary.

### Download sources

You may download sources as a zip file from GitHub or if git is installed on your OS just type:

```
git clone https://github.com/sajmmon/Pilincs.git pilincs
cd pilincs
```

### Build project

If you have gradle installed, type:
```
gradle clean build
```
If you have no gradle and your OS is *-nix, type:
```
gradlew
```
If you have no gradle and your OS is Windows, type:
```
gradle.bat
```

### Run application

Just type:
```
java -jar build/libs/pilincs-0.1.0-SNAPSHOT.jar
```

In the background Tomcat is started as well as H2 database. Your application is available in:
[localhost:8080/pilincs](http://www.localhost:8080/pilincs)
