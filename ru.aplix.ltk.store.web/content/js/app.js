angular.module(
		"rfid-tag-store",
		[
			"ngResource",
			"ui.bootstrap",
			"notifier",
			"rfid-tag-store.receivers",
			"rfid-tag-store.tags",
		])
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
