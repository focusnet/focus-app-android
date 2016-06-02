function FocusStandData() {
    this._type = null;
    this._url = null;
    this._version = null;
    this._owner = null;
    this._creationDateTime = null;
    this._editor = null;
    this._editionDateTime = null;
    this._active = null;
    this._data = null;
    this._leafletid = null;

    this.getType = function () {
        return this._type;
    };

    this.setType = function (value) {
        this._type = value;
    };

    this.getURL = function () {
        return this._url;
    };

    this.setURL = function (value) {
        this._url = value;
    };

    this.getVersion = function () {
        return this._version;
    };

    this.setVersion = function (value) {
        this._version = value;
    };

    this.getOwner = function () {
        return this._owner;
    };

    this.setOwner = function (value) {
        this._owner = value;
    };

    this.getCreationDateTime = function () {
        return this._creationDateTime;
    };

    this.setCreationDateTime = function (value) {
        this._creationDateTime = value;
    };

    this.getEditor = function () {
        return this._editor;
    };

    this.setEditor = function (value) {
        this._editor = value;
    };

    this.getEditionDateTime = function () {
        return this._editionDateTime;
    };

    this.setEditionDateTime = function (value) {
        this._editionDateTime = value;
    };

    this.getActive = function () {
        return this._active;
    };

    this.setActive = function (value) {
        this._active = value;
    };

    this.getData = function () {
        return this._data;
    };

    this.setData = function (value) {
        this._data = value;
    };

    this.setGeoJSON = function (value) {
        var data = this.getData();
        data.geojson = JSON.stringify(value);
    };

    this.getGeoJSON = function () {
        var data = this.getData();

        if (data.hasOwnProperty('geojson')) {
            return JSON.parse(data.geojson);
        } else {
            return null;
        }
    };

    this.getUID = function () {
        var url = this.getURL();
        var split = url.split("/");
        return split[split.length - 1];
    }

    this.toJSON = function () {
        var result = {
            url: this.getURL(),
            creationDateTime: this.getCreationDateTime(),
            active: this.getActive(),
            version: this.getVersion(),
            editor: this.getEditor(),
            type: this.getType(),
            owner: this.getOwner(),
            editionDateTime: this.getEditionDateTime(),
            data: this.getData()
        };

        result.data.geojson = JSON.stringify(this.getGeoJSON());

        return result;
    };
};

function FocusDataFactory() {
    FocusDataFactory.prototype.createStandData = function createStandDataObject(response) {
        console.log("CREATING FOCUS STAND DATA OBJECT");
        var dataObject = new FocusStandData();

        if (response.hasOwnProperty('active')) {
            dataObject.setActive(response.active);
        }

        if (response.hasOwnProperty('creationDateTime')) {
            dataObject.setCreationDateTime(response.creationDateTime);
        }

        if (response.hasOwnProperty('editionDateTime')) {
            dataObject.setEditionDateTime(response.editionDateTime);
        }

        if (response.hasOwnProperty('editor')) {
            dataObject.setEditor(response.editor);
        }

        if (response.hasOwnProperty('owner')) {
            dataObject.setOwner(response.owner);
        }

        if (response.hasOwnProperty('type')) {
            dataObject.setType(response.type);
        }

        if (response.hasOwnProperty('url')) {
            dataObject.setURL(response.url);
        }

        if (response.hasOwnProperty('version')) {
            dataObject.setVersion(response.version);
        }

        if (response.hasOwnProperty('data')) {
            dataObject.setData(response.data);
        }

        return dataObject;
    }
};
