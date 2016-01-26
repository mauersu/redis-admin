# redis-admin [![Build Status](https://travis-ci.org/mauersu/redis-admin.svg?branch=master)](https://travis-ci.org/mauersu/redis-admin)
[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://github.com/mauersu/redis-admin/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

This is a redis client web tool written based on Java EE and Jedis. It's my objective to build the most convenient redis client web tool in the world. In the first place, it will facilitate in editing redis data, such as: add, update, delete, search, cut, copy, paste etc.

![](https://www.google.com/logos/2012/halloween-2012-hp.jpg)

## Features

**Multiple Redis version adaptive**

 1. Manage redis server, support server password authentication
 2. Manage redis data
 	* New redis data: string, list, hash, set, sorted set
 	* Rename redis data 
 	* Delete redis data
 	* Update redis data
 	* Cut, copy paste redis data
 	* Search redis data by key
 	* Order redis data by key
 	* Support time to live
 	* Support multiple language, now support English and Chinese

##  Screenshots
![Showcase](http://mauersu.github.io/img/redis_admin_zset.png)

##  Quick Start

`first step`:Edit file:'redis.properties' :
first of all, set up redis.server.num, this is redis.properties example:

```
redis.server.num=1
redis.language=English

#must set a default redis
redis.host.1=10.100.142.9
redis.name.1=10.100.142.9
redis.port.1=6379
redis.password.1=SH89qwIO

redis.host.2=yours
redis.name.2=yours
redis.port.2=yours
redis.password.2=yours
```

`second step`:Edit file:'application.properties' :

```
####Security Manager
manager.username=admin
manager.password=admin
```

`third step`: deploy project

run maven command : mvn clean package
you will found war in 'target/redis-admin.war'
move war to ../tomcat/wabapps and start tomcat 

`last step`: visit redis-admin

open brower and visit: http://IP:[port]/redis-admin/redis
enter username:{manager.username} and password:{manager.password}
have fun ^ ^ 

![img-source-from-https://github.com/docker/dockercraft](https://github.com/docker/dockercraft/raw/master/docs/img/contribute.png?raw=true)
