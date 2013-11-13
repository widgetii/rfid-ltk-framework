function RfidTagStore() {}

RfidTagStore.prototype.load = function() {
	var self = this;
	var request = new XMLHttpRequest();
	request.open("GET", "rcm/ui-scripts.json", true);
	request.responseType = "json";
	request.onload = function() {
		self.init(request.response);
	};
	request.send();
};

RfidTagStore.prototype.init = function(response) {
	if (typeof response == "string") response = angular.fromJson(response);
	var moduleIds = [
	 "ngResource",
	 "ui.bootstrap",
	 "notifier",
	 "rfid-tag-store.receivers",
	 "rfid-tag-store.tags",
	].concat(response.angularModuleIds);
	var scripts = [
	 "js/angular-locale_ru-ru.js",
	 "js/angular-resource.js",
	 "js/ui-bootstrap-tpls.js",
	 "js/notifier.js",
	 "js/app.js",
	 "receivers/receivers.js",
	 "tags/tags.js"
	].concat(response.scriptURLs);
	var head = document.getElementsByTagName("head").item(0);
	var scriptsToLoad = {
		count: scripts.length
	};
	var self = this;
	for (var i = 0; i < scripts.length; ++i) {
		var script = document.createElement("script");
		script.setAttribute("type", "text/javascript");
		script.setAttribute("src", scripts[i]);
		script.addEventListener("load", function() {
			if (!--scriptsToLoad.count) self.bootstrap(moduleIds);
		});
		head.appendChild(script);
	}
};

RfidTagStore.prototype.createModule = function(moduleIds) {
	angular.module('rfid-tag-store', moduleIds)
	.config(function($locationProvider) {
		$locationProvider.hashPrefix('!');
	})
	.config(function($routeProvider) {
		$routeProvider
		.when('/receivers', {templateUrl: 'receivers/receivers.html'})
		.when('/tags', {templateUrl: 'tags/tags.html'})
		.when('/tags/:receiver', {templateUrl: 'tags/tags.html'})
		.otherwise({redirectTo: '/receivers'});
	})
	.controller("NavCtrl", function($scope, $location) {
		$scope.navClass = function(path) {
			if ($location.path().substring(0, path.length) == path) {
				return "active";
			}
			return null;
		};
	})
	.filter('timestamp', function($filter) {
		return function(timestamp) {
			var time = $filter('date')(timestamp, 'yyyy-MM-dd HH:mm:ss');
			var ms = timestamp % 1000;
			if (ms < 10) return time + ".00" + ms;
			if (ms < 100) return time + ".0" + ms;
			return time + "." + ms;
		};
	});
};

RfidTagStore.prototype.bootstrap = function(moduleIds) {
	this.createModule(moduleIds);
	angular.bootstrap(document, ['rfid-tag-store']);
};

rfidTagStore = new RfidTagStore();
angular.element(document).ready(function() {
	rfidTagStore.load();
});
