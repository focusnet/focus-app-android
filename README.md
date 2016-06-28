# FOCUS Mobile


<p>The objective of the FOCUS Mobile solution is to offer means for workers to take benefit from the
    data consensus provided by the FOCUS platform when they are working on-site.</p>

<p>This solution is implemented as an open-source Android application and consists of three
    complementary aspects: (a) information visualization, (b) data collection, and (c) decision
    support.</p>

<p>This software has been developped by the Berner Fachhochschule (BFH). Sources are available on
    <a href="https://github.com/focusnet/focus-app-android">GitHub</a>.</p>

<p><a href="http://www.focusnet.eu">www.focusnet.eu</a></p>
<p><a href="http://www.bfh.ch">www.bfh.ch</a></p>

## Introduction

## Getting the repo

````
$ git clone --recursive git@github.com:focusnet/focus-app-android.git
````

## Requirements

## Building

- See #9 and #10
- Generate the APK

## Third-party software

Android:
MPAndroidChart http://www.apache.org/licenses/LICENSE-2.0
Table
GSON
Google API
font awesome

Webapps:
- jQuery
- SIMOSOL: 

## Contact

## License

This software is released under the commercial-friendly and open-source MIT license.

````
The MIT License (MIT)

Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
````



# TODO
 


TODAY

- About
- README.md

TO TEST
- HttpResponse: replace StringBuffer with StringBuilder (not thread-safe but more efficient)	
 -- GPSWidgetFragment:  test current code. Does it also work without Intenet connection?