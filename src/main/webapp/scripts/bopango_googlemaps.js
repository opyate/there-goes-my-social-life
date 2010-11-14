var geocoder;
var map;

// Map Style Wizard: http://gmaps-samples-v3.googlecode.com/svn/trunk/styledmaps/wizard/index.html
// pink map JSON:
var bopango_map_style = [
  {
    featureType: "administrative",
    elementType: "all",
    stylers: [
      { saturation: -20 },
      { hue: "#ff00dd" }
    ]
  },{
    featureType: "landscape",
    elementType: "all",
    stylers: [
      { hue: "#ff00dd" },
      { saturation: -20 }
    ]
  },{
    featureType: "poi",
    elementType: "all",
    stylers: [
      { hue: "#ff00dd" },
      { saturation: -20 }
    ]
  },{
    featureType: "road",
    elementType: "all",
    stylers: [
      { hue: "#ff00dd" },
      { saturation: -20 }
    ]
  },{
    featureType: "transit",
    elementType: "all",
    stylers: [
      { hue: "#ff00dd" },
      { saturation: -20 }
    ]
  },{
    featureType: "water",
    elementType: "all",
    stylers: [
      { hue: "#ff00dd" },
      { saturation: -20 }
    ]
  }
]

function google_maps_init() {
    var latlng = new google.maps.LatLng(51.524552, -0.126987);
    var myOptions = {
        zoom: 15,
        center: latlng,
        mapTypeControlOptions: {
            mapTypeIds: [google.maps.MapTypeId.ROADMAP, 'bopango']
        }
    };
    map = new google.maps.Map(document.getElementById('map_canvas'), myOptions);
    var styledMapOptions = {
        name: "Bopango"
    }

    var bopangoMapType = new google.maps.StyledMapType(
            bopango_map_style, styledMapOptions);
    map.mapTypes.set('bopango', bopangoMapType);
    map.setMapTypeId('bopango')
}

function codeAddress() {
    geocoder = new google.maps.Geocoder();
    var address = document.getElementById("address").value;
    geocoder.geocode({ 'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
//            var marker = new google.maps.Marker({
//                map: map,
//                position: results[0].geometry.location
//            });
        } else {
            alert("Geocode was not successful for the following reason: " + status);
        }
    });

    setMarkers(map, test_restaurants);
}

function showInContentWindow(text) {
    var sidediv = document.getElementById('restaurant_address');
    sidediv.innerHTML = text;
}

/**
 * Data for the markers consisting of a name, a LatLng and a zIndex for
 * the order in which these markers should display on top of each
 * other.
 */
var test_restaurants = [
  ['Costa', 51.548982, -0.148573, 4, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"],
  ['Wagamama', 51.549873, -0.147573, 5, "<img src=\"images/restaurants/wagamama.png\"/><br/><br/><strong>Address:</strong><br/>66 Elm Street<br/>London<br/>NW5 6HH<br/>Phone: 0207 998 5544<br/>Email: <a href=\"#\">contact@wagamama.com</a><br/><br/>"],
  ['Costa', 51.547874, -0.146573, 3, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"],
  ['Wagamama', 51.550875, -0.145573, 2, "<img src=\"images/restaurants/wagamama.png\"/><br/><br/><strong>Address:</strong><br/>66 Elm Street<br/>London<br/>NW5 6HH<br/>Phone: 0207 998 5544<br/>Email: <a href=\"#\">contact@wagamama.com</a><br/><br/>"],
  ['Costa', 51.551876, -0.145873, 1, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"]
];

function setMarkers(map, locations) {
  // Add markers to the map

  // Marker sizes are expressed as a Size of X,Y
  // where the origin of the image (0,0) is located
  // in the top left of the image.

  // Origins, anchor positions and coordinates of the marker
  // increase in the X direction to the right and in
  // the Y direction down.
  var image = new google.maps.MarkerImage('images/bopango_flag.png',
      // This marker is 20 pixels wide by 32 pixels tall.
      new google.maps.Size(20, 32),
      // The origin for this image is 0,0.
      new google.maps.Point(0,0),
      // The anchor for this image is the base of the flagpole at 0,32.
      new google.maps.Point(0, 32));
  var shadow = new google.maps.MarkerImage('images/bopango_flag_shadow.png',
      // The shadow image is larger in the horizontal dimension
      // while the position and offset are the same as for the main image.
      new google.maps.Size(37, 32),
      new google.maps.Point(0,0),
      new google.maps.Point(0, 32));
      // Shapes define the clickable region of the icon.
      // The type defines an HTML &lt;area&gt; element 'poly' which
      // traces out a polygon as a series of X,Y points. The final
      // coordinate closes the poly by connecting to the first
      // coordinate.
  var shape = {
      coord: [1, 1, 1, 20, 18, 20, 18 , 1],
      type: 'poly'
  };
  for (var i = 0; i < locations.length; i++) {
    var restaurant = locations[i];
    var myLatLng = new google.maps.LatLng(restaurant[1], restaurant[2]);
    var marker = new google.maps.Marker({
        position: myLatLng,
        map: map,
        shadow: shadow,
        icon: image,
        shape: shape,
        title: restaurant[0],
        zIndex: restaurant[3]
    });

    google.maps.event.addListener(marker, 'click', function() {showInContentWindow(restaurant[4]);});

  }
}
