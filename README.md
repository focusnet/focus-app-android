# focus-app-android

FOCUS Android application

(still in active development ... may not compile at all)

## Warning

The application silently sends error reports when it encounters errors. These reports may contain
the content of the displayed dashboards and other sensitive information. If you do not agree
with that, please do not use the application

This feature will be disabled when the application enters production stage.

## DEV note

we use git sumbodules for fi.simosol.focus.map. Be careful, submodules are not easy not handle.


## Third-party software

Android:
MPAndroidChart
Table
GSON
Google API

Webapps:
- jQuery
- SIMOSOL: 


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

# DOC
- MVC-like

# TODO
- check if we set synchronized methods and volatile properties correctly
- check that ACRA works in production. Sometimes the bug report window is not shown in emulator. Watch.
- Anonymize ACRA reports
- FocusApplogic#init() -> useExistingDataSet() -> warning in logs because is run on main thread. -> move to EntrypointActivity#doInBackground() ?
- i18n change language always work? emulator sometimes give strange results.
- getSupportedLanguages() -> from properties?
- FocusInternalException:  FIXME probably bad practice. We should keep the original exception. 
- Translate to DE
- ProjectInProjectActivity -> useless? FIXME perhaps there is a smarter way to do that.
- ProjectListingActivity: 
 - animate sync button when active. Listen for completion.
 - onBackPressed() FIXME no animation? init.setFlags ( ANIMATION ) may help
 - highlightSelectedMenuItem() does it have any effect? 
- getColor(id) is deprecated
- move to real login activity.  * FIXME this code is not used. For the prototype, we use {@link DemoUseCaseSelectionActivity}. - marked as deprecated.
- TODO consider setting up AppIndex API
- envoyer un mail à Yandy pour dire que le projet est terminé.
- TAbleView may be SortableTableView
- scroll on TouchTableView does not work anymore??? TouchTableView
- fix NavigationListAdapter#getView() recycling.
- NavigationListADapter.SaveUserPreferencesTask :  what happens if we save user preferences when user preference saving is not finished, yet. To check.
- Projectfragment.ProjectBuilderTask : modularize code (duplicate) / also consider other classes
- user / prefersnces instances -> child of gson objects -> into model, not gson directory
- use savedInstanceState, especially if we must survive configuration changes (rotation of screen)
- WidgetFragment#setupWidget: redirect if missing WidgetInstance for all widgets, which is not optimal.
- SubmitWidgetFragment: implement action on click submit.
- improve layout of pie chart, line chart and bar chart widgets
- possible to modularize bar chart and line chart widgets? mostly similar.
- WebAppInterface: 
 - implement non-blocking methods with {@code Future}s
 - implement getResource()
 - implement getAccessControlToken()
- GPSWidgetFragment: 
 - move to MOTI code
 - test current code. Does it also work without Internet connection?