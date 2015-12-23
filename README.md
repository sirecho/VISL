# VISL - A project that provide a simple framework for visual invariant testing of web pages.

To run VISL you must have Firefox and Xvfb (X virtual framebuffer) installed on your system.

VISL also requires the OpenCV library which is not included in the current Maven configuration.


The "tests" project requires that the "VISL" project is built and installed to maven:
- go into the VISL directory and run 'mvn install'

To run the tests, go into the tests directory and run 'mvn test'. This will test all feature definitions in the tests/src/test/resources/com/tests directory.
