FROM maven:3.8.3-openjdk-8

COPY . .

RUN mvn package -DskipTests

EXPOSE 5000

CMD ["java", "-cp", "target/DevOpsREST-1.0-SNAPSHOT.jar", "Main"]