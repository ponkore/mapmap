// Start position for the map (hardcoded here for simplicity)
var lat=34.73368;
var lon=135.500035;
var zoom=12;

var map; //complex object of type OpenLayers.Map
var layer;

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
          fontSize: "15px",
          fontFamily: "Courier New, monospace",
          fontWeight: "bold",
          labelOutlineColor: "white",
          labelOutlineWidth: 2},
      'selected':{
          pointRadius: 8,
          strokeColor: "#00FF00"}});

  layer = new OpenLayers.Layer.Vector("L01", { styleMap: composedDrawStyle });
  map.addLayer(layer);

  function create_feature_fn(val, geometry) {
    var isShinkansen = (val["properties"]["name"] == "山陽新幹線");
    var feature = new OpenLayers.Feature.Vector(geometry, {
      id: val["id"],
      name: val["properties"]["name"],
      fontcolor: (isShinkansen ? "#0000FF" : "#000000"),
      strokeWidth: 2.0,
      strokeColor: "#FF0000"
    });
    return feature;
  }

  function create_features(param) {
    var geojson_format = new OpenLayers.Format.GeoJSON();
    var url = "/map/stations";
    if (layer != undefined) {
      layer.removeAllFeatures();
    }
    $.getJSON(url, param,
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

  function mapMoved(event) {
    var extent = map.getExtent().transform(
        new OpenLayers.Projection("EPSG:900913"),
        new OpenLayers.Projection("EPSG:4326")
    );
    var zoom = map.getZoom();
    var scale = map.getScale();
    console.debug("left:" + extent.left +
                  ",top:" + extent.top +
                  ",right:" + extent.right +
                  ",bottom:" + extent.bottom +
                  ",zoom:" + zoom +
                  ",scale:" + scale);
    create_features({
      left: extent.left,
      top: extent.top,
      right: extent.right,
      bottom: extent.bottom,
      zoom: zoom,
      scale: scale
    });
  }

  function mapZoomEnd(event) {
    console.debug(event.type + "/" + map.numZoomLevels + "/" + map.getZoom());
  }
});
