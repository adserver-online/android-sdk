package com.adserver.mraid;

public class MraidJsLibrary {
    public static final String JAVASCRIPT_SOURCE = "(function () {\n" +
            "    var mraid = window.mraid = {};\n" +
            "\n" +
            "    var STATES = mraid.STATES = {\n" +
            "        \"LOADING\": \"loading\",\n" +
            "        \"DEFAULT\": \"default\",\n" +
            "        \"EXPANDED\": \"expanded\",\n" +
            "        \"RESIZED\": \"resized\",\n" +
            "        \"HIDDEN\": \"hidden\"\n" +
            "    };\n" +
            "\n" +
            "    var PLACEMENT_TYPES = mraid.PLACEMENTS = {\n" +
            "        \"INLINE\": \"inline\",\n" +
            "        \"INTERSTITIAL\": \"interstitial\"\n" +
            "    };\n" +
            "\n" +
            "    var POSITIONS = mraid.POSITIONS = {\n" +
            "        \"CENTER\": \"center\",\n" +
            "        \"TOP_LEFT\": \"top-left\",\n" +
            "        \"TOP_CENTER\": \"top-center\",\n" +
            "        \"TOP_RIGHT\": \"top-right\",\n" +
            "        \"BOTTOM_LEFT\": \"bottom-left\",\n" +
            "        \"BOTTOM_CENTER\": \"bottom-center\",\n" +
            "        \"BOTTOM_RIGHT\": \"bottom-right\"\n" +
            "    };\n" +
            "\n" +
            "    var ORIENTATIONS = mraid.ORIENTATIONS = {\n" +
            "        \"PORTRAIT\": \"portrait\",\n" +
            "        \"LANDSCAPE\": \"landscape\",\n" +
            "        \"NONE\": \"none\"\n" +
            "    };\n" +
            "\n" +
            "    var EVENTS = mraid.EVENTS = {\n" +
            "        \"READY\": \"ready\",\n" +
            "        \"SIZE_CHANGE\": \"sizeChange\",\n" +
            "        \"STATE_CHANGE\": \"stateChange\",\n" +
            "        \"VIEWABLE_CHANGE\": \"viewableChange\",\n" +
            "        \"ERROR\": \"error\"\n" +
            "    };\n" +
            "\n" +
            "    var FEATURES = mraid.FEATURES = {\n" +
            "        \"SMS\": \"sms\",\n" +
            "        \"TEL\": \"tel\",\n" +
            "        \"STORE_PICTURE\": \"storePicture\",\n" +
            "        \"INLINE_VIDEO\": \"inlineVideo\",\n" +
            "        \"CALENDAR\": \"calendar\"\n" +
            "    };\n" +
            "\n" +
            "    var NATIVE_ENDPOINTS = mraid.NATIVE_ENDPOINTS = {\n" +
            "        \"EXPAND\": \"expand\",\n" +
            "        \"OPEN\": \"open\",\n" +
            "        \"PLAY_VIDEO\": \"playVideo\",\n" +
            "        \"RESIZE\": \"resize\",\n" +
            "        \"STORE_PICTURE\": \"storePicture\",\n" +
            "        \"CREATE_CALENDAR_EVENT\": \"createCalendarEvent\",\n" +
            "        \"CALL_NUMBER\": \"callNumber\",\n" +
            "        \"SET_ORIENTATION_PROPERTIES\": \"setOrientationProperties\",\n" +
            "        \"SET_RESIZE_PROPERTIES\": \"setResizeProperties\",\n" +
            "        \"SET_EXPAND_PROPERTIES\": \"setExpandProperties\",\n" +
            "        \"REPORT_DOM_SIZE\": \"reportDOMSize\",\n" +
            "        \"REPORT_JS_LOG\": \"reportJSLog\",\n" +
            "        \"CLOSE\": \"close\",\n" +
            "    }\n" +
            "\n" +
            "    var state = STATES.LOADING;\n" +
            "    var listeners = []; // contains arrays of listeners for each event type\n" +
            "    var placementType = PLACEMENT_TYPES.INLINE;\n" +
            "    var supportedFeatures = [];\n" +
            "    mraid.debug = true;\n" +
            "\n" +
            "    var orientationProperties = {\n" +
            "        \"allowOrientationChange\": false,\n" +
            "        \"forceOrientation\": ORIENTATIONS.NONE\n" +
            "    }\n" +
            "\n" +
            "    var expandProperties = {\n" +
            "        \"width\": 0,\n" +
            "        \"height\": 0,\n" +
            "        \"useCustomClose\": false,\n" +
            "        \"isModal\": true\n" +
            "    }\n" +
            "\n" +
            "    var resizeProperties = {\n" +
            "        \"width\": undefined,\n" +
            "        \"height\": undefined,\n" +
            "        \"offsetX\": undefined,\n" +
            "        \"offsetY\": undefined,\n" +
            "        \"customClosePosition\": POSITIONS.TOP_RIGHT,\n" +
            "        \"allowOffscreen\": true\n" +
            "    }\n" +
            "\n" +
            "    var version = \"2.0\";\n" +
            "    var defaultPosition = {x: 0, y: 0, height: 50, width: 50};\n" +
            "    var currentPosition = {x: 0, y: 0, height: 50, width: 50};\n" +
            "    var maxSize = {x: 0, y: 0, height: 0, width: 0};\n" +
            "    var screenSize = {x: 0, y: 0, height: 0, width: 0};\n" +
            "    var isViewable = false;\n" +
            "\n" +
            "    mraid.addEventListener = function (event, listener) {\n" +
            "        if (listeners.containsListener(event, listener)) {\n" +
            "            warning('addEventListener - this function already registered for (' + event + ') event.');\n" +
            "            return;\n" +
            "        }\n" +
            "        log('addEventListener (event = ' + event + ')');\n" +
            "        listeners[event] = listeners[event] || [];\n" +
            "        listeners[event].push(listener);\n" +
            "    }\n" +
            "\n" +
            "    mraid.removeEventListener = function (event, listener) {\n" +
            "        listeners.removeListener(event, listener);\n" +
            "    }\n" +
            "\n" +
            "    mraid.createCalendarEvent = function (params) {\n" +
            "        log('createCalendarEvent');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.CREATE_CALENDAR_EVENT, JSON.stringify(params));\n" +
            "    }\n" +
            "\n" +
            "    mraid.close = function () {\n" +
            "        log('close');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.CLOSE);\n" +
            "    }\n" +
            "\n" +
            "    mraid.expand = function (uri) {\n" +
            "        if (placementType !== PLACEMENT_TYPES.INLINE || (state !== STATES.DEFAULT && state !== STATES.RESIZED)) {\n" +
            "            warning('expand called while in invalid state.');\n" +
            "            return;\n" +
            "        }\n" +
            "        log('expand');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.EXPAND, uri);\n" +
            "    }\n" +
            "\n" +
            "    mraid.getCurrentPosition = function () {\n" +
            "        log('getCurrentPosition');\n" +
            "        return currentPosition;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getDefaultPosition = function () {\n" +
            "        log('getDefaultPosition');\n" +
            "        return defaultPosition;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getExpandProperties = function () {\n" +
            "        log('getExpandProperties');\n" +
            "        return expandProperties;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getMaxSize = function () {\n" +
            "        log('getMaxSize');\n" +
            "        return maxSize;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getOrientationProperties = function () {\n" +
            "        log('getOrientationProperties');\n" +
            "        return orientationProperties;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getPlacementType = function () {\n" +
            "        log('getPlacementType');\n" +
            "        return placementType;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getResizeProperties = function () {\n" +
            "        log('getResizeProperties');\n" +
            "\n" +
            "        return resizeProperties;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    mraid.getScreenSize = function () {\n" +
            "        log('getScreenSize');\n" +
            "        return screenSize;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getState = function () {\n" +
            "        log('getState (state = ' + state + \")\");\n" +
            "        return state;\n" +
            "    }\n" +
            "\n" +
            "    mraid.getVersion = function () {\n" +
            "        log('getVersion');\n" +
            "        return version;\n" +
            "    }\n" +
            "\n" +
            "    mraid.isViewable = function () {\n" +
            "        log('isViewable - ' + isViewable);\n" +
            "        return isViewable;\n" +
            "    }\n" +
            "\n" +
            "    mraid.open = function (uri) {\n" +
            "        log('open');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.OPEN, uri);\n" +
            "    }\n" +
            "\n" +
            "    mraid.playVideo = function (uri) {\n" +
            "        log('playVideo');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.PLAY_VIDEO, uri);\n" +
            "    }\n" +
            "\n" +
            "    mraid.resize = function () {\n" +
            "        log('resize - ' + JSON.stringify(resizeProperties));\n" +
            "        if (state == STATES.EXPANDED) {\n" +
            "            error(\"Resize called while in expanded state.\", true);\n" +
            "        }\n" +
            "        if (resizeProperties.allowOffscreen === true) {\n" +
            "            var pos = resizeProperties.customClosePosition || \"top-right\";\n" +
            "            var frame = currentPosition;\n" +
            "            var valid = true;\n" +
            "            if (pos.indexOf(\"right\") >= 0) {\n" +
            "                var offscreenLeft = frame.x + resizeProperties.width + resizeProperties.offsetX > screenSize.width - 50;\n" +
            "                var offscreenRight = frame.x + resizeProperties.width + resizeProperties.offsetX < 50;\n" +
            "                if (offscreenLeft || offscreenRight) {\n" +
            "                    valid = false;\n" +
            "                }\n" +
            "            }\n" +
            "            if (pos.indexOf(\"left\") >= 0) {\n" +
            "                var offscreenLeft = frame.x + resizeProperties.offsetX < 0;\n" +
            "                var offscreenRight = frame.x + resizeProperties.offsetX > screenSize.width - 50;\n" +
            "                if (offscreenLeft || offscreenRight) {\n" +
            "                    valid = false\n" +
            "                }\n" +
            "            }\n" +
            "            if (pos.indexOf(\"top\") >= 0) {\n" +
            "                mraid.log(JSON.stringify(currentPosition), true);\n" +
            "                mraid.log(JSON.stringify(resizeProperties), true);\n" +
            "                var offscreenTop = frame.y + resizeProperties.offsetY < 0;\n" +
            "                var offscreenBottom = frame.y + resizeProperties.offsetY > screenSize.height - 50;\n" +
            "                if (offscreenTop || offscreenBottom) {\n" +
            "                    valid = false\n" +
            "                }\n" +
            "            }\n" +
            "            if (pos.indexOf(\"bottom\") >= 0) {\n" +
            "                var offscreenTop = frame.y + resizeProperties.height + resizeProperties.offsetY < 50;\n" +
            "                var offscreenBottom = frame.y + resizeProperties.height + resizeProperties.offsetY > screenSize.height - 50;\n" +
            "                if (offscreenTop || offscreenBottom) {\n" +
            "                    valid = false\n" +
            "                }\n" +
            "            }\n" +
            "            if (!valid) {\n" +
            "                error(\"Current resize properties would result in the close region being off screen. Ignoring resize.\", true);\n" +
            "            }\n" +
            "        }\n" +
            "        if (resizeProperties.width && resizeProperties.height) {\n" +
            "            invokeSDK(NATIVE_ENDPOINTS.RESIZE);\n" +
            "        } else {\n" +
            "            error(\"Resize properties must be set before calling mraid.resize()\", true);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.setExpandProperties = function (properties) {\n" +
            "        log('setExpandProperties');\n" +
            "        setProperties('setExpandProperties', expandProperties, properties)\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.SET_EXPAND_PROPERTIES, JSON.stringify(properties))\n" +
            "    }\n" +
            "\n" +
            "    mraid.setOrientationProperties = function (properties) {\n" +
            "        log('setOrientationProperties');\n" +
            "        setProperties('setOrientationProperties', orientationProperties, properties);\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.SET_ORIENTATION_PROPERTIES, JSON.stringify(properties))\n" +
            "    }\n" +
            "\n" +
            "    mraid.setResizeProperties = function (properties) {\n" +
            "        log('setResizeProperties - ' + JSON.stringify(properties));\n" +
            "        setProperties('setResizeProperties', resizeProperties, properties);\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.SET_RESIZE_PROPERTIES, JSON.stringify(properties))\n" +
            "    }\n" +
            "\n" +
            "    mraid.storePicture = function (uri) {\n" +
            "        log('storePicture');\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.STORE_PICTURE, uri);\n" +
            "    }\n" +
            "\n" +
            "    mraid.supports = function (feature) {\n" +
            "        log('supports - ' + feature + ' : ' + (supportedFeatures[feature] === true));\n" +
            "        return supportedFeatures[feature] === true;\n" +
            "    }\n" +
            "\n" +
            "    mraid.useCustomClose = function (val) {\n" +
            "        log('useCustomClose');\n" +
            "        expandProperties.useCustomClose = val;\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.SET_EXPAND_PROPERTIES, JSON.stringify(expandProperties))\n" +
            "    }\n" +
            "\n" +
            "    mraid.setState = function (toState) {\n" +
            "        log(\"setState (\" + toState + \")\");\n" +
            "        if (state != toState) {\n" +
            "            state = toState;\n" +
            "            mraid.fireEvent(EVENTS.STATE_CHANGE, state);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.fireEvent = function (event, args) {\n" +
            "        log(\"fire event (\" + event + \")\");\n" +
            "        if (listeners.containsEvent(event)) {\n" +
            "            listeners.invoke(event, args);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.setSupports = function (feature, isSupported) {\n" +
            "        log(\"set feature \" + feature + \" \" + isSupported);\n" +
            "        supportedFeatures[feature] = isSupported;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setPlacementType = function (type) {\n" +
            "        if (type == PLACEMENT_TYPES.INLINE || type == PLACEMENT_TYPES.INTERSTITIAL) {\n" +
            "            placementType = type;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.setCurrentPosition = function (pos) {\n" +
            "        log(\"set current position \" + JSON.stringify(pos));\n" +
            "        currentPosition = pos;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setDefaultPosition = function (pos) {\n" +
            "        log(\"set default position \" + JSON.stringify(pos));\n" +
            "        defaultPosition = pos;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setMaxSize = function (size) {\n" +
            "        log(\"set max size \" + JSON.stringify(size));\n" +
            "        maxSize = size;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setScreenSize = function (size) {\n" +
            "        log(\"set screen size - \" + JSON.stringify(size));\n" +
            "        screenSize = size;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setVersion = function (ver) {\n" +
            "        log(\"set version - \" + ver);\n" +
            "        version = ver;\n" +
            "    }\n" +
            "\n" +
            "    mraid.setIsViewable = function (val) {\n" +
            "        log(\"set is viewable - \" + val);\n" +
            "        isViewable = val;\n" +
            "    }\n" +
            "\n" +
            "    document.addEventListener(\"DOMContentLoaded\", function (event) {\n" +
            "        var detectedSize = findLargestElement();\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.REPORT_DOM_SIZE, JSON.stringify(detectedSize));\n" +
            "    });\n" +
            "\n" +
            "    function invokeSDK(endpoint, args) {\n" +
            "        var iframe = document.createElement(\"IFRAME\");\n" +
            "        var qs = args != null ? '?args=' + encodeURIComponent(args) : '';\n" +
            "        iframe.setAttribute(\"src\", \"mraid://\" + endpoint + qs);\n" +
            "        document.documentElement.appendChild(iframe);\n" +
            "        iframe.parentNode.removeChild(iframe);\n" +
            "        iframe = null;\n" +
            "    }\n" +
            "\n" +
            "    mraid.findLargestElement = function () {\n" +
            "        return findLargestElement();\n" +
            "    }\n" +
            "\n" +
            "    function findLargestElement() {\n" +
            "        // Get all elements that have width defined (we can consider their height as well\n" +
            "        var elements = document.querySelectorAll(\"body > *\");\n" +
            "        var largestWidth = 0;\n" +
            "        var largestHeight = 0;\n" +
            "        // Loop through them\n" +
            "        Array.prototype.forEach.call(elements, function (elem) {\n" +
            "            var compStyle = window.getComputedStyle(elem)\n" +
            "            if (compStyle.display != \"none\" && compStyle.visibility != \"hidden\") {\n" +
            "                // DOM may not have loaded, so we have to read the actual style property\n" +
            "                var w = parseInt(elem.offsetWidth);\n" +
            "                var h = parseInt(elem.offsetHeight);\n" +
            "                largestWidth = Math.max(largestWidth, w);\n" +
            "                largestHeight = Math.max(largestHeight, h);\n" +
            "            }\n" +
            "        });\n" +
            "        return {width: largestWidth, height: largestHeight};\n" +
            "    }\n" +
            "\n" +
            "    function setProperties(caller, ogProperties, newProperties) {\n" +
            "        // do some validation for specific cases, like required fields, or fields with allowed values.\n" +
            "        switch (caller) {\n" +
            "            case \"setOrientationProperties\":\n" +
            "                if (newProperties.forceOrientation && Object.keys(ORIENTATIONS).indexOf(newProperties.forceOrientation.toUpperCase()) < 0) {\n" +
            "                    error(caller + ' - property (forceOrientation) invalid value. ' + newProperties.forceOrientation, true);\n" +
            "                }\n" +
            "                break;\n" +
            "            case \"setExpandProperties\":\n" +
            "                // nothing custom required here.\n" +
            "                break;\n" +
            "            case \"setResizeProperties\":\n" +
            "                newProperties.width = parseInt(newProperties.width);\n" +
            "                newProperties.height = parseInt(newProperties.height);\n" +
            "                newProperties.offsetX = parseInt(newProperties.offsetX);\n" +
            "                newProperties.offsetY = parseInt(newProperties.offsetY);\n" +
            "\n" +
            "                if (isNaN(newProperties.width)) {\n" +
            "                    error(caller + ' - property (width) must be an integer, and is required.', true);\n" +
            "                } else if (newProperties.width < 50) {\n" +
            "                    error(caller + ' - property (width) must be at least 50 dp.', true);\n" +
            "                }\n" +
            "\n" +
            "                if (isNaN(newProperties.height)) {\n" +
            "                    error(caller + ' - property (height) must be an integer, and is required.', true);\n" +
            "                } else if (newProperties.height < 50) {\n" +
            "                    error(caller + ' - property (height) must be at least 50 dp.', true);\n" +
            "                }\n" +
            "\n" +
            "                if (isNaN(newProperties.offsetX)) {\n" +
            "                    error(caller + ' - property (offsetX) must be an integer, and is required.', true);\n" +
            "                }\n" +
            "\n" +
            "                if (isNaN(newProperties.offsetY)) {\n" +
            "                    error(caller + ' - property (offsetY) must be an integer, and is required.', true);\n" +
            "                }\n" +
            "\n" +
            "                if (isNaN(newProperties.allowOffscreen)) {\n" +
            "                    if (newProperties.width > screenSize.width || newProperties.height > screenSize.height) {\n" +
            "                        error(caller + ' - cannot set the width or height greater than screensize if \"allowOffscreen\" is false', true);\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                if (newProperties.customClosePosition === true && Object.values(POSITIONS).indexOf(newProperties.customClosePosition) < 0) {\n" +
            "                    error(caller + ' - property (customClosePosition) invalid value.  Default is \"top-right\"', true);\n" +
            "                }\n" +
            "                break;\n" +
            "        }\n" +
            "\n" +
            "        // generic validation.\n" +
            "        for (var property in newProperties) {\n" +
            "            // check if the property was not invalidated above, and make sure we aren't finding any inherited properties\n" +
            "            if (newProperties.hasOwnProperty(property) && newProperties[property] !== undefined) {\n" +
            "                if (!ogProperties.hasOwnProperty(property)) {\n" +
            "                    // original does not have this property, so it is invalid.\n" +
            "                    error(caller + ' - property (' + property + ') does not exist.')\n" +
            "                } else {\n" +
            "                    // We are through the gauntlet.  Go ahead and set the property for the original.\n" +
            "                    ogProperties[property] = newProperties[property];\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    listeners.invoke = function (event, args) {\n" +
            "        listeners[event].forEach(function (listener) {\n" +
            "            switch (event) {\n" +
            "                case EVENTS.READY:\n" +
            "                    listener();\n" +
            "                    break;\n" +
            "                case EVENTS.STATE_CHANGE:\n" +
            "                    listener(args);\n" +
            "                    break;\n" +
            "                case EVENTS.SIZE_CHANGE:\n" +
            "                    listener(args.width, args.height);\n" +
            "                    break;\n" +
            "                case EVENTS.VIEWABLE_CHANGE:\n" +
            "                    listener(args);\n" +
            "                    break;\n" +
            "                case EVENTS.ERROR:\n" +
            "                    listener(args);\n" +
            "                    break;\n" +
            "            }\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    listeners.containsEvent = function (event) {\n" +
            "        return Array.isArray(listeners[event]);\n" +
            "    }\n" +
            "\n" +
            "    listeners.containsListener = function (event, listener) {\n" +
            "        var listenerString = String(listener);\n" +
            "        if (Array.isArray(listeners[event])) {\n" +
            "            listeners[event].forEach(function (listener) {\n" +
            "                var thisStr = String(listener);\n" +
            "                if (thisStr === listenerString) {\n" +
            "                    return true;\n" +
            "                }\n" +
            "            });\n" +
            "        }\n" +
            "        return false;\n" +
            "    }\n" +
            "\n" +
            "    listeners.removeListener = function (event, listener) {\n" +
            "        var listenerString = String(listener);\n" +
            "        var index = -1;\n" +
            "        if (Array.isArray(listeners[event])) {\n" +
            "            listeners[event].forEach(function (listener) {\n" +
            "                var thisStr = String(listener);\n" +
            "                if (thisStr === listenerString) {\n" +
            "                    log(\"removing event listener (\" + event + \")\");\n" +
            "                    index = listeners[event].indexOf(listener);\n" +
            "                }\n" +
            "            });\n" +
            "        }\n" +
            "        if (index > 0) {\n" +
            "            listeners[event].splice(index, 1);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.log = function (str) {\n" +
            "        invokeSDK(NATIVE_ENDPOINTS.REPORT_JS_LOG, str);\n" +
            "    }\n" +
            "\n" +
            "    function log(str) {\n" +
            "        if (mraid.debug) {\n" +
            "            console.log('mraid.js::' + str);\n" +
            "            invokeSDK(NATIVE_ENDPOINTS.REPORT_JS_LOG, str);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    function error(str, throws) {\n" +
            "        if (mraid.debug) {\n" +
            "            invokeSDK(NATIVE_ENDPOINTS.REPORT_JS_LOG, \"ERROR -- \" + str);\n" +
            "        }\n" +
            "        console.error('mraid.js::' + str);\n" +
            "        if (throws) {\n" +
            "            throw new Error(\"mraid.js::\" + str);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    function warning(str) {\n" +
            "        if (mraid.debug) {\n" +
            "            console.warn('mraid.js::' + str);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    mraid.log(\"mraid.js loaded\");\n" +
            "})();".replaceAll("(?m)^\\s+", "").replaceAll("(?m)^//.*(?=\\n)", "");
}
