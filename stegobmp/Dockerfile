FROM maven:3-jdk-10 AS builder

WORKDIR /stego
COPY pom.xml /stego/pom.xml

# RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline
RUN mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline dependency:resolve-plugins dependency:resolve

COPY src /stego/src

RUN mvn -B package

# RUN mvn -B -e -o -T 1C verify

FROM openjdk:10-jre AS final

WORKDIR /stego

COPY --from=builder /stego/stegobmp.jar .

VOLUME ["/stego"]

ENTRYPOINT ["java", "-jar", "/stego/stegobmp.jar"]
CMD ["--help"]

