FROM nginx:1.11.5-alpine
MAINTAINER Joel Birchler <joel@joelbirchler.com>

COPY grok-nginx.conf /etc/nginx/conf.d/default.conf
COPY static /usr/share/nginx/static
COPY cljs/compiled-js /usr/share/nginx/js
