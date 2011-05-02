/**
 * Boffin Frontend code.
 *
 * Responsible for rendering search results and doing client-side API calls.
 *
 */

var BoffinFrontend = {
    map: null,
    infoWindow: null,
    boffin_results: null,
    boffin_meta: null,
    geocoder: null,
    // clearing overlays as per http://code.google.com/apis/maps/documentation/javascript/overlays.html
    markersArray: [],
    // a nice, purple map,
    // Map Style Wizard: http://gmaps-samples-v3.googlecode.com/svn/trunk/styledmaps/wizard/index.html
    style: [
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
};


BoffinFrontend.init = function() {
    if ( typeof google == "undefined" ) {
        // maps not available, but this should not influence the UX in any way.
    } else {
        // center on London -- Bopango HQ!
        var latlng = new google.maps.LatLng(51.524552, -0.126987);
        var myOptions = {
            zoom: 15,
            center: latlng,
            mapTypeControlOptions: {
                mapTypeIds: ['bopango']
            }
        };
        BoffinFrontend.map = new google.maps.Map(document.getElementById('map_canvas'), myOptions);
        var styledMapOptions = {
            name: "Bopango"
        };

        // map type
        var bopangoMapType = new google.maps.StyledMapType(
                BoffinFrontend.style, styledMapOptions);
        BoffinFrontend.map.mapTypes.set('bopango', bopangoMapType);
        BoffinFrontend.map.setMapTypeId('bopango');

        // Create a single instance of the InfoWindow object which will be shared
        // by all Map objects to display information to the user.
        BoffinFrontend.infoWindow = new google.maps.InfoWindow();
        
        BoffinFrontend.geocoder = new google.maps.Geocoder();
        
        // search results, and meta info are both sections on the page
        BoffinFrontend.boffin_results = document.getElementById("boffin_results");
        BoffinFrontend.boffin_meta = document.getElementById('boffin_meta');

        // listener to clear all overlays if the map is clicked anywhere
//        google.maps.event.addListener(BoffinFrontend.map, 'click', function(event) {
//            BoffinFrontend.deleteOverlays();
//        });
    }
}

/**
 * Takes 'query' and calls various APIs until a sufficient result is achieved.
 *
 * Calls:
 * 1) Google Geocoder, then if we get a valid result,
 * 1.1) proximity query to get results near the geo result
 * 2) Boffin, based on loose search
 *
 * TODO have "show more results near this" function, especially in case of (2) above.
 *
 * @param query
 */
BoffinFrontend.search = function(query) {
    if ( typeof google == "undefined" ) {
        BoffinFrontend.searchRestaurants(query, "Calling Bopango Restaurants API because GMaps API is down.");
    } else {
        // clear all overlays when doing a new search
        BoffinFrontend.deleteOverlays();
        BoffinFrontend.boffin_results.innerHTML = '';
        BoffinFrontend.boffin_meta.innerHTML = '';

        if (query) {
        } else {
            // if the search query was not provided as an argument, try and find it in
            // a preselected place on the form.
            alert("Weird -- I didn't receive the query string...");
            query = document.getElementById("query").value;
        }

        BoffinFrontend.geocoder.geocode({ 'address': query}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                BoffinFrontend.findLocationsNear(results[0].geometry.location, query);
            } else {
                //  search Boffin
                BoffinFrontend.searchRestaurants(query, "Calling Bopango Restaurants API because GMaps API did not find a geo location for [" + query + "]");
            }
        });
    }
}

BoffinFrontend.searchRestaurants = function(query, msg) {
    //alert(msg);
    if (query) {
        // Do not remove the .xml below until we can figure out how to send the Accept header with Ajax call.
        // center the BoffinFrontend.map on the first venue in the result, if we have any results -- using 'true'.
        BoffinFrontend.callAPI('api/search/restaurants/' + encodeURI(query) + ".xml", query, true);
    }
}

BoffinFrontend.findLocationsNear = function(location, query) {
    BoffinFrontend.map.setCenter(location);
    // BoffinFrontend.map already centered -- using 'false'.
    BoffinFrontend.callAPI('api/venues/'+location.lat()+'/'+location.lng()+'/25', query, false);
}

BoffinFrontend.callAPI = function(searchUrl, query, centerMap) {
    downloadUrl(searchUrl, function(data) {
        var xml = data;
        var markers = xml.documentElement.getElementsByTagName('marker');

        BoffinFrontend.boffin_results.innerHTML = '';

        // we've reached this point, because the search query yields a Geo result,
        // but our own API can't find anything nearby.
        // So, let's try our own search engine instead
        if (markers.length == 0) {
            BoffinFrontend.boffin_results.innerHTML = 'No results found.';
            BoffinFrontend.searchRestaurants(query, searchUrl + " called, but no markers in result.");
            return;
        }

        var bounds;
        if (!(typeof google == "undefined")) {
            bounds = new google.maps.LatLngBounds();
        }

        for (var i = 0; i < markers.length; i++) {
            var lat =  markers[i].getAttribute('lat');
            var lng =  markers[i].getAttribute('lng');
            var point;
            if (!(typeof google == "undefined")) {
                point = new google.maps.LatLng(parseFloat(lat), parseFloat(lng));

                if (centerMap && i == 0) {
                    BoffinFrontend.map.setCenter(point);
                }
            }

            BoffinFrontend.placeMarker(point,
                    markers[i].getAttribute('id'),
                    markers[i].getAttribute('name'),
                    markers[i].getAttribute('address'),
                    markers[i].getAttribute('description'),
                    lat,
                    lng,
                    markers[i].getAttribute('distance'));

            if (!(typeof google == "undefined")) {
                bounds.extend(point);
            }
        }

        if (!(typeof bounds == "undefined")) {
            BoffinFrontend.map.fitBounds(bounds);
        }
    });
}

