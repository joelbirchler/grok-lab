all: build verify run

build:
	docker build -t grok-lab .

run:
	docker run --rm -p 8080:3000 grok-lab

# TODO:	lein cljsbuild auto 

repl:
	docker run --rm -it grok-lab lein repl

verify:
	docker run --rm grok-lab lein test
