# Stage 1: Maven 构建
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# Stage 2: JRE 运行
FROM eclipse-temurin:8-jre-focal
WORKDIR /app

# 创建上传目录
RUN mkdir -p /data/uploads

COPY --from=build /app/target/clue-hub-1.0.0.jar app.jar
COPY admin-users.txt /app/admin-users.txt

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
