# Grok Lab

Grok Lab is a learning platform for live demo and experimentation of code.

## TODO

- Watching
  - Use js splice (watch out for mutation) to instrument the code
  - Protect against instrument blanks with watches
  - Watch displays in console area, constantly updates
  - Run/watch tabs?
- defn-
- Termination and errors
  - Terminate any existing running workers on run, set/reset a terminate timer
  - Improve editor integration (with errors, etc)
- Load lodash by default


## Building

Build ClojureScript with:

    $ lein cljsbuild once
    $ lein cljsbuild auto


## Running

Fire up the server:

    $ lein ring server


## License

This project is under the MIT license.
Copyright © 2016 Joel Birchler.
