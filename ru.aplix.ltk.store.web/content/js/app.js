angular.module("rfid-tag-store", ["ui.bootstrap"])
.config(function($locationProvider) {
	$locationProvider.hashPrefix('!');
}).config(function($routeProvider) {
	$routeProvider.when(
			'/readers',
			{
				templateUrl: 'readers/readers.html',
				controller: ReadersCtrl
			})
	.otherwise({redirectTo: '/readers'});
});

function NavCtrl($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
}
