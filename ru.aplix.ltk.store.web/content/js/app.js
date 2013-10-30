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
	.otherwise({redirectTo: '/receivers'});
})
.controller("NavCtrl", function($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
});
