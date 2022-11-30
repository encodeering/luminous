FROM gradle:7-jdk11 AS build

WORKDIR                      /var/www/partner
COPY --chown=gradle:gradle . /var/www/partner

RUN ./gradlew buildFatJar --no-daemon


FROM       adoptopenjdk:11-jre-hotspot

ARG        user=www-data

ENV        JAVA_OPTS=""

EXPOSE     8080

WORKDIR    /var/www/partner

COPY       --from=build --chown=${user}:${user} /var/www/partner/build/libs/partner-all.jar lib/partner-all.jar

USER                            ${user}

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar lib/partner-all.jar"]
