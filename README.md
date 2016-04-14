# Grok Lab

Grok Lab is a learning platform for live demo and experimentation of code.

## TODO

- Watching
  - Highlight then alt+w or click watch adds/removes a marker for watching
    - event should tell core to set the watch range (r/atom), then this will trigger a marker
    - anchor: https://groups.google.com/forum/#!topic/ace-discuss/WsL-ZATvKQQ
  - Add __watch__(uid, code) wrappers
  - Watches list in console area (maybe console stack?)
- Termination and errors
  - Terminate any existing running workers on run, set/reset a terminate timer
  - Improve editor integration (with errors, etc)
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
