angular.module("rfid-tag-store", ["ngResource", "ui.bootstrap", "notifier"])
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
.factory('$rfStores', function($resource) {
	function RfStores() {
		this.list = [];
		var self = this;

		this.RfStore = $resource(
				"stores/:storeId.json",
				{
					storeId: '@id'
				},
				{
					all: {
						method: 'GET',
						url: "stores/all.json",
						isArray: true,
					},
					create: {
						method: 'PUT',
						url: 'stores/create.json',
						transformResponse: function (store) {
							self.list.push(store);
							return store;
						}
					},
					save: {
						method: 'PUT',
					},
					del: {
						method: 'DELETE',
					}
				});

		this.RfStore.prototype.getName = function() {
			return this.remoteURL;
		};
	}

	RfStores.prototype.newStore = function() {
		var RfStore = this.RfStore;
		return new RfStore();
	};

	return new RfStores();
});

function NavCtrl($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
}
