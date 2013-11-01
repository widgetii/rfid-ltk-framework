angular.module('rfid-tag-store.tags', ["notifier"])
.controller(
		"RfTagsCtrl",
		function(
				$scope,
				$http,
				$notifier,
				$routeParams,
				$location) {
	var state = {
		inProgress: false
	};
	var query = {
		receiver: $routeParams.receiver != "all" ? $routeParams.receiver : null,
		tag: $routeParams.tag,
		since: $routeParams.since,
		offset: $routeParams.offset,
		limit: $routeParams.limit
	};
	var tags = {
		totalCount: 0,
		events: []
	};

	$scope.state = state;
	$scope.query = query;
	$scope.tags = tags;

	$scope.receiverName = function(receiver) {
		if (!receiver.remoteURL) return "#" + receiver.id;
		return receiver.remoteURL + " (#" + receiver.id + ")";
	};

	$scope.search = function() {
		if (state.inProgress) return;
		state.inProgress = true;
		var receiver = query.receiver ? query.receiver : "all";
		var s = {};
		if (query.tag) s.tag = query.tag;
		if (query.since) s.since = query.since;
		if (query.offset) s.offset = query.offset;
		if (query.limit) s.limit = query.limit;
		$location.path("/tags/" + receiver).search(s);
	};

	function find() {
		var loading = $notifier.info("Загрузка...");
		state.inProgress = true;
		function done() {
			state.inProgress = false;
			loading.close();
		}
		$http.post("tags/find.json", query, {requestType: "json"})
		.success(function(data, status) {
			done();
			angular.copy(data, tags);
		})
		.error(function(data, status) {
			done();
			$notifier.error(
					"Не удалось найти теги",
					"ОШИБКА " + status);
		});
	}

	if ($routeParams.receiver) find();
});
