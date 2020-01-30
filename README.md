### 参数异常处理
原先都是使用@valid + BindingResult：
``` java
    @PostMapping("/subjects")
    public CommonResult insertSubjects(@RequestBody @Valid SubjectParams subjectParams,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return CommonResult.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        teacherSubjectService.insertSubjects(subjectParams);
        return CommonResult.success();
    }
```
现在使用同一处理参数异常（不再使用bindingResult参数）：
``` java
    @PostMapping("/subjects")
    public CommonResult insertSubjects(@RequestBody @Valid SubjectParams subjectParams){
        teacherSubjectService.insertSubjects(subjectParams);
        return CommonResult.success();
    }
```
``` java
    //处理参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handler(MethodArgumentNotValidException e) {
        return CommonResult.fail(400, e.getBindingResult().getFieldError().getDefaultMessage());
    }
```
RuntimeException是非受检异常，可以不用在方法中捕获。


### 添加跨域配置
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```


### 使用elasticsearch搜索题目
##### 在服务器上使用docker下载elasticsearch镜像
```aidl
docker pull elasticsearch:6.4.3
```
##### 运行elasticsearch
```
docker run --name myelasticsearch -d -p 9200:9200 -p 9300:9300 elasticsearch:6.4.3
```
可能会启动失败，此时可以查看下日志
```aidl
docker logs -f [容器id]
```
一般都是因为内存不足
在/var/lib/docker/overlay2/08783caa23e675d67e31843d8bfdd793ea61397bd7077de641188d85049b7bad/diff/usr/share/elasticsearch中修改jvm内存，
```aidl
-Xms512m
-Xmx512m
```
此时虽然启动成功了，但是运行一会儿就自动又关闭了，查看日志

 max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
 
 输入：
 sysctl -w vm.max_map_count=262144
 
 然后重启elasticsearch容器。
 
 最后别忘记在服务器上打开9200端口，然后输入ip+9200端口
 ```aidl
{
  "name" : "RjcVPr0",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "1LOd01hpSo-_emOum4vCgQ",
  "version" : {
    "number" : "6.4.3",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "fe40335",
    "build_date" : "2018-10-30T23:17:19.084789Z",
    "build_snapshot" : false,
    "lucene_version" : "7.4.0",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```
表示elasticseach开启成功

程序启动时，会提醒找不到分词器

安装分词器：
```aidl
docker exec -it elasticsearch /bin/bash

./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.4.3/elasticsearch-analysis-ik-6.4.3.zip

```
安装成功后，会在plugins文件夹下生成analysis_ik

再次启动程序，会提示说找不到9300端口，可是明明就打开了啊，然后查了百度，说是要打开9200到9400的所有端口
在阿里云上设置玩后，再次启动，终于可以了。

### elasticsearch 反序列化localdatetime失败，不知道咋搞。。。
```css
"updateTime": {
-"chronology": {
"calendarType": "iso8601",
"id": "ISO"
},
"dayOfMonth": 14,
"dayOfWeek": "THURSDAY",
"dayOfYear": 318,
"hour": 14,
"minute": 52,
"month": "NOVEMBER",
"monthValue": 11,
"nano": 0,
"second": 35,
"year": 2019
}
```

mysql和elasticserach数据同步
1.在docker中进入mysql
```
docker exec -it mysql bash
```
2.查看是否打开binlog增量备份功能
```aidl
mysql -uroot -p

show variables like '%log_bin%';

```
如果没有开启的话，需要在my.cnf配置中添加如下配置(其中server-id可以根据情况设置，这里设置为1，log-bin为日志位置，一定要给日志写的权限，不然会报错，binlog_format为模式，这里必须为ROW)：

root@0eee85c592f9:/etc/mysql/mysql.conf.d# vi mysqld.cnf 

```aidl
server-id=1
log-bin=/usr/local/mysql-log/mysql-bin.log
binlog_format=row
```

## 自动组卷算法
 https://www.cnblogs.com/artwl/archive/2011/05/19/2051556.html
 

## beanutil
import org.springframework.beans.BeanUtils; spring自带的beanutils
使用copyProperties时，只会复制类型和变量名字都匹配的字段
使用hutool的beanutil时，只要变量名字相同，就会复制，当类型不同时，就可能出现类型转换错误。



## mybatis返回自增id
```css
  <insert id="insertSelective" parameterType="com.yb.onlineexamserver.mbg.model.Paper"
  useGeneratedKeys="true" keyProperty="id">
```
使用 useGeneratedKeys 和 keyProperty两个配置即可。id会直接映射到实体类上，使用getId即可。