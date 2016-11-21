FROM clojure:lein-2.7.1-alpine
MAINTAINER Joel Birchler <joel@joelbirchler.com>

COPY . /app
WORKDIR /app

RUN lein deps

CMD ["lein", "ring", "server-headless", "8080"]
