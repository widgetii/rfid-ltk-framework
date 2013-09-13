function RfStoresCtrl($scope, $rfStores) {
	$scope.stores = $rfStores;
}

function NewRfStoreCtrl($scope, $rfStores) {
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
	$scope.create = function() {
		var newStore = $scope.newStore;
		$scope.newStore = $rfStores.newStore();
		newStore.create();
	};
}
