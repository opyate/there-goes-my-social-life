// a default lat/lng if Google is unavailable:
// lat = 51.51158
// lng = 0  (London)

var sideBarName = "sidebar";
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
    if ( typeof google == "undefined" ) {
        document.getElementById('map_canvas').innerHTML =
                "Google Maps is not available at this time. Please continue booking by selecting a venue from the listing.";
        codeAddress(null);
    } else {
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
}

function codeAddress(address) {
    if ( typeof google == "undefined" ) {
        var customCentre = new google.maps.LatLng(51.51158, 0, true);
        centreAndSearchLocationsNear(customCentre); // search for "default" coords
    } else {
        geocoder = new google.maps.Geocoder();
        if (address) {
        } else {
            address = document.getElementById("address").value;
        }

        geocoder.geocode({ 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {

                centreAndSearchLocationsNear(results[0].geometry.location);

            } else {
                alert("Unfortunately, Google Maps could not find your location. (" + status + ")");
            }
        });
    }
}

function centreAndSearchLocationsNear(location) {
    map.setCenter(location);

    // TODO either get radius from user, or base it on the GMap current zoom level
    //var radius = document.getElementById('radiusSelect').value;

    var searchUrl = 'api/venues/'+location.lat()+'/'+location.lng()+'/25';

    downloadUrl(searchUrl, function(data) {
        var xml = data;
        var markers = xml.documentElement.getElementsByTagName('marker');

        var sidebar = document.getElementById(sideBarName);
        sidebar.innerHTML = '';

        if (markers.length == 0) {
            sidebar.innerHTML = 'No results found.';
            return;
        }

        var bounds;
        if (!(typeof google == "undefined")) {
            bounds = new google.maps.LatLngBounds();
        }

        for (var i = 0; i < markers.length; i++) {

            var distance = parseFloat(markers[i].getAttribute('distance'));
            var name = markers[i].getAttribute('name');
            var address = markers[i].getAttribute('address');
            var description = markers[i].getAttribute('description');
            var id = markers[i].getAttribute('id');

            if (typeof google == "undefined") {
                var sidebarEntry = createSidebarEntry(marker, name, address, distance, description, id);
                sidebar.appendChild(sidebarEntry);
            } else {
                // Marker sizes are expressed as a Size of X,Y
                // where the origin of the image (0,0) is located
                // in the top left of the image.
                var point = new google.maps.LatLng(parseFloat(markers[i].getAttribute('lat')),
                        parseFloat(markers[i].getAttribute('lng')));
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

                var marker = new google.maps.Marker({
                    position: point,
                    map: map,
                    shadow: shadow,
                    icon: image,
                    shape: shape,
                    title: markers[i].getAttribute('name')
                });

                var sidebarEntry = createSidebarEntry(marker, name, address, distance, description, id);
                sidebar.appendChild(sidebarEntry);

                var html = '<b>' + name + '</b> <br/>' + address;

                var infoWindow = new google.maps.InfoWindow({
                    content:html
                });

                google.maps.event.addListener(marker, 'click', (function(event, index) {
                    return function() {
                        infoWindow.content = "<b>" + markers[index].getAttribute('name')  + "</b><br/>" + markers[index].getAttribute('address') + '<br/>' + getRestaurantForm(markers[index].getAttribute('id'));
                        infoWindow.open(map, this);
                    }
                })(marker, i));

                bounds.extend(point);
            }
        }

        if (!(typeof bounds == "undefined")) {
            map.fitBounds(bounds);
        }
    });
}

function createMarker(point, name, address) {
    var marker = new google.maps.Marker(point);
    var html = '<b>' + name + '</b> <br/>' + address;
    google.maps.event.addListener(marker, 'click', function() {
        marker.openInfoWindowHtml(html);
    });
    return marker;
}

function createSidebarEntry(marker, name, address, distance, description, id) {
    var div = document.createElement('div');
    var html = '<b>' + name + '</b>' +
            ' (' + distance.toFixed(1) + 'km)<br/>' +
            address;

    div.style.cursor = 'pointer';
    div.style.padding = '2px';
    div.style.paddingBottom = '5px';
    div.style.borderBottom = '1px solid #ddd';
    div.innerHTML = html;

    if ( !(typeof google == "undefined") ) {
        google.maps.event.addDomListener(div, 'click', function() {
            google.maps.event.trigger(marker, 'click');
            //document.getElementById('restaurant').value = id;
            //document.getElementById('sidebar_submit').style.visibility = 'visible';
            document.getElementById('restaurant_info').innerHTML = description + '<br/>' + getRestaurantForm(id);
        });
        google.maps.event.addDomListener(div, 'mouseover', function() {
            div.style.backgroundColor = '#eee';
        });
        google.maps.event.addDomListener(div, 'mouseout', function() {
            div.style.backgroundColor = '#fff';
        });
    } else {
        div.click = function() {
            document.getElementById('restaurant').value = id;
            document.getElementById('sidebar_submit').style.visibility = 'visible';
            document.getElementById('restaurant_info').innerHTML = description + '<br/>' + getRestaurantForm(id);
        };
        div.mouseover = function() {
            div.style.backgroundColor = '#eee';
        };
        div.mouseout = function() {
            div.style.backgroundColor = '#fff';
        };
    }

    return div;
}

function getRestaurantForm(id) {
    return '<form method="get" action="book"><input type="hidden" name="restaurant_id" value="'+id+'"/><input type="submit" value="Go"/></form>'
}

function showInContentWindow(text) {
    var sidediv = document.getElementById(sideBarName);
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
