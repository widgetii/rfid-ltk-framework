function RfStoresCtrl($scope, $rfStores) {
	$scope.stores = $rfStores;
}

function NewRfStoreCtrl($scope, $rfStores, $notifier) {
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
	function stopUpdate() {
		$scope.updating = false;
	}
	$scope.create = function() {
		startUpdate();
		$scope.newStore.create(
				function() {
					$scope.newStore = $rfStores.newStore();
					stopUpdate();
				},
				stopUpdate);
	};
}
