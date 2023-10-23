FROM gradle:jdk17 AS build
WORKDIR /app

#Context 가 상위폴더 기준이다.
COPY . /app

RUN gradle build -x test

FROM eclipse-temurin:17-jre-alpine as run
WORKDIR /app

# tzdata: set timezone
RUN apk --no-cache add tzdata && \
	cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
	echo "Asia/Seoul" > /etc/timezone \
	apk del tzdata

# use non-root linux account
RUN addgroup -S webgroup && adduser -S webuser -G webgroup
USER webuser:webgroup

ARG JAR_FILE=/app/build/libs/mcafe-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]