var map, marker; // global variables

// Print message to log
function msg(text) { $("#log").prepend(text + "<br/>"); }

function init() { // Execute after login succeed
    var sess = wialon.core.Session.getInstance(); // get instance of current Session
    // specify what kind of data should be returned
    var flags = wialon.item.Item.dataFlag.base | wialon.item.Unit.dataFlag.lastMessage;

    sess.loadLibrary("itemIcon"); // load Icon Library
    sess.updateDataFlags( // load items to current session
        [{type: "type", data: "avl_unit", flags: flags,mode: 0}], // Items specification
        function (code) { // updateDataFlags callback
            if (code) { msg(wialon.core.Errors.getErrorText(code)); return; } // exit if error code

            var units = sess.getItems("avl_unit"); // get loaded 'avl_resource's items
            if (!units || !units.length){ msg("No units found"); return; } // check if units found
            for (var i = 0; i< units.length; i++) // construct Select list using found resources
                $("#units").append("<option value='"+ units[i].getId() +"'>"+ units[i].getName()+ "</option>");
            // bind action to select change event
            $("#units").change( showUnit );
        });
}

function showUnit(){ // show selected unit on map
    var val = $("#units").val(); // get selected unit id
    if(!val) return; // exit if no unit selected
    var unit = wialon.core.Session.getInstance().getItem(val); // get unit by id
    if(!unit) return; // exit if no unit
    var pos = unit.getPosition(); // get unit position
    if(!pos) return; // exit if no position
    // print message with information about selected unit and its position
    msg("<img src='"+ unit.getIconUrl(32) +"'/> " + unit.getName()+" selected. Position "+ pos.x+", "+pos.y);

    if (map) { // check if map created
        var icon = L.icon({
            iconUrl: unit.getIconUrl(32)
        });
        if (!marker) {
            marker = L.marker({lat: pos.y, lng: pos.x}, {icon: icon}).addTo(map);
        } else {
            marker.setLatLng({lat: pos.y, lng: pos.x});
            marker.setIcon(icon);
        }
        map.setView({lat: pos.y, lng: pos.x});
    }
}

function initMap() {
    // create a map in the "map" div, set the view to a given place and zoom
    map = L.map('map').setView([53.9, 27.55], 10);

    // add an OpenStreetMap tile layer
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://gurtam.com">Gurtam</a>'
    }).addTo(map);
}

// execute when DOM ready
$(document).ready(function () {

    wialon.core.Session.getInstance().initSession("https://hst-api.wialon.com"); // init session
    // For more info about how to generate token check
    // http://sdk.wialon.com/playground/demo/app_auth_token
    wialon.core.Session.getInstance().loginToken("5dce19710a5e26ab8b7b8986cb3c49e58C291791B7F0A7AEB8AFBFCEED7DC03BC48FF5F8", "", // try to login
        function (code) { // login callback
            // if error code - print error message
            if (code){ msg(wialon.core.Errors.getErrorText(code)); return; }
            msg("Logged successfully");
            initMap();
            init(); // when login suceed then run init() function
        });
});
