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
		if (state.loading) state.loading.close();
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

	function newSearch() {
		delete query.page;
		search();
	}

	$scope.searchIfNotStarted = function() {
		if (!state.inProgress) newSearch();
	};

	$scope.resetTag = function() {
		delete query.tag;
		newSearch();
	};

	$scope.setPage = function(page) {
		query.page = page;
		search();
	};

	function find() {
		var loading = $notifier.info("Загрузка...");
		state.loading = loading;
		state.inProgress = true;
		function done() {
			if (state.loading != loading) return false;
			state.inProgress = false;
			loading.close();
			return true;
		}
		$http.post("tags/find.json", query, {responseType: "json"})
		.success(function(data, status) {
			if (done()) angular.copy(data, tags);
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
