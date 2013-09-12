angular.module("rfid-tag-store", ["ui.bootstrap", "notifier"])
.config(function($locationProvider) {
	$locationProvider.hashPrefix('!');
})
.config(function($routeProvider) {
	$routeProvider.when(
			'/readers',
			{
				templateUrl: 'readers/readers.html'
			})
	.otherwise({redirectTo: '/readers'});
})
.factory('$rfReaders', function() {
	function RfReader(readers) {
		this.readers = readers;
		this.collectorURL = null;
	}

	RfReader.prototype.getName = function() {
		return this.collectorURL;
	};

	RfReader.prototype.create = function() {
		this.readers.list.push(this);
	};

	function RfReaders() {
		this.list = [];
	}

	RfReaders.prototype.newReader = function() {
		return new RfReader(this);
	};

	return new RfReaders();
});

function NavCtrl($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
}
