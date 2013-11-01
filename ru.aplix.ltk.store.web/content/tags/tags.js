angular.module('rfid-tag-store.tags', ["notifier"])
.controller(
		"RfTagsCtrl",
		function(
				$scope,
				$http,
				$notifier,
				$routeParams,
				$location,
				$timeout,
				$filter) {

	function Query() {
		this.DEFAULT_PAGE_SIZE = 50;
		if ($routeParams.receiver && $routeParams.receiver != "all") {
			this.receiver = $routeParams.receiver;
		}
		if ($routeParams.tag) this.tag = $routeParams.tag;
		if ($routeParams.since) {
			var since = new Date(parseInt($routeParams.since));
			this.sinceDate = new Date(
					since.getFullYear(),
					since.getMonth(),
					since.getDate());
		}
		this.page =
			$routeParams.page && $routeParams.page > 1 ? $routeParams.page : 1;
		this.pageSize =
			$routeParams.pageSize && $routeParams.pageSize > 0
			? $routeParams.pageSize : this.DEFAULT_PAGE_SIZE;

		this.inProgress = false;
	}

	Query.prototype.toRequest = function() {
		var req = {};
		if (this.tag) req.tag = this.tag;
		if (this.sinceDate) {
			req.since = this.sinceDate.getTime();
		}
		if (this.page && this.page > 1) req.page = this.page;
		if (this.pageSize && this.pageSize != this.DEFAULT_PAGE_SIZE) {
			req.pageSize = this.pageSize;
		}
		return req;
	};

	Query.prototype.newSearch = function() {
		delete this.page;
		this.search();
	};

	Query.prototype.search = function() {
		if (this.loading) this.loading.close();
		var oldUrl = $location.absUrl();
		var receiver = this.receiver ? this.receiver : "all";
		var newUrl =
			$location.path("/tags/" + receiver)
			.search(this.toRequest())
			.absUrl();
		if (oldUrl == newUrl) this.find();
	};

	Query.prototype.find = function() {
		var self = this;
		var loading = $notifier.info("Загрузка...");
		this.loading = loading;
		this.inProgress = true;
		function done() {
			if (self.loading !== loading) return false;
			self.inProgress = false;
			loading.close();
			return true;
		}
		$http.post("tags/find.json", this.toRequest(), {responseType: "json"})
		.success(function(data, status) {
			if (done()) angular.copy(data, tags);
		})
		.error(function(data, status) {
			done();
			$notifier.error(
					"Не удалось найти теги",
					"ОШИБКА " + status);
		});
	};

	var query = new Query();
	var tags = {
		totalCount: 0,
		events: []
	};

	$scope.query = query;
	$scope.tags = tags;

	$scope.receiverName = function(receiver) {
		if (!receiver.remoteURL) return "#" + receiver.id;
		return receiver.remoteURL + " (#" + receiver.id + ")";
	};

	$scope.openSinceDate = function() {
		$timeout(function() {
			query.sinceDateOpened = true;
		});
	};

	$scope.searchIfNotStarted = function() {
		if (!query.inProgress) query.newSearch();
	};

	$scope.resetTag = function() {
		delete query.tag;
		query.newSearch();
	};

	$scope.hidePagination = function() {
		return tags.totalCount <= tags.events.length;
	};

	$scope.setPage = function(page) {
		query.page = page;
		query.search();
	};

	$scope.$watch('query.sinceDate', function(newValue, oldValue) {
		if (oldValue !== newValue) query.newSearch();
	});

	if ($routeParams.receiver) query.find();
});
