FROM java:8
WORKDIR /
ADD build/libs/grpc-aggregator-1.0-SNAPSHOT.jar grpc-aggregator.jar
EXPOSE 8080
CMD java -jar grpc-aggregator.jar