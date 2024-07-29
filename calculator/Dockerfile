WORKDIR /
MAINTAINER Dmitriy Kavtasyev <dkavtasyev@gmail.com>


WORKDIR /usr/local/builder/
COPY src ./src/
RUN mvn bootJar

FROM openjdk:11-jre-slim@sha256:93af7df2308c5141a751c4830e6b6c5717db102b3b31f012ea29d842dc4f2b02
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n"
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0"
COPY --from=backend_builder /usr/local/builder/build/libs/*.jar /usr/local/app.jar
EXPOSE 8080 5005
CMD java ${JAVA_OPTS} -jar -Duser.timezone=Europe/Moscow /usr/local/app.jar