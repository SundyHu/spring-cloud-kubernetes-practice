# Kubernetes部署Traefik Ingress控制器（1.7.12）

**前言：**
Ingress可以为Kubernetes集群外部访问集群内部Service提供配置，Ingress Controller控制器可以充当网关，提供路由策略、负载均衡流量、SSL，并
提供基于名称的虚拟主机，提供统一的入口供流量涌入，是一个边缘路由器或额外的前端。这里使用Traefik来充当Ingress Controller控制器，下面将介绍如何在 Kubernetes中部署Traefik这个过程。
> Ingress不会暴露任意端口或协议。将除HTTP和HTTPS之外的服务暴露给互联网通常使用Service.Type=NodePort或Service.Type=LoadBalancer类型的服务。

系统环境：

- Kubernetes版本：1.14.0
- traefik版本：1.7.12

Github示例部署文件

- 部署文件github地址：[示例文件](https://github.com/my-dlq/blog-example/tree/master/kubernetes/traefik-v1.7-deploy)

## 一、Ingress介绍

    对于基于HTTP的服务，不同的URL对应不同的后端服务或者虚拟服务器（Virtual Host），这些应用层的转发无法通过Kubernetes Service机制实现，所以以从Kubernetes 1.1版本开始新增Ingress资源。

Ingress 是一种将不同的URL的访问请求转发到不同的Service实现HTTP层业务路由机制。 Kubernetes
Ingress包含Ingress策略和Ingress控制器两部分组成，Ingress策略是配置路由规则，而Ingress控制器则是将服务进行转发。Ingress
Controller基于Ingress规则将客户端请求转发到Service对应的后端Endpoints（Pod）上，这样会跳过Kube-Proxy转发功能避免增加开销。

## 二、Traefik介绍

    Traefik是一个轻松与微服务配合且流行的Http反向代理和负载均衡器。Traefik与现有的基础架构组件（如Docker、Swarm、Kubernetes、Marathon、Consul、Etcd、Rancher、Amazon ECS...）集成，并自动动态配置。在你的控制器上指向Traefik应该是你唯一

需要配置的步骤。

## 三、部署Ingress控制器Traefik

    在定义Ingress之前我先首先拥有Ingress控制器，以实现为后端所有服务统一入口。Ingress控制器也是以Pod形式运行在Kubernetes集群中，监控API Server和`/ingress`接口后端的backend services，如果Service发生变化，则Ingress控制器将自动更新其转发规则，

下面我们开始部署Traefik到Kubernetes集群。 步骤简介：

- 创建Traefik配置文件，并将其以ConfigMap方式挂载到Kubernetes集群中。
- 生成CA证书，并将其以Secret方式挂载到Kubernetes集群中。
- 创建Traefik Label设置到Kubernetes节点（相当于设置了Label的节点是Ingress入口）。
- 创建Traefik ServiceAccount以提供Traefik一定权限。
- 创建Traefik控制器。

1、Traefik两种部署方式介绍 在Kubernetes下有两种部署Kubernetes的方式：

- Deployment
- DaemonSet

两种部署方式的区别
在Kubernetes利用DaemonSet方式部署的应用会在设置对应Label的Kubernetes节点上部署一个Pod，每当新增Kubernetes节点后只要对新增的Kubernetes节点设置对应Label就可将Traefik扩展到
该节点。但是也是因为如此，一个节点只能启用一个Pod，这样对于Traefik的扩展非常不利，如果将外部流量大量指向某个节点，那么很可能会致使该节点的Traefik崩溃，但也是非常方便限制两个Traefik Pod
起在不在同一节点上。使用Deployment方式的部署一般是部署无状态应用的，所以通过这种方式能够很轻松的扩展Traefik应用副本数。
如果用DaemonSet方式部署Traefik就可与使用该“NET_BIND_SERVICE”功能，这将允许它绑定到每个主机上的`80/443/etc端口`。这将允许绕过kube-proxy，并减少流量跳跃。而Deployment这种
部署方式是通过kube-proxy代理将流量转发到Traefik，所以利用集群中所有节点的IP地址访问Traefik，但是每新增一个Kubernetes节点后，如果想在该节点上设置Traefik，那么必须更改Traefik
Deployment配置， 增加副本数且使其在新节点上调度。 所以两种部署方式区别大概如下：

- DaemonSet方式能够确定有哪些节点在运行Traefik，可以确定的知道后端IP，但是不能方便的伸缩。Deployment可以方便的伸缩，但是不能确定有哪些节点在运行Traefik，所以不能确定的知道后端IP。
- 使用Deployment时可伸缩性可以更好，DaemonSet方式在新增节点时更易扩展。
- DaemonSet确保每个节点只有一个Traefik Pod在运行。而Deployment可以有多个副本。如果要确保两个Pod不在同一节点上，则可以设置成Deployment。
- DeamonSet可以使用`NET_BIND_SERVICE`功能，这将允许它帮定到每个主机上的端口`80/443/etc`。这将允许绕过kube-proxy，并减少流量跳跃。

这两种方式可以根据自己的需求选择其一即可。这里将介绍如何通过DeaemonSet方式部署Traefik Ingress。

2、创建Traefik配置文件

    为了方便配置Traefik，一般情况下将Traefik配置文件放置到容器外，这里以ConfigMap方式配置文件存入Kubernetes集群，然后通过挂载方式将ConfigMap挂入Traefik容器中。

创建`traefik.toml`文件

```toml
# traefik.toml
debug = true
InsecureSkipVerify = true
defaultEntryPoints = ["http","https"]
[entryPoints]
  [entryPoints.http]
    address = ":80"
    compress = true
  [entryPoints.https]
    address = ":443"
    compress = true
    [entryPoints.https.tls]
      [[entryPoints.https.tls.certificates]]
        CertFile = "/ssl/tls.crt"
        KeyFile = "/ssl/tls.key"
  [entryPoints.traefik]
    address = ":8080"
[kubernetes]
[traefikLog]
  format = "json"
  #filePath = "/data/traefik.log"
[accessLog]
  #filePath = "/data/access.log"
  format = "json"
  [accessLog.filters]
    retryAttempts = true
    minDuration = "10ms"
  [accessLog.fields]
    defaultMode = "keep"
    [accessLog.fields.names]
    "ClientUsername" = "drop"
    [accessLog.fields.headers]
      defaultMode = "keep"
      [accessLog.fields.headers.names]
        "User-Agent" = "redact"
        "Authorization" = "drop"
        "Content-Type" = "keep"
[api]
  entryPoint = "traefik"
  dashboard = true
```

- Traefik配置文件中设置该Traefik Ingress允许以Http、Https方式进入；
- 设置SSL统一的CA证书文件地址为"/ssl/tls.crt","/ssl/tls.key"方便后续通过ConfigMap方式将CA证书文件挂入其中。

3、将Traefik配置文件挂载到ConfigMap

- n指定程序启动的Namespace。
- --from-file读取文件生成ConfigMap
``bash
  kubectl create configmap traefik-config --from-file=./traefik.toml -n kube-system  
``

4、设置CA证书

这里设置Traefik统一的CA证书文件，如果已经拥有证书文件可以直接使用，或者也可以用openssl程序生成自签名证书。

**生成自签名CA证书**

openssl工具生成CA自签名的证书
```bash
openssl req -newkey rsa:2048 -nodes -keyout tls.key -x509 -days 9999 -out tls.crt
```

生成secret到Kubernetes
```bash
kubectl create secret generic traefik-ui-tls-cert --from-file=tls.crt --from-file=tls.key -n kube-system
```

5、给节点设置Label

由于是Kubernetes DeamonSet这种方式部署Traefik，所以需要提前给节点设置Label，这样当程序部署时Pod会自动调度到设置Label的节点上。

节点设置Label标签
- 格式：`kubectl label nodes [节点名] [key=value]`
```bash
kubectl label nodes k8s-master-2-11 IngressProxy=true
```

查看节点是否设置Label成功
```bash
kubectl get nodes --show-labels

NAME            STATUS ROLES  VERSION  LABELS
k8s-master-2-11 Ready  master v1.14.0  IngressProxy=true,kubernetes.io/hostname=k8s-master-2-11,kubernetes.io/os=linux,node-role.kubernetes.io/master=
k8s-node-2-12   Ready  <none> v1.14.0  kubernetes.io/hostname=k8s-node-2-12,storagenode=glusterfs
k8s-node-2-13   Ready  <none> v1.14.0  kubernetes.io/hostname=k8s-node-2-13,storagenode=glusterfs
k8s-node-2-14   Ready  <none> v1.14.0  kubernetes.io/hostname=k8s-node-2-14,storagenode=glusterfs
```

可以看到已经设置上了Label

## 6、创建Traefik服务帐户与角色权限

    Kubernetes在1.6版本中引入了基于角色的访问控制（RBAC）策略，方便对Kubernetes资源和API进行细粒度控制。所以这里提前创建好Traefik ServiceAccount并分配一定的权限。

创建traefik-rbac.yaml文件
```yaml
# traefik-rbac.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: traefik-ingress-controller
  namespace: kube-system

---

kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1beta1
metadata:
  name: traefik-ingress-controller
rules:
  - apiGroups: [""]
    resources: ["services","endpoints","secrets"]
    verbs: ["get", "watch", "list"]
  - apiGroups: ["extensions"]
    resources: ["ingresses"]
    verbs: ["get", "watch", "list"]
  - apiGroups: ["extensions"]
    resources: ["ingresses/status"]
    verbs: ["update"]

---

kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1beta1
metadata:
  name: traefik-ingress-controller
subjects:
  - kind: ServiceAccount
    name: traefik-ingress-controller
    namespace: kube-system
roleRef:
  kind: ClusterRole
  name: traefik-ingress-controller
  apiGroup: rbac.authorization.k8s.io
```

创建Traefik RBAC
```yaml
kubectl apply -f traefik-rbac.yaml
```

## 7、创建Traefik Ingress Controller
创建traefik.yaml文件
这里设置部署 Traefik 的 yaml 文件，里面包含 Traefik 的 Service 和 DaemonSet。Service 里面需要设置三个端口，分别为 80、443、8080，这三个端口分别对应 http端口、https端口、traefik admin控制台端口。DaemonSet 里面设置容器也暴露三个端口，其中 “80/443” 是使用特权端口的守护进程，阐述了静态（非NodePort）hostPort 绑定，这样相当于暴露当前 Traefik Pod 所在节点 IP，外部流量能通过该节点 IP 进入 Traefik Ingress。而 8080 是供 Traefik Dashboard 用的控制台端口，可以通过该端口访问 Traefik 控制台。

例如这里设置 Traefik 启动到 “k8s-master-2-11”，此节点的 IP 为 “192.168.2.11” ，这样外部想通过 Traefik Ingress 访问集群服务，必须通过该 IP 地址而不应通过 Kubernetes 集群 IP，相当于该节点充当了 Kubernetes 入口。

```yaml
kind: Service
apiVersion: v1
metadata:
  name: traefik-ingress-service
  namespace: kube-system
spec:
  selector:
    k8s-app: traefik-ingress-lb
  ports:
    - protocol: TCP
      port: 80
      name: http
    - protocol: TCP
      port: 443
      name: https
    - protocol: TCP
      port: 8080
      name: admin
      
---

apiVersion: extensions/v1beta1
kind: DaemonSet
metadata:
  name: traefik-ingress-controller
  namespace: kube-system
  labels:
    k8s-app: traefik-ingress-lb
spec:
  template:
    metadata:
      labels:
        k8s-app: traefik-ingress-lb
        name: traefik-ingress-lb
    spec:
      serviceAccountName: traefik-ingress-controller
      terminationGracePeriodSeconds: 60
      containers:
      - image: traefik:1.7.12
        name: traefik-ingress-lb
        ports:
        - name: http
          containerPort: 80
          hostPort: 80                   #hostPort方式，将端口暴露到集群节点
        - name: https
          containerPort: 443
          hostPort: 443                  #hostPort方式，将端口暴露到集群节点
        - name: admin
          containerPort: 8080
        securityContext:
          capabilities:
            drop:
            - ALL
            add:
            - NET_BIND_SERVICE
        args:
        - --api
        - --kubernetes
        - --logLevel=INFO
        - --configfile=/config/traefik.toml
        volumeMounts:
        - mountPath: "/ssl"
          name: "ssl"
        - mountPath: "/config"
          name: "config"
      volumes:
        - name: ssl
          secret:
            secretName: mydlqcloud-traefik-tls  
        - name: config
          configMap:
            name: traefik-config 
      tolerations:              #设置容忍所有污点，防止节点被设置污点
      - operator: "Exists"
      nodeSelector:             #设置node筛选器，在特定label的节点上启动
        IngressProxy: "true"
```

部署Traefik
```bash
kubectl apply -f traefik.yaml
```

查看Traefik资源
```yaml
kubectl get daemonset,service,pod -o wide -n kube-system
```

到这里 Traefik 控制器已经部署完成，下面将同过暴露 Traefik Dashboard Ingres 来进行 Igress 配置示例。

## 配置Ingress访问策略

1、Ingress规则配置简介
一个常规的路由策略如下所示，这里根据注解详细描述 Ingress 访问配置。
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: test-ingress                            #Ingress 资源名称
  annotations:
    kubernetes.io/ingress.class: traefik        #指定用 traefik 控制器,如果为 nginx 控制器则设置为 nginx
spec:
  rules:
  - host: cloud.mydlq.club                      #设置 host 匹配规则，外部流量访问该 host 时候进行代理操作
    http:
      paths:
      - path: /traefik                          #设置 path，当 host 匹配后进行 path 匹配，根据 path 不同转发不同的服务 
        backend:
          serviceName: traefik-ingress-service  #设置 service 名称，和要跳转的 service 一致
          servicePort: 8080                     #设置 service 端口
```

2、创建Traefik Dashboard Ingress

上面部署的 Traefik 控制器默认有控制台服务，即 Traefik Service 的 8080 端口。

这里配置一个 Ingress， 将这个 Dashboard 设置域名为 http://cloud.mydlq.club/traefik 暴露出去，其它服务也是类似这种方法将 Service 暴露出去。

创建traefik-dashboard-ingress.yaml文件
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: traefik-dashboard
  namespace: kube-system
  annotations:
    kubernetes.io/ingress.class: traefik                        #指定用 traefik 控制器
    traefik.frontend.rule.type: PathPrefixStrip                 #跳转后端时忽略 path
    traefik.ingress.kubernetes.io/frontend-entry-points: http   #指定只能以 http，方式访问，也可以设置 https
spec:
  rules:
  - host: cloud.mydlq.club                                      #设置 host
    http:
      paths:
      - path: /traefik                                          #设置 path
        backend:
          serviceName: traefik-ingress-service                  #设置 service 名称，和要跳转的 service 一致
          servicePort: 8080                                     #设置 service 端口
```




## 参考
http://www.mydlq.club/article/17/
