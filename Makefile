all: build verify run
build: build-app build-web

build-app:
	docker build -t grok-lab-app ./app

build-web:
	docker build -t grok-lab-cljs ./web/cljs
	docker run --rm --name "grok-cljs" --volume $(PWD)/web/cljs/compiled-js:/build/compiled-js grok-lab-cljs
	docker build -t grok-lab-web ./web

verify:
	docker run --rm --name "grok-app-test" grok-lab-app lein test

run:
	docker-compose up

prod-run:
	# TODO
	docker run --rm --name "grok-app" -p 8080:8080 --env "LEIN_NO_DEV=true" grok-lab-app

repl:
	docker run --rm --name "grok-app-repl" -it grok-lab-app lein repl
