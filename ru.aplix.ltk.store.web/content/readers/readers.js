function RfReadersCtrl($scope, $rfReaders) {
	$scope.readers = $rfReaders;
}

function NewRfReaderCtrl($scope, $rfReaders) {
	$scope.newReader = $rfReaders.newReader();
	$scope.correctCollectorURL = function() {
		var reader = $scope.newReader;
		var url = reader.collectorURL;
		if (typeof url !== "string") return;
		if (url.indexOf("://") >= 0) return;
		if ("http:/".startsWith(url)) return;
		if ("https:/".startsWith(url)) return;
		reader.collectorURL = "http://" + url;
	};
	$scope.createReader = function() {
		$scope.newReader.create();
		$scope.newReader = $rfReaders.newReader();
	};
}
