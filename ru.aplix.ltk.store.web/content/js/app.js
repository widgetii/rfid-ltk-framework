angular.module(
		"rfid-tag-store",
		[
			"ngResource",
			"ui.bootstrap",
			"notifier"
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
.factory('$rfStores', function($resource, $notifier, $rootScope) {
	function RfStores() {
		this.list = [];
		var stores = this;

		this.RfStore = $resource(
				"stores/:storeId.json",
				{
					storeId: '@id'
				},
				{
					all: {
						method: 'GET',
						params: {storeId: "all"},
						isArray: true
					},
					create: {
						method: 'PUT',
						params: {storeId: 'create'}
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

		this.RfStore.prototype.create = function(success, error) {
			this.$create(
					{
						storeId: 'create'
					},
					function(store) {
						stores.list.push(store);
						if (success) success(store);
					}, function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось создать хранилище");
						if (error) error(response);
					});
		};

		this.RfStore.prototype.save = function(success, error) {
			function updateStore(store) {
				var len = stores.list.length;
				for (var i = 0; i < len; ++i) {
					var old = stores.list[i];
					if (old.id == store.id) {
						stores.list[i] = store;
						return;
					}
				}
				$notifier.error(
						"Ошибка обновления хранилища",
						"Неизвестное хранилище: " + store.id);
			}
			this.$save(
					function(store) {
						updateStore(store);
						if (success) success(store);
					},
					function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось обновить хранилище");
						if (error) error(response);
					});
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
