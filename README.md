# Pre-requirements
- minikube

# Step by step
## create namespace
`$ kubectl create namespace zj`
  
## deploy ConfigMap
`$ kubectl -n zj apply -f yaml/redis-config-map.yaml`

## create pv
`$ kubectl -n zj apply -f yaml/redis-pv.yaml`

## create headless-service
`$ kubectl -n zj apply -f yaml/redis-headless-service.yaml`

## create statefulSet
`$ kubectl -n zj apply -f yaml/redis-stateful-set.yaml`

## create cluster
```
# remember to make shell script executable
$ chmod a+x create-redis-cluster.sh

# execute the script to create cluster
$ ./create-redis-cluster.sh`
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 10.244.0.9:6379 to 10.244.0.5:6379
Adding replica 10.244.0.10:6379 to 10.244.0.6:6379
Adding replica 10.244.0.8:6379 to 10.244.0.7:6379
M: 7875654e4750d7c1e7914c86917e1c94d4a29d94 10.244.0.5:6379
   slots:[0-5460] (5461 slots) master
M: 60fe03c197845d78ad6d4af4ba292f9cb6d3bf74 10.244.0.6:6379
   slots:[5461-10922] (5462 slots) master
M: 3236c30531dd499ac15e53a08019eecf2fde5d63 10.244.0.7:6379
   slots:[10923-16383] (5461 slots) master
S: 3f7fc12f212c1ae31c7aa6e0658151c2797d779c 10.244.0.8:6379
   replicates 3236c30531dd499ac15e53a08019eecf2fde5d63
S: ae31ad5276a5803089c599a10ac488391bdff0af 10.244.0.9:6379
   replicates 7875654e4750d7c1e7914c86917e1c94d4a29d94
S: 9009c1c20589ba317efb4b47015a632b61705217 10.244.0.10:6379
   replicates 60fe03c197845d78ad6d4af4ba292f9cb6d3bf74
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join
.
>>> Performing Cluster Check (using node 10.244.0.5:6379)
M: 7875654e4750d7c1e7914c86917e1c94d4a29d94 10.244.0.5:6379
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: ae31ad5276a5803089c599a10ac488391bdff0af 10.244.0.9:6379
   slots: (0 slots) slave
   replicates 7875654e4750d7c1e7914c86917e1c94d4a29d94
M: 60fe03c197845d78ad6d4af4ba292f9cb6d3bf74 10.244.0.6:6379
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 9009c1c20589ba317efb4b47015a632b61705217 10.244.0.10:6379
   slots: (0 slots) slave
   replicates 60fe03c197845d78ad6d4af4ba292f9cb6d3bf74
S: 3f7fc12f212c1ae31c7aa6e0658151c2797d779c 10.244.0.8:6379
   slots: (0 slots) slave
   replicates 3236c30531dd499ac15e53a08019eecf2fde5d63
M: 3236c30531dd499ac15e53a08019eecf2fde5d63 10.244.0.7:6379
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
```

## create service
`$ kubectl -n zj apply -f yaml/redis-service.yaml`

## build sample spring-boot application
`$ ./gradlw clean build assemble`

## build sample spring-boot application image
```
# enable minikube to load podman registry
$ eval $(minikube podman-env)

# build image with podman-remote
$ podman-remote build -f Dockerfile -t redis-sample:0.0.1 .

# check if image is accessible from minikube
$ minikube image
localhost/redis-sample:0.0.1
k8s.gcr.io/pause:3.2
k8s.gcr.io/kube-scheduler:v1.20.7
k8s.gcr.io/kube-proxy:v1.20.7
k8s.gcr.io/kube-controller-manager:v1.20.7
k8s.gcr.io/kube-apiserver:v1.20.7
k8s.gcr.io/etcd:3.4.13-0
k8s.gcr.io/coredns:1.7.0
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/redis:6.2.4-alpine3.13
docker.io/library/redis:6.0.6
docker.io/kubernetesui/metrics-scraper:v1.0.4
docker.io/kubernetesui/dashboard:v2.1.0
docker.io/kindest/kindnetd:v20210326-1e038dc5
docker.io/adoptopenjdk/openjdk8:jdk8u292-b10-alpine-slim
```

## deploy sample spring-boot application
`$ kubectl -n zj apply -f yaml/deployment-test-redis-sample.yaml`

## deploy sample spring-boot service
`$ kubectl -n zj apply -f yaml/service-test-redis-sample.yaml`

## run the sample spring-boot service
```
# find the service name
$ kubectl -n zj get service
NAME                    TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
app-service             NodePort    10.98.20.178     <none>        80:31959/TCP   22m
redis-headless-server   ClusterIP   None             <none>        6379/TCP       24m
redis-service           ClusterIP   10.108.149.169   <none>        6379/TCP       22m

# run 
$ minikube -n zj service app-service
|-----------|-------------|-------------|---------------------------|
| NAMESPACE |    NAME     | TARGET PORT |            URL            |
|-----------|-------------|-------------|---------------------------|
| zj        | app-service | http/80     | http://192.168.49.2:31959 |
|-----------|-------------|-------------|---------------------------|
🎉  Opening service zj/app-service in default browser...
```

## test
open browser and type the url `http://192.168.49.2:31959/helloWorld` and repeat multiple times...

# or... get minikube ip
```
$ minikube ip
192.168.49.2

# and test with curl
$ curl 192.168.49.2:31959/helloWorld
```

## read the log
```
# find the pod name
$ kubectl -n zj get pod
NAME                                     READY   STATUS    RESTARTS   AGE
redis-app-0                              1/1     Running   0          3m7s
redis-app-1                              1/1     Running   0          3m5s
redis-app-2                              1/1     Running   0          3m4s
redis-app-3                              1/1     Running   0          3m
redis-app-4                              1/1     Running   0          2m56s
redis-app-5                              1/1     Running   0          2m55s
test-redis-deployment-68cc687879-thq4c   1/1     Running   0          69s

# get the log
$ kubectl -n zj logs test-redis-deployment-555457d995-zxl78 --tail=10
2021-06-22 14:19:30.105  INFO 1 --- [nio-8080-exec-9] org.example.service.RedisService         : getName start
2021-06-22 14:19:30.105  INFO 1 --- [nio-8080-exec-9] org.example.service.RedisService         : ===== cache missed, gonna create value =====
2021-06-22 14:19:30.105  INFO 1 --- [nio-8080-exec-9] org.example.service.RedisService         : key: helloWorld
2021-06-22 14:19:30.106  INFO 1 --- [nio-8080-exec-9] org.example.service.RedisService         : ===== value created, will push value into cache =====
2021-06-22 14:19:30.106  INFO 1 --- [nio-8080-exec-9] org.example.service.RedisService         : getName end
2021-06-22 14:19:30.108  INFO 1 --- [nio-8080-exec-9] o.e.controller.TestRedisController       : testCache end
2021-06-22 14:19:34.190  INFO 1 --- [io-8080-exec-10] o.e.controller.TestRedisController       : testCache start
2021-06-22 14:19:34.193  INFO 1 --- [io-8080-exec-10] o.e.controller.TestRedisController       : testCache end
2021-06-22 14:19:35.463  INFO 1 --- [nio-8080-exec-1] o.e.controller.TestRedisController       : testCache start
2021-06-22 14:19:35.466  INFO 1 --- [nio-8080-exec-1] o.e.controller.TestRedisController       : testCache end
```