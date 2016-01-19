# redis-admin [![Build Status](https://travis-ci.org/mauersu/redis-admin.svg?branch=master)](https://travis-ci.org/mauersu/redis-admin)
[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://github.com/mauersu/redis-admin/releases)

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


## redis-admin使用帮助

### redis-admin配置介绍

`第一步`：修改配置文件 redis.properties
首先设置redis服务器数量redis.server.num，然后参照例子设置redis服务器登录参数，如下：

```
redis.server.num=1
redis.language=Chinese

#must set a default-name redis
redis.host.1=10.100.142.9
redis.name.1=default
redis.port.1=6379
redis.password.1=SH89qwIO

redis.host.2=yours
redis.name.2=yours
redis.port.2=yours
redis.password.2=yours

```

`第二步`：编辑项目中application.properties，配置如下：

```

####Security Manager
manager.username=admin
manager.password=admin

```

`第三步`：打包运行项目

执行maven命令：mvn clean package
target文件夹下生成的redis-admin.war即为项目部署文件，将其放置到对应服务器目录下，启动服务器即可。例如：tomcat的webapps文件夹下。

`第四步`：访问项目

启动web服务器后，访问地址：http://IP:[port]/redis-admin/redis，采用配置文件中manager.username和manager.password设置值进行登录。

![img-source-from-https://github.com/docker/dockercraft](https://github.com/docker/dockercraft/raw/master/docs/img/contribute.png?raw=true)
