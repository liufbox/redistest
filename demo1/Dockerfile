FROM openjdk:11

RUN apt-get clean && apt-get update &&\
	  apt-get -y install vim &&\
	  apt-get -y install procps &&\
	  apt-get -y install net-tools &&\
	  apt-get -y install inetutils-ping &&\
      apt-get -y install tcpdump


ARG version=0.0.1-SNAPSHOT
ARG jar_file=./target/demo1-$version.jar


COPY $jar_file /opt/app/demo1.jar


EXPOSE 8080

ENTRYPOINT ["java","-jar","/opt/app/demo1.jar"]
