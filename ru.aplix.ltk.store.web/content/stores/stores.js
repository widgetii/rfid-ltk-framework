angular.module(
		'rfid-tag-store.stores',
		[
			"ngResource",
			"notifier"
		])
.factory('$rfStores', function($resource, $timeout, $notifier) {
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

		this.RfStore.prototype.getStatus = function() {
			switch (this.status) {
			case "inactive":
				return "Отключено";
			case "active":
				return "Подключено";
			case "ready":
				return "Работает";
			case "error":
				return "Ошибка";
			}
			return "???";
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
				var index = stores.storeIndexById(store.id);
				if (index >= 0) {
					stores.list[i] = store;
					return;
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

		this.RfStore.prototype.del = function(success, error) {
			var store = this;
			function removeStore() {
				var index = stores.list.indexOf(store);
				if (index >= 0) stores.list.splice(index, 1);
			}
			store.$del(
					function() {
						removeStore();
						if (success) success(store);
					},
					function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось удалить хранилище");
					});
		};
	}

	RfStores.prototype.newStore = function() {
		var RfStore = this.RfStore;
		return new RfStore();
	};

	RfStores.prototype.storeIndexById = function(id) {
		for (var i = 0; i < len; ++i) {
			if (this.list[i].id == id) {
				return i;
			}
		}
		return -1;
	};

	var stores = new RfStores();

	function refreshStores() {
		stores.RfStore.all(
				function(list) {
					stores.list = list;
				},
				function(response) {
					$notifier.error(
							"ОШИБКА " + response.status,
							"Не удалось загрузить список хранилищ");
				});
		$timeout(refreshStores, 3000);
	}

	refreshStores();

	return stores;
})
.controller("RfStoresCtrl", function($scope, $rfStores) {
	$scope.stores = $rfStores;
	$scope.expanded = {};
})
.controller("RfStoreCtrl", function($scope) {
	$scope.isCollapsed = function() {
		return !$scope.expanded[$scope.store.id];
	};
	$scope.toggle = function() {
		var id = $scope.store.id;
		var expanded = $scope.expanded;
		if (expanded[id]) {
			delete expanded[id];
		} else {
			expanded[id] = true;
		}
	};
	$scope.del = function() {
		$scope.store.del();
	};
})
.controller("NewRfStoreCtrl", function(
		$scope,
		$document,
		$rfStores,
		$notifier) {
	$scope.updating = false;
	$scope.newStore = $rfStores.newStore();
	$scope.correctRemoteURL = function() {
		var store = $scope.newStore;
		var url = store.remoteURL;
		if (typeof url !== "string") return;
		if (url.indexOf("://") >= 0) return;
		if ("http:/".startsWith(url)) return;
		if ("https:/".startsWith(url)) return;
		store.remoteURL = "http://" + url;
	};
	function startUpdate() {
		$scope.updating = true;
	}
	function endUpdate(success) {
		$scope.updating = false;
		if (success) {
			angular.element($document[0].getElementById("newStoreForm"))
			.controller("form")
			.$setDirty(false);
		}
	}
	$scope.create = function() {
		if ($scope.updating) return;
		startUpdate();
		$scope.newStore.create(
				function() {
					$scope.newStore = $rfStores.newStore();
					endUpdate(true);
				},
				endUpdate);
	};
});
