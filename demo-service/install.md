k8s.gcr.io/kube-apiserver:v1.15.3
k8s.gcr.io/kube-controller-manager:v1.15.3
k8s.gcr.io/kube-scheduler:v1.15.3
k8s.gcr.io/kube-proxy:v1.15.3
k8s.gcr.io/pause:3.1
k8s.gcr.io/etcd:3.3.10
k8s.gcr.io/coredns:1.3.1
## 拉取镜像
```bash

docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/kube-apiserver:v1.15.3
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/kube-apiserver:v1.15.3 k8s.gcr.io/kube-apiserver:v1.15.3
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/kube-apiserver:v1.15.3

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/kube-controller-manager:v1.15.3
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/kube-controller-manager:v1.15.3 k8s.gcr.io/kube-controller-manager:v1.15.3
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/kube-controller-manager:v1.15.3

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/kube-scheduler:v1.15.3
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/kube-scheduler:v1.15.3 k8s.gcr.io/kube-scheduler:v1.15.3
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/kube-scheduler:v1.15.3

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/kube-proxy:v1.15.3
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/kube-proxy:v1.15.3 k8s.gcr.io/kube-proxy:v1.15.3
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/kube-proxy:v1.15.3

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/pause:3.1
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/pause:3.1 k8s.gcr.io/pause:3.1
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/pause:3.1

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/etcd:3.3.10
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/etcd:3.3.10 k8s.gcr.io/etcd:3.3.10
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/etcd:3.3.10

sudo docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/coredns:1.3.1
sudo docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/coredns:1.3.1 k8s.gcr.io/coredns:1.3.1
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/coredns:1.3.1

sudo kubeadm init --
```

```shell
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://h5fkwf4f.mirror.aliyuncs.com"],
  "exec-opts": ["native.cgroupdriver=systemd"]
}
EOF


kubeadm init --kubernetes-version=1.15.3 --apiserver-advertise-address=192.168.2.107 --pod-network-cidr=10.244.0.0/16
```

```bash

  Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

  You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

  kubeadm join 192.168.2.107:6443 --token hql5xh.nlnwk8ex6lyx3fzu \
  --discovery-token-ca-cert-hash sha256:4e217cc78020915ebf4039f1ea47a9a6ebab646ac840ce25c020699f5b05220b

```