angular.module(
		'rfid-tag-store.tags',
		[
			'notifier',
			'rfid-tag-store.receivers'
		])
.controller(
		"RfTagsCtrl",
		function(
				$scope,
				$http,
				$notifier,
				$routeParams,
				$location,
				$timeout,
				$rfReceivers) {
	$scope.receivers = $rfReceivers;

	function Timestamp(value) {
		if (!value) return;
		this.time = parseInt(value);
		var timestamp = new Date(this.time);
		this.date = new Date(
				timestamp.getFullYear(),
				timestamp.getMonth(),
				timestamp.getDate());
	}

	Timestamp.prototype.toValue = function() {
		if (this.time) return this.time;
		if (!this.date) return;
		var date = new Date(
				this.date.getFullYear(),
				this.date.getMonth(),
				this.date.getDate());
		return date.getTime();
	};

	Timestamp.prototype.openDate = function() {
		var self = this;
		$timeout(function() {
			self.dateOpened = true;
		});
	};

	Timestamp.prototype.watch = function(path) {
		var self = this;
		$scope.$watch(path + '.date', function(newValue, oldValue) {
			if (oldValue === newValue) return;
			self.time = null;
			query.newSearch();
		});
	};

	function Query() {
		this.DEFAULT_PAGE_SIZE = 50;
		if ($routeParams.receiver) {
			this.searchResults = true;
			if ($routeParams.receiver != "all") {
				this.receiver = parseInt($routeParams.receiver);
			}
		}
		if ($routeParams.tag) this.tag = $routeParams.tag;
		this.since = new Timestamp($routeParams.since);
		this.page =
			$routeParams.page && $routeParams.page > 1 ? $routeParams.page : 1;
		this.pageSize =
			$routeParams.pageSize && $routeParams.pageSize > 0
			? $routeParams.pageSize : this.DEFAULT_PAGE_SIZE;

		this.inProgress = false;
	}

	Query.prototype.toRequest = function() {
		var req = this.toSearch();
		if (this.receiver) req.receiver = this.receiver;
		return req;
	};

	Query.prototype.toSearch = function() {
		var req = {};
		if (this.tag) req.tag = this.tag;
		var sinceVal = this.since.toValue();
		if (sinceVal) req.since = sinceVal;
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
			.search(this.toSearch())
			.absUrl();
		if (oldUrl == newUrl) this.find();
	};

	Query.prototype.setReceiver = function(receiver) {
		this.receiver = receiver;
		this.newSearch();
	};

	Query.prototype.resetReceiver = function() {
		if (!this.receiver) return;
		delete this.receiver;
		this.newSearch();
	};

	Query.prototype.setTag = function(tag) {
		this.tag = tag;
		this.newSearch();
	};

	Query.prototype.resetTag = function() {
		if (!this.tag) return;
		delete this.tag;
		this.newSearch();
	};

	Query.prototype.setSince = function(timestamp) {
		this.since.time = timestamp;
		this.newSearch();
	};

	Query.prototype.setPage = function(page) {
		this.page = page;
		query.search();
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

	$scope.noResults = function() {
		return query.searchResults && !query.inProgress && !tags.events.length;
	};

	$scope.receiverName = function(receiver) {
		if (!receiver.remoteURL) return "#" + receiver.id;
		return receiver.remoteURL + " (#" + receiver.id + ")";
	};

	$scope.searchIfNotStarted = function() {
		if (!query.inProgress) query.newSearch();
	};

	$scope.hidePagination = function() {
		return tags.totalCount <= tags.events.length;
	};

	query.since.watch('query.since');

	if (query.searchResults) query.find();
});
