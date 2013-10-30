angular.module(
		'rfid-tag-store.tags',
		[
			"notifier"
		])
.factory('$rfTags', function($notifier, $http) {
	function RfTags() {
		this.pageSize = 50;
	}

	RfTags.prototype.receiverTagsSince = function(receiverId, since, success) {
		this.loadTags(
				"tags/since.json",
				{
					receiver: receiverId,
					since: since
				},
				success);
	};

	RfTags.prototype.listTags = function(receiverId, fromId, success) {
		this.loadTags(
				"tags/list.json",
				{
					receiver: receiverId,
					fromId: fromId
				},
				success);
	};

	RfTags.prototype.allTagsSince = function(since, offset, success) {
		var params = {since: since};
		if (offset) params.offset = offset;
		this.loadTags("tags/since.json", params, success);
	};

	RfTags.prototype.loadTags = function(url, params, success) {
		var loading = $notifier.info("Загрузка...");
		if (!params.limit) params.limit = this.pageSize;
		$http.get(url, {params: params})
		.success(function(response) {
			loading.close();
			success(response);
		})
		.error(function(response) {
			loading.close();
			$notifier.error(
					"ОШИБКА " + response.status,
					"Не удалось загрузить теги");
		});
	};

	return new RfTags();
})
.controller("RfTagsCtrl", function($scope, $rfTags) {
	$scope.$rfTags = $rfTags;
});
