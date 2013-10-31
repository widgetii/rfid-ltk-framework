angular.module('rfid-tag-store.tags', ["notifier"])
.controller("RfTagsCtrl", function($scope, $http, $notifier) {
	$scope.query = {};
	$scope.state = {
		inProgress: false
	};
	$scope.tags = {totalCount: 0, events: []};
	$scope.receiverName = function(receiver) {
		if (!receiver.remoteURL) return "#" + receiver.id;
		return receiver.remoteURL + " (#" + receiver.id + ")";
	};
	$scope.search = function() {
		var state = $scope.state;
		if (state.inProgress) return;
		var query = $scope.query;
		if (!query.limit) query.limit = 50;
		var loading = $notifier.info("Загрузка...");
		state.inProgress = true;
		function done() {
			state.inProgress = false;
			loading.close();
		}
		$http.post("tags/find.json", query, {requestType: "json"})
		.success(function(data, status) {
			done();
			$scope.tags = data;
		})
		.error(function(data, status) {
			done();
			$notifier.error(
					"Не удалось найти теги",
					"ОШИБКА " + status);
		});
	};
});
