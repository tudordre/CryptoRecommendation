#
mvn clean install



#DOCKER
# step 1 - build image
docker build -t crypto-app .

# step 2 - go to docker folder
cd docker

# step 3 - creates/starts the container
docker-compose up -d

# test
docker-compose ps
#a container should appear like : crypto-app   java -jar crypto-app.jar   Up      0.0.0.0:8080->8080/tcp
#you can send requests to port 8080

curl --location 'localhost:8080/prices'
curl --location 'localhost:8080/prices/btc'
curl --location 'localhost:8080/prices/btC?year=2022&month=1&period=1'




# K8S
# step 1 build docker image
docker build -t crypto-app .

# step 2 - deploy 2 instances of our service
kubectl apply -f k8s/myapp-deployment.yaml

# test
kubectl get pods # 2 pods should appear as ready

# step 3 - deploy service as nodeport.
kubectl apply -f k8s/myapp-nodeport-service.yaml

# test
kubectl get services
# service should appear like: crypto-k8s-svc   NodePort    10.108.124.133   <none>        8080:30589/TCP   6s
# you can send now request to node-ip:30589 via postman
# node ip on docker desktop is localhost

curl --location 'localhost:30589/prices'
curl --location 'localhost:30589/prices/btc'
curl --location 'localhost:30589/prices/btC?year=2022&month=1&period=1'