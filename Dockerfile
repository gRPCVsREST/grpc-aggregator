FROM java:8
WORKDIR /
ADD build/libs/grpc-aggregator-1.0-SNAPSHOT.jar grpc-aggregator.jar
EXPOSE 8080
CMD java -Djava.rmi.server.hostname=localhost \
-Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.rmi.port=9999 \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.local.only=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-jar grpc-aggregator.jar
