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
      "JRW-000-sanyoshinkansen.geojson",
      "JRW-001-hokurikusen.geojson",
      "JRW-002-etsumihokusen.geojson",
      "JRW-003-nanaosen.geojson",
      "JRW-004-johanasen.geojson",
      "JRW-005-himisen.geojson",
      "JRW-006-takayamasen.geojson",
      "JRW-007-ooitosen.geojson",
      "JRW-008-kansaisen.geojson",
      "JRW-009-kusatsusen.geojson",
      "JRW-010-narasen.geojson",
      "JRW-011-sakuraisen.geojson",
      "JRW-012-wakayamasen.geojson",
      "JRW-013-hanwasen.geojson",
      "JRW-014-kiseisen.geojson",
      "JRW-015-tokaidosen.geojson",
      "JRW-016-koseisen.geojson",
      "JRW-017-fukuchiyamasen.geojson",
      "JRW-018-obamasen.geojson",
      "JRW-019-kakogawasen.geojson",
      "JRW-020-bantansen.geojson",
      "JRW-021-maizurusen.geojson",
      "JRW-022-kanjosen.geojson",
      "JRW-023-katamachisen.geojson",
      "JRW-024-tozaisen.geojson",
      "JRW-025-oosakahigashisen.geojson",
      "JRW-026-sanyosen.geojson",
      "JRW-027-kishinsen.geojson",
      "JRW-028-akosen.geojson",
      "JRW-029-tsuyamasen.geojson",
      "JRW-030-kibisen.geojson",
      "JRW-031-unosen.geojson",
      "JRW-032-honshibisansen.geojson",
      "JRW-033-hakubisen.geojson",
      "JRW-034-geibisen.geojson",
      "JRW-035-fukushiosen.geojson",
      "JRW-036-kuresen.geojson",
      "JRW-037-kabesen.geojson",
      "JRW-038-iwatokusen.geojson",
      "JRW-039-yamaguchisen.geojson",
      "JRW-040-ubesen.geojson",
      "JRW-041-onodasen.geojson",
      "JRW-042-miyasen.geojson",
      "JRW-043-sanninsen.geojson",
      "JRW-044-inbisen.geojson",
      "JRW-045-sakaisen.geojson",
      "JRW-046-kisugisen.geojson",
      "JRW-047-sankosen.geojson",
      "JRW-048-hakataminamisen.geojson"
      ];
  $.each(urls, function(i, val) {
    var layer = new OpenLayers.Layer.Vector("L" + i, { styleMap: composedDrawStyle });
    map.addLayer(layer);
    var file = "/map/" + val;
    create_features(file, feature_composed_fn, layer);
  });
});
