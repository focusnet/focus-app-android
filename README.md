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

## Building

### Getting the git repository

This git repository contains a submodule, so for cloning:

````
$ git clone --recursive git@github.com:focusnet/focus-app-android.git
````

### Requirements

- This application will run on devices running Android SDK 19 or more recent. 
- The dependencies are defined in the `focus-mobile/build.gradle` file.

### Compilation

- This project can be built with AndroidStudio, although other building environments should work.
- Before building, see documented procedure in issues #9 and #10


## Licenses

### FOCUS Mobile

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


### Third-party software


<p>FOCUS Mobile is using the following software components, also released under open-source
    commercial-friendly licenses:</p>

<table>
    <thead>
    <tr>
        <th>Software component</th>
        <th>License</th>
        <th>Web page</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>MPAndroidChart</td>
        <td>Copyright 2016 Philipp Jahoda - Apache License, Version 2.0</td>
        <td><a href="https://github.com/PhilJay/MPAndroidChart">Link</a></td>
    </tr>
    <tr>
        <td>SortableTableView for Android</td>
        <td>Copyright 2015 Ingo Schwarz - Apache License, Version 2.0</td>
        <td><a href="https://github.com/ISchwarz23/SortableTableView">Link</a></td>
    </tr>
    <tr>
        <td>Gson</td>
        <td>Copyright 2008 Google Inc. - Apache License, Version 2.0</td>
        <td><a href="https://github.com/google/gson">Link</a></td>
    </tr>
    <tr>
        <td>jQuery</td>
        <td>Copyright 2016 The jQuery Foundation - MIT License</td>
        <td><a href="https://jquery.com/">Link/</a></td>
    </tr>
    <tr>
        <td>Simosol Map web component</td>
        <td>Copyright (c) 2016, Simosol Oy</td>
        <td><a href="https://github.com/focusnet/fi.simosol.focus.map">Link</a></td>
    </tr>
    
    </tbody>
<!--
FIXME Google Maps API ?
-->
</table>

<p>Artworks not specific to the specific project are provided by the <a href="http://fontawesome.io/">Font Awesome</a> icon library,
    which is released under the SIL Open Font License (OFL).</p>


## Contact

https://web.ti.bfh.ch/~flu1/


# TODO
 



TO TEST
- HttpResponse: replace StringBuffer with StringBuilder (not thread-safe but more efficient)	
 -- GPSWidgetFragment:  test current code. Does it also work without Intenet connection?