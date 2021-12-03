### 도커 스웜 데모

1. 도커 매니저 노드 생성하기([Init 사용방법](https://docs.docker.com/engine/reference/commandline/swarm_init/))

```
# 도커에서 내부 IP로만 통신을 할때 
[root@~]# docker swarm init 

# 혹은 물리적으로 분리된 서버끼리 통신을 할때 매니저 노드의 공인 IP 가 필요
[root@~]# docker swarm init --advertise-addr <공인IP>
```

2. 위의 명령어가 정상적으로 실행되면 아래와 같이 워커 노드에서 사용할 명령어가 생성된다.

```
# docker swarm init 후 실행 결과
docker swarm join --token SWMTKN-1-1vegfnz1vl2u182bd9yh74za2y8rkyd3xxpdeux4b9aqq9e7nq-exuu6tul11acyvwemfp0ogkil 192.168.0.175:2377
```

3. 도커 스웜 매니저 노드의 포트 3개를 방화벽에서 열어준다.

```
# 2377(TCP): 클러스터 매니지먼트에서 사용(스웜 조인 등등..)
# 7946(TCP, UDP): 노드간의 통신
# 4789(UDP): 오버레이 네트워크 간 트래픽 통신
```

5. 생성된 명령어를 다른 도커(워커 노드)에서 실행을 한다.

```
# 아래 명령어 실행
[root@~]# docker swarm join --token SWMTKN-1-1vegfnz1vl2u182bd9yh74za2y8rkyd3xxpdeux4b9aqq9e7nq-exuu6tul11acyvwemfp0ogkil 192.168.0.175:2377

# 실행이 정상적으로 되면 아래와 같이 메시지가 출력된다.
This node joined a swarm as a worker.
```

6. 매니저 노드에서 아래와 같이 명령어를 실행하면 정상적으로 노드들이 붙은 것을 확인 할 수 있다.

```
[root@~]# docker node ls

ID                            HOSTNAME                STATUS    AVAILABILITY   MANAGER STATUS   ENGINE VERSION
rgshuhq1dj006yzbe8hdwbgrb     docker-desktop          Ready     Active                          20.10.8
xh3kyktkne7104luehaypa6qp *   localhost.localdomain   Ready     Active         Leader           20.10.11
```

7. Docker Service 에서 사용 할 Spring Demo 프로젝트의 Dockerfile 을 Build 및 도커 허브 리포지토리에 업로드 한다.

```
# https://hub.docker.com/ 에서 계정을 만들고 리포지터리를 생성한다.
# Spring Demo 프로젝트의 최상위 폴더로 후 도커파일을 빌드 한다.
[root@~]# docker build -t <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1 .

# 도커 허브에 로그인을 한다.
[root@~]# docker login
Username:
Password:
Login Successed

# 도커 허브에 이미지를 업로드 한다.
[root@~]# docker push <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1

# 정상적으로 업로드가 완료되었다면 도커 허브 페이지의 리포지터리에서 확인 가능하다.
```

8. 매니저 노드 및 워커 노드에서 Spring Demo 이미지를 받는다.

```
[root@~]# docker pull <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1
```

9. 매니저 노드에서 서비스를 생성 한다.([서비스 명령어](https://docs.docker.com/engine/reference/commandline/service/), [서비스 옵션](https://docs.docker.com/engine/reference/commandline/service_create/))

```
# replica(생성할 컨테이너 갯수) 를 2개를 만들고, 포트는 9999, 이름은 spring-demo 로 생성한다.
[root@~]# docker service create --replicas 2 -p 9999:9999 --name spring-demo <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1

# 정상적으로 실행되면 아래와 같은 메시지가 출력된다.
suh43brc2pq4b5nte055b
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Service converged
```

10. 서비스가 정상적으로 생성되었는지 확인한다.

```
[root@~]# docker service ls
ID             NAME          MODE         REPLICAS   IMAGE                                                  PORTS
suh43brc2pq4   spring-demo   replicated   2/2        <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1   *:9999->9999/tcp
```

11. 서비스가 정상적으로 워커 노드에 배포가 되고 동작중인지 확인한다.

```
[root@~]# docker service ps spring-demo
ID             NAME            IMAGE                                                  NODE                    DESIRED STATE   CURRENT STATE            ERROR     PORTS
nl3nb09y377h   spring-demo.1   <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1   localhost.localdomain   Running         Running 35 seconds ago
xb3c2e5ew4sb   spring-demo.2   <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1   docker-desktop          Running         Running 27 seconds ago
```

12. 매니저 노드 및 워커 노드에서 docker ps 명령어를 실행하면 매니저 노드 서비스에서 구동한 것을 확인 할 수 있다.

```
# 위의 11번 ID 와 ps 명령어의 NAME 에 해시 코드가 동일한 것을 확인 가능하다.
# 매니저 노드
[root@~]# docker ps
CONTAINER ID   IMAGE                                                   COMMAND               CREATED          STATUS          PORTS     NAMES
0c749bb78c9a   <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1   "java -jar app.jar"   54 minutes ago   Up 54 minutes             spring-demo.2.nl3nb09y377hfdue6wqbp3p3j

# 워커 노드
[root@~]@ docker ps 
CONTAINER ID   IMAGE                                                  COMMAND               CREATED          STATUS          PORTS     NAMES
afd30091c75e   <도커허브 아이디>/<Repository 이름>:spring-demo-0.0.1   "java -jar app.jar"   38 minutes ago   Up 38 minutes             spring-demo.1.xb3c2e5ew4sbtx13iwwwc2ao6
``` 