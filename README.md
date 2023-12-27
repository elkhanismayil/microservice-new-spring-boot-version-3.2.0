Before you start this service, we need to have that certificate on that volume.
In project root folder we run following commands: (one by one)

1.docker volume create vault-volume

2.docker run -v vault-volume:/data --name helper busybox true

3.docker cp ./vault-volume helper:/data

4.docker rm helper

Now we can start our service with following command:

docker-compose -f docker-compose.dev.yml up --build -d