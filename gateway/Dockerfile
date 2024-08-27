FROM maven:3.9.6-amazoncorretto-21@sha256:d1412a52e10b30ed42818c7a9d66ee75873c700579abc6671c27b98941940fec AS maven_builder

COPY pom.xml /build/
WORKDIR /build/

COPY ./calculator/src calculator/src
COPY ./calculator/pom.xml ./calculator/

COPY ./common/src common/src
COPY ./common/pom.xml ./common/

COPY ./deal/src deal/src
COPY ./deal/pom.xml ./deal/

COPY ./dossier/src dossier/src
COPY ./dossier/pom.xml ./dossier/

COPY ./gateway/src gateway/src
COPY ./gateway/pom.xml ./gateway/

COPY ./statement/src statement/src
COPY ./statement/pom.xml ./statement/

RUN mvn install -DskipTests

FROM alpine:3.20.2@sha256:0a4eaa0eecf5f8c050e5bba433f58c052be7587ee8af3e8b3910ef9ab5fbe9f5 AS packager

RUN apk --no-cache add openjdk21-jdk openjdk21-jmods
RUN apk --no-cache add binutils

ENV JAVA_MINIMAL="/opt/java-minimal"
RUN /usr/lib/jvm/java-21-openjdk/bin/jlink \
    --verbose \
    --add-modules \
        java.base,java.desktop,java.logging,java.management,java.naming,java.security.jgss,java.security.sasl,java.net.http,java.instrument,java.sql,java.xml,jdk.unsupported,jdk.crypto.ec,jdk.crypto.cryptoki,jdk.naming.rmi,jdk.naming.dns \
    --compress=zip-6 --strip-debug --no-header-files --no-man-pages \
    --release-info="add:IMPLEMENTOR=radistao:IMPLEMENTOR_VERSION=radistao_JRE" \
    --output "$JAVA_MINIMAL"

FROM alpine:3.20.2@sha256:0a4eaa0eecf5f8c050e5bba433f58c052be7587ee8af3e8b3910ef9ab5fbe9f5
LABEL maintainer="Dmitriy Kavtasyev"
ENV JAVA_HOME=/opt/java-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"
ENV TZ=Europe/Samara
COPY --from=packager "$JAVA_HOME" "$JAVA_HOME"

WORKDIR /app
COPY --from=maven_builder /build/gateway/target/gateway-0.0.1-SNAPSHOT.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]

