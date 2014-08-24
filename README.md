# screenslide

Quick and dirty image slideshow using Clojure and SWT.

## Usage

You'll need [leiningen](https://github.com/technomancy/leiningen). Screenslide
can be run like so:

    lein run $directory

Screenslide will recursively search the specified directory for images and
present them as a full screen slide show.

## Bugs

The initial switch to full screen is a bit jarring, the images start displaying
during the Mac OS X full screen transition.

This has only been tested on Mac OS X.

## License

Copyright © 2013 John Barker

Distributed under the Eclipse Public License, the same as Clojure.
