# Grok Lab

Grok Lab is a learning platform for live demo and experimentation of code.

## TODO

- Execute on load
- Terminate any existing running workers on run, set/reset a terminate timer
- Improve editor integration (with errors, etc)
- Think about __watch__(uid, code) wrappers
- Load lodash by default


## Building

Build ClojureScript with:

    $ lein cljsbuild once
    (or)
    $ lein cljsbuild auto


## Running

Fire up the server:

    $ lein ring server


## License

This project is under the MIT license.
Copyright © 2016 Joel Birchler.
