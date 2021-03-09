# Spring Cloud集成Kubernetes

## 1、使用Kubernetes原生服务发现组件

### 添加依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-kubernetes-fabric8</artifactId>
    </dependency>
    <!-- 任选其中即可 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-kubernetes-client</artifactId>
    </dependency>
</dependencies>
```

    其余与使用Nacos及Eureka一样，不同的是不需要配置服务发现服务端地址，部署yaml文件，需要配置RBAC，否则出现没有权限访问的情况：

```yaml
# 创建在pod只读角色
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: pod-reader-cluster
rules:
  - apiGroups: [ "" ] # "" 指定核心 API 组
    resources: [ "pods","endpoints","services","configmaps" ]
    verbs: [ "get", "watch", "list" ]
---
apiVersion: rbac.authorization.k8s.io/v1
# 此角色绑定使得用户 "jane" 能够读取 "default" 命名空间中的 Pods
kind: ClusterRoleBinding
metadata:
  name: read-pods-cluster
subjects:
  - kind: Group
    name: system:serviceaccounts
    apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole #this must be Role or ClusterRole
  name: pod-reader-cluster # 这里的名称必须与你想要绑定的 Role 或 ClusterRole 名称一致
  apiGroup: rbac.authorization.k8s.io
```

## 2、使用ConfigMap作为配置中心

    注意:  不推荐在生产环境下使用此种方式，所有配置均需使用YAML更新，操作较Nacos与Apollo相比不太方便。

### 定义配置文件

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: demo-service
  namespace: default
data:
  application.yml: |-
    demo:
      name: "This is my test!"
```

### 引入依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-kubernetes-client-config</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
</dependencies>
```

### 创建bootstrap.yaml文件

```yaml
management:
  endpoint:
    restart:
      enabled: true
    health:
      enabled: true
    info:
      enabled: true
spring:
  application:
    name: @artifactId@
  cloud:
    kubernetes:
      config:
        enabled: true
        sources:
          - name: ${spring.application.name}
            namespace: default
      reload:
        enabled: true
        mode: polling
        period: 1000
```

### 修改application.yml

```yaml
server:
  port: 9001

management:
  server:
    port: 9001
  endpoints:
    web:
      base-path: '/'
      exposure:
        include: '*'
  endpoint:
    metrics:
      enabled: true
    health:
      show-details: always
logging:
  level:
    root: debug
```

### 添加RefreshScope注解

```java

@Configuration
@ConfigurationProperties(prefix = "demo")
@Data
@RefreshScope
public class DemoConfig {

    private String name;
}
```

## 3、敏感配置项的处理

    对于一些敏感配置项我们可以使用Kubernetes的Secret来处理，将其写入Secret中，默认使用Base64进行加密处理。

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db_secret
data:
  username: dXNlcg==
  password: cDQ1NXcwcmQ=
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-service
  namespace: default
spec:
  selector:
    matchLabels:
      app: demo-service
  replicas: 1
  template:
    metadata:
      labels:
        app: demo-service
    spec:
      containers:
        - name: demo-service
          image: demo-service:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 9001
          env:
            - name: MYSQL_USERNAME
              valueFrom:
                secretKeyRef:
                  name: db_secret
                  key: username
```
