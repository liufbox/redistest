FROM openjdk:11


RUN apt-get clean && apt-get update &&\
	  apt-get -y install vim &&\
	  apt-get -y install procps &&\
	  apt-get -y install net-tools &&\
	  apt-get -y install inetutils-ping &&\
      apt-get -y install tcpdump


ARG version=0.0.1-SNAPSHOT
ARG jar_file=./target/demo3-$version.jar


COPY $jar_file /opt/app/demo3.jar


EXPOSE 8080


#CMD java -Djava.security.egd=file:/dev/./urandom -jar /opt/netbar_service.jar
ENTRYPOINT ["java","-jar","/opt/app/demo3.jar"]
