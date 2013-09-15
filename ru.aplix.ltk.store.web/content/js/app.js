angular.module(
		"rfid-tag-store",
		[
			"ngResource",
			"ui.bootstrap",
			"notifier",
			"rfid-tag-store.stores"
		])
.config(function($locationProvider) {
	$locationProvider.hashPrefix('!');
})
.config(function($routeProvider) {
	$routeProvider.when(
			'/stores',
			{
				templateUrl: 'stores/stores.html'
			})
	.otherwise({redirectTo: '/stores'});
})
.controller("NavCtrl", function($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
});
