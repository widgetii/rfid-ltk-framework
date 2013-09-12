function RfCollectorsCtrl($scope, $rfCollectors) {
	$scope.collectors = $rfCollectors;
}

function NewRfCollectorCtrl($scope, $rfCollectors) {
	$scope.newCollector = $rfCollectors.newCollector();
	$scope.correctRemoteURL = function() {
		var collector = $scope.newCollector;
		var url = collector.remoteURL;
		if (typeof url !== "string") return;
		if (url.indexOf("://") >= 0) return;
		if ("http:/".startsWith(url)) return;
		if ("https:/".startsWith(url)) return;
		collector.remoteURL = "http://" + url;
	};
	$scope.create = function() {
		var newCollector = $scope.newCollector;
		$scope.newCollector = $rfCollectors.newCollector();
		newCollector.create();
	};
}
