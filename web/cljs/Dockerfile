FROM clojure:lein-2.7.1-alpine
MAINTAINER Joel Birchler <joel@joelbirchler.com>

COPY . /build
WORKDIR /build

RUN lein deps

CMD ["lein", "cljsbuild", "once"]