BoffinFrontend.createSidebarEntry = function(marker, name, address, distance, description, id) {
    var newDiv = document.createElement('div');
    var html = '<b>' + name + '</b>' +
            ' (' + distance.toFixed(1) + 'km)<br/>' +
            address + '<br/>' +
            '';

    newDiv.style.cursor = 'pointer';
    newDiv.style.padding = '2px';
    newDiv.style.paddingBottom = '5px';
    newDiv.style.borderBottom = '1px solid #ddd';
    newDiv.innerHTML = html;
    
    // display restaurant information in dedicated div below the map
    var resto_info = description + '<br/>' + BoffinFrontend.getRestaurantForm(id)

    if ( !(typeof google == "undefined") ) {
        google.maps.event.addDomListener(newDiv, 'click', function() {
            google.maps.event.trigger(marker, 'click');

            BoffinFrontend.boffin_meta.innerHTML = resto_info;
        });
        google.maps.event.addDomListener(newDiv, 'mouseover', function() {
            newDiv.style.backgroundColor = '#eee';
        });
        google.maps.event.addDomListener(newDiv, 'mouseout', function() {
            newDiv.style.backgroundColor = '#fff';
        });
    } else {

        newDiv.onclick = function() {
            BoffinFrontend.boffin_meta.innerHTML = resto_info;
        };

        newDiv.mouseover = function() {
            newDiv.style.backgroundColor = '#eee';
        };
        newDiv.mouseout = function() {
            newDiv.style.backgroundColor = '#fff';
        };
    }

    return newDiv;
}

BoffinFrontend.getRestaurantForm = function(id) {
    return '<a href="/venue/'+id+'">Select this one</a>'
}

BoffinFrontend.placeMarker = function(point, id, name, address, description, lat, lng, distance) {

    if (typeof google == "undefined") {
        BoffinFrontend.boffin_results.appendChild(
                BoffinFrontend.createSidebarEntry(marker, name, address, parseFloat(distance), description, id)
                );
    } else {
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
            new google.maps.Point(0, 32)
        );
        
        var shadow = new google.maps.MarkerImage('images/bopango_flag_shadow.png',
            // The shadow image is larger in the horizontal dimension
            // while the position and offset are the same as for the main image.
            new google.maps.Size(37, 32),
            new google.maps.Point(0,0),
            new google.maps.Point(0, 32)
        );
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
            map: BoffinFrontend.map,
            shadow: shadow,
            icon: image,
            shape: shape,
            title: name
        });
        BoffinFrontend.markersArray.push(marker);

        BoffinFrontend.boffin_results.appendChild(
                BoffinFrontend.createSidebarEntry(marker, name, address, parseFloat(distance), description, id)
                );

        google.maps.event.addListener(marker, 'click', (function(event, index) {
            return function() {
                BoffinFrontend.infoWindow.close();
                BoffinFrontend.infoWindow.content = "<b>" + name  + "</b><br/>" + address + '<br/>' + BoffinFrontend.getRestaurantForm(id);
                BoffinFrontend.infoWindow.open(BoffinFrontend.map, this);
                BoffinFrontend.boffin_meta.innerHTML = description + '<br/>' + BoffinFrontend.getRestaurantForm(id);
            }
        })(marker, id));
    }
}

// Removes the overlays from the BoffinFrontend.map, but keeps them in the array
BoffinFrontend.clearOverlays = function() {
  if (BoffinFrontend.markersArray) {
    for (i in BoffinFrontend.markersArray) {
      BoffinFrontend.markersArray[i].setMap(null);
    }
  }
}

// Shows any overlays currently in the array
BoffinFrontend.showOverlays = function() {
  if (BoffinFrontend.markersArray) {
    for (i in BoffinFrontend.markersArray) {
      BoffinFrontend.markersArray[i].setMap(BoffinFrontend.map);
    }
  }
}

// Deletes all markers in the array by removing references to them
BoffinFrontend.deleteOverlays = function() {
  if (BoffinFrontend.markersArray) {
    for (i in BoffinFrontend.markersArray) {
      BoffinFrontend.markersArray[i].setMap(null);
    }
    BoffinFrontend.markersArray.length = 0;
  }
}