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
				"stores/:storeId.do",
				{
					storeId: '@id'
				},
				{
					list: {
						method: 'GET',
						url: "stores/list.do",
						isArray: true,
					},
					create: {
						method: 'PUT',
						url: 'stores/create.do',
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
		return new RfStore();
	};

	return new RfStores();
});

function NavCtrl($scope, $location) {
	$scope.navClass = function(path) {
		return $location.path() == path ? "active" : null;
	};
}
