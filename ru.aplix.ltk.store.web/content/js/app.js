angular.module("rfid-tag-store", ["ui.bootstrap", "notifier"])
.config(function($locationProvider) {
	$locationProvider.hashPrefix('!');
})
.config(function($routeProvider) {
	$routeProvider.when(
			'/collectors',
			{
				templateUrl: 'collectors/collectors.html'
			})
	.otherwise({redirectTo: '/collectors'});
})
.factory('$rfCollectors', function() {
	function RfCollector(collectors) {
		this.collectors = collectors;
		this.remoteURL = null;
	}

	RfCollector.prototype.getName = function() {
		return this.remoteURL;
	};

	RfCollector.prototype.create = function() {
		this.collectors.list.push(this);
	};

	function RfCollectors() {
		this.list = [];
	}

	RfCollectors.prototype.newCollector = function() {
		return new RfCollector(this);
	};

	return new RfCollectors();
});

function NavCtrl($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
}
