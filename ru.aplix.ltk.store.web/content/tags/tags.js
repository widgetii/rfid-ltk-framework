angular.module('rfid-tag-store.tags', ["notifier"])
.controller(
		"RfTagsCtrl",
		function(
				$scope,
				$http,
				$notifier,
				$routeParams,
				$location) {
	var DEFAULT_PAGE_SIZE = 50;
	var state = {
		inProgress: false
	};
	var query = {
		receiver: $routeParams.receiver != "all" ? $routeParams.receiver : null,
		tag: $routeParams.tag,
		since: $routeParams.since,
		page: $routeParams.page,
		pageSize: $routeParams.pageSize
			? $routeParams.pageSize : DEFAULT_PAGE_SIZE
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

	function search() {
		state.inProgress = true;
		var receiver = query.receiver ? query.receiver : "all";
		var s = {};
		if (query.tag) s.tag = query.tag;
		if (query.since) s.since = query.since;
		if (query.page && query.page > 1) s.page = query.page;
		if (query.pageSize && query.pageSize != DEFAULT_PAGE_SIZE) {
			s.pageSize = query.pageSize;
		}
		$location.path("/tags/" + receiver).search(s);
	}

	$scope.search = function() {
		if (state.inProgress) return;
		query.page = 0;
		search();
	};

	$scope.setPage = function(page) {
		query.page = page;
		search();
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
