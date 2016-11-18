FROM clojure:lein-2.7.1-alpine
MAINTAINER Joel Birchler <joel@joelbirchler.com>

RUN mkdir /app
WORKDIR /app

# Dependencies
COPY project.clj /app
RUN lein deps

# Standalone uberjar
COPY . /app
RUN lein cljsbuild once
RUN lein ring uberjar

CMD ["java", "-jar", "/app/target/grok-lab-0.1.0-SNAPSHOT-standalone.jar"]
