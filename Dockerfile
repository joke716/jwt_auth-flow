FROM maven:3.9-amazoncorretto-21

WORKDIR /backend

COPY pom.xml .
COPY lombok.config .

RUN mvn dependency:go-offline -B

COPY . .

CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.fork=false"]