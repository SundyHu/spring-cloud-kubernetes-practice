    在Kubernetes里，你可以通过服务名去访问相同Namespace里的服务，然后服务可以解析到对应的Pod，从而再由Pod转到对应的容器里，我们可以认为这个过程有两个port的概念，Service Port就是服务的Port，
在Kubernetes配置文件里用Port表示，还有一个是Pod和容器的Port，用TargetPort表示，其中Pod和容器的Port你可以认为它是一个。
## 多Namespace的Service场景
    我们通常会把MySQL、Redis、RabbitMQ、MondoDB这些公用组件放在一个Naqmespace里，或者多个公用组件都有自己的Namespace，而你的业务组件会统一放在自己的Namespace里，这时就涉及到了跨Namespace
的数据通讯问题。
### Kubernetes的服务名DNS解析
    Kubernetes目前使用的Kube-DNS来实现集群内部的Service DNS记录解析。默认情况下`/etc/resolv.conf`里，它的内容是统一的格式。
```bash
nameserver 10.96.0.10
search sc-kubernetes.svc.cluster.local svc.cluster.local cluster.local
options ndots:5
```

    search domain列表默认情况下，它只包含本地域名。这可以通过在search关键字后面列出所需的域搜索路径来添加。Kubernetes为每个容器配置默认是`${namespace}.svc.cluster.local`
`svc.cluster.local` `cluster.local`。在一次dns域名查询时，将会尝试使用每个search domain依次搜索少于ndots点（默认值为1）的解析器查询，直到找到匹配项。对于具有多个子域的环境，建议调整
选项`ndots:n`，以避免`man-in-the-middle`攻击和`root-dns-servers`不必要通信。
    `ndots:5`这个我们可以把它理解成服务名dns解析的层次，例如`{服务名}`是一级，而`{服务名}`.`{命名空间}`为二层，`{服务名}`.`{命名空间}`.svc.cluster.local是第三层，上面的配置一共有5层，同时也开启了5层，这样
做可以保证最大限度的找到你的服务，但对于解析的性能是有影响的。
> 请注意，如果搜索域对应的服务不是本地的，那么这个查询过程会很慢，并且会产生大量的网络流量。如果其中一个搜索域没有可用的服务器，则查询超进。

### 同一集群namespace访问
    如果你要连接namespace是redis的，服务名是`redis-master`的服务，你可以这样去配置你的连接：
```yaml
spring:
  profiles: redis-prod
  redis:
    host: redis-master.redis
    port: 6379
    password: 123456
    database: 1
```
    它采用了服务名+命名空间的格式，如果是相同的namespace，可以直接使用服务名来解析。