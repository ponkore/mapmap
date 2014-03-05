// Start position for the map (hardcoded here for simplicity)
var lat=34.73368;
var lon=135.500035;
var zoom=12;

var map; //complex object of type OpenLayers.Map

//Initialise the 'map' object
$(function() {
  map = new OpenLayers.Map('map', {
    layers: [
      new OpenLayers.Layer.OSM.Mapnik("Mapnik")
    ],
    controls: [
      new OpenLayers.Control.Navigation(),
      new OpenLayers.Control.PanZoomBar(),
      new OpenLayers.Control.LayerSwitcher(),
      new OpenLayers.Control.ScaleLine(),
      new OpenLayers.Control.Attribution()],
    eventListeners: {
      "moveend": mapMoved,
      "zoomend": mapZoomEnd
    },
    maxResolution: 'auto'
  });
  var lonLat = new OpenLayers.LonLat(lon, lat).transform(
    new OpenLayers.Projection("EPSG:4326"),
    map.getProjectionObject()
  );
  map.setCenter(lonLat, zoom);

  // see http://dev.openlayers.org/releases/OpenLayers-2.10/doc/apidocs/files/OpenLayers/Feature/Vector-js.html#OpenLayers.Feature.Vector.style
  var composedDrawStyle = new OpenLayers.StyleMap({
      'default':{
          pointRadius: 6,
          strokeColor: "${strokeColor}",
          strokeOpacity: 1,
          strokeWidth: "${strokeWidth}",
          strokeDashstyle: "solid",
          strokeLinecap: "square",
          fillColor: "#0000ff",
          fillOpacity: 1.0,
          label : "${name}",
          fontColor: "${fontcolor}",
          fontSize: "10px",
          fontFamily: "Courier New, monospace",
          fontWeight: "bold",
          labelOutlineColor: "white",
          labelOutlineWidth: 2},
      'selected':{
          pointRadius: 8,
          strokeColor: "#00FF00"}});

  function create_features(url, create_feature_fn, layer) {
    var geojson_format = new OpenLayers.Format.GeoJSON();
    $.getJSON(url, "",
      function(data, textStatus, jqXHR) {
        $.each(data["features"], function(i, val) {
          var geometry = geojson_format.parseGeometry(val["geometry"]);
          geometry.transform(
            new OpenLayers.Projection("EPSG:4326"),
            map.getProjectionObject()
          );
          layer.addFeatures([create_feature_fn(val, geometry)]);
        });
     });
  }

  function feature_composed_fn(val, geometry) {
    var isShinkansen = (val["properties"]["N05_002"] == "山陽新幹線");
    var isStation = (val["geometry"]["type"] == "Point");
    var nn = (isStation ? val["properties"]["N05_011"] : val["properties"]["N05_002"]);
    var feature = new OpenLayers.Feature.Vector(geometry, {
      id: val["id"],
      name: nn,
      fontcolor: (isShinkansen ? "#0000FF" : "#000000"),
      strokeWidth: (isStation? 2.0 : 4.5),
      strokeColor: (isStation? "#FF0000" : (isShinkansen ? "#0000FF" : "#000000"))
    });
    return feature;
  }

  function mapMoved(event) {
//    log(event.type);
  }

  function mapZoomEnd(event) {
//    log(event.type + "/" + map.numZoomLevels + "/" + map.getZoom());
//    var displayStationLabel = (map.getZoom() > 12);
  }

//  function log(msg) {
//    document.getElementById("output").innerHTML += msg + "\n";
//  }

  var urls = [
      "JRW-railroad.geojson",
      "JRW-stations.geojson"
      ];
  $.each(urls, function(i, val) {
    var layer = new OpenLayers.Layer.Vector("L" + i, { styleMap: composedDrawStyle });
    map.addLayer(layer);
    var file = "/map/" + val;
    create_features(file, feature_composed_fn, layer);
  });
});
