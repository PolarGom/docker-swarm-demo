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

3. 도커 스웜 매니저 워커의 포트 3개를 방화벽에서 열어준다.

```
# 2377: 클러스터 매니지먼트에서 사용(스웜 조인 등등..)
# 7946: 노드간의 통신
# 4789: 오버레이 네트워크 간 트래픽 통신
```

5. 생성된 명령어를 다른 도커(워커 노드)에서 실행을 한다.

```
# 아래 명령어 실행
[root@~]# docker swarm join --token SWMTKN-1-1vegfnz1vl2u182bd9yh74za2y8rkyd3xxpdeux4b9aqq9e7nq-exuu6tul11acyvwemfp0ogkil 192.168.0.175:2377

# 실행이 정상적으로 되면 아래와 같이 메시지가 출력된다.
This node joined a swarm as a worker.
```

6. 매니저 워커에서 아래와 같이 명령어를 실행하면 정상적으로 노드들이 붙은 것을 확인 할 수 있다.

```
[root@~]# docker node ls

ID                            HOSTNAME                STATUS    AVAILABILITY   MANAGER STATUS   ENGINE VERSION
rgshuhq1dj006yzbe8hdwbgrb     docker-desktop          Ready     Active                          20.10.8
xh3kyktkne7104luehaypa6qp *   localhost.localdomain   Ready     Active         Leader           20.10.11
```

7. Spring Demo 프로젝트의 Dockerfile 을 Build 한다.

```
# Spring Demo 프로젝트의 최상위 폴더로 후 도커파일을 빌드 한다.
[root@~]# docker build -t spring-demo:0.0.1 .
```

8. 빌드가 정상적으로 됐다면 아래의 명령어로 도커 이미지를 확인 할 수 있다.

```
[root@~]# docker images

# docker images 명령어 실행 결과
REPOSITORY           TAG           IMAGE ID       CREATED              SIZE
spring-demo          0.0.1         3c871095e4d1   About a minute ago   123MB
```