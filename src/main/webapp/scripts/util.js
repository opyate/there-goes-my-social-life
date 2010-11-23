/**
 * Adding GMaps v2 functionality back in.
 */

/**
 * Returns an XMLHttp instance to use for asynchronous
 * downloading. This method will never throw an exception, but will
 * return NULL if the browser does not support XmlHttp for any reason.
 * @return {XMLHttpRequest|Null}
 */
function createXmlHttpRequest() {
    try {
        if (typeof ActiveXObject != 'undefined') {
            return new ActiveXObject('Microsoft.XMLHTTP');
        } else if (window["XMLHttpRequest"]) {
            return new XMLHttpRequest();
        }
    } catch (e) {
        changeStatus(e);
    }
    return null;
}
;

/**
 * This functions wraps XMLHttpRequest open/send function.
 * It lets you specify a URL and will call the callback if
 * it gets a status code of 200.
 * @param {String} url The URL to retrieve
 * @param {Function} callback The function to call once retrieved.
 */
function downloadUrl(url, callback) {
    var status = -1;
    var request = createXmlHttpRequest();
    if (!request) {
        return false;
    }

    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            try {
                status = request.status;
            } catch (e) {
                // Usually indicates request timed out in FF.
            }
            if (status == 200) {
                callback(request.responseXML, request.status);
                request.onreadystatechange = function() {
                };
            }
        }
    }
    request.open('GET', url, true);
    try {
        request.send(null);
    } catch (e) {
        changeStatus(e);
    }
}
;

function downloadScript(url) {
    var script = document.createElement('script');
    script.src = url;
    document.body.appendChild(script);
}

function GXml() {
}
GXml.value = value;
GXml.parse = parse;

function value(node) {
    if (!node) {
        return"";
    }
    var retStr = "";
    if (node.nodeType == 3 || node.nodeType == 4 || node.nodeType == 2) {
        retStr += node.nodeValue;
    } else if (node.nodeType == 1 || node.nodeType == 9 || node.nodeType == 11) {
        for (var i = 0; i < node.childNodes.length; ++i) {
            retStr += arguments.callee(node.childNodes[i]);
        }
    }
    return retStr;
}


function parse(textDoc) {
    try {
        if (typeof ActiveXObject != "undefined" && typeof GetObject != "undefined") {
            var b = new ActiveXObject("Microsoft.XMLDOM");
            b.loadXML(textDoc);
            return b;
        } else if (typeof DOMParser != "undefined") {
            return(new DOMParser()).parseFromString(textDoc, "text/xml");
        } else {
            return Wb(textDoc);
        }
    }
    catch(c) {
        P.incompatible("xmlparse");
    }
    try {
        return Wb(textDoc);
    }
    catch(c) {
        P.incompatible("xmlparse");
        return document.createElement("div");
    }
}

function P() {
}
P.write = function(a, b) {
}
        ;
P.writeRaw = function(a) {
}
        ;
P.writeXML = function(a) {
}
        ;
P.writeURL = function(a) {
}
        ;
P.dump = function(a) {
}
        ;
P.incompatible = function() {
}
        ;
P.clear = function() {
}
        ;

function Wb(a) {
    return null;
}
