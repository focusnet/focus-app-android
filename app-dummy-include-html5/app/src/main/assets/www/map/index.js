var map;

function initmap() {
    console.log("initmap");

    // set up the map
    map = new L.Map('map', {attributionControl: false});

    var shapeOptions = {
        stroke: true,
        color: '#f06eaa',
        weight: 4,
        opacity: 0.5,
        fill: true,
        fillColor: null, //same as color by default
        fillOpacity: 0.2,
        clickable: true
    };

    // create the openstreetmap tile layer
    var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 18});

    // start the map in South-East England
    map.setView([51.503, -0.06], 13);
    map.addLayer(osm);

    var drawnItems = new L.FeatureGroup();
    map.addLayer(drawnItems);

    L.drawLocal.draw.toolbar.buttons.polygon = 'Draw a sexy polygon!';

    var drawControl = new L.Control.Draw({
        position: 'topright',
        draw: {
            marker: false,
            circle: false,
            polygon: {
                allowIntersection: false,
                showArea: true,
                drawError: {
                    color: '#b00b00',
                    timeout: 1000
                }
            }
        },
        edit: {
            featureGroup: drawnItems,
            remove: true
        }
    });
    map.addControl(drawControl);

    map.on('draw:created', function (e) {
        var type = e.layerType,
            layer = e.layer;

        // Do whatever else you need to. (save to db, add to map etc)
        drawnItems.addLayer(layer);
    });

    // in here you do whatever you want with the split output
    map.on('merge:created', function (e) {
        if (e.merge) {
            var result = e.merge;
            result.eachLayer(function (layer) {
                drawnItems.addLayer(layer);
            });
        }
    });

    // in here you do whatever you want with merge output
    map.on('split:created', function (e) {
        if (e.created) {
            var result = e.created;
            result.eachLayer(function (layer) {
                console.log(layer);
                layer.eachLayer(function (sublayer) {
                    sublayer.setStyle(shapeOptions);
                    drawnItems.addLayer(sublayer);
                })
            });
        }
    })

    // sample data load from local file
    var xmlhttp = new XMLHttpRequest();
    var url = "sampledata.json";
    var standDataObject = null;
    var factory = new FocusDataFactory();

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            standDataObject = factory.createStandData(JSON.parse(xmlhttp.responseText));
            console.log("STANDDATAOBJECT :: %o", standDataObject);
            console.log(" > uid :: " + standDataObject.getUID());
            data = standDataObject.getGeoJSON();
            console.log(" > getGeoJSON :: %o", data);
            var geojson = L.Proj.geoJson(data).addTo(drawnItems);
            map.fitBounds(geojson.getBounds());
            console.log(JSON.stringify(standDataObject.toJSON()));
        }
    };

    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}