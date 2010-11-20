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
            mapTypeIds: ['bopango']
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

function codeAddress(address) {
    geocoder = new google.maps.Geocoder();
    if (address) {
        //alert("address arg: " + address);
    } else {
        //alert("no address arg, using id");
        address = document.getElementById("address").value;
    }
    
    geocoder.geocode({ 'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
//            var marker = new google.maps.Marker({
//                map: map,
//                position: results[0].geometry.location
//            });
            
            // yields something like (51.5498766, -0.1485683)
            //alert(results[0].geometry.location);


            // If I want to be able to post a location from the same page, Ajax-style:
            // TODO
            // - post 'results[0].geometry.location' back to the server
            // - render a postback call here which
            // -- takes the location
            // -- finds restaurants close-by
            // -- calls 'setMarkers' once all the data's obtained

            setMarkers(map, restaurant_data);

        } else {
            //alert("Unfortunately, Google Maps could not find your location. (" + status + ")");

            // TODO let the server know that there was a problem
        }
    });


}

function showInContentWindow(text) {
    var sidediv = document.getElementById('restaurant_address');
    sidediv.innerHTML = text;
}

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
