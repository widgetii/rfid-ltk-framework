angular.module(
		'rfid-tag-store.receivers',
		[
			"ngResource",
			"notifier"
		])
.factory('$rfReceivers', function($resource, $timeout, $notifier) {
	function RfReceivers() {
		this.list = [];
		var receivers = this;

		this.RfReceiver = $resource(
				"receivers/:receiverId.json",
				{
					receiverId: '@id'
				},
				{
					all: {
						method: 'GET',
						params: {receiverId: "all"},
						isArray: true
					},
					create: {
						method: 'PUT',
						params: {receiverId: 'create'}
					},
					save: {
						method: 'PUT'
					},
					del: {
						method: 'DELETE'
					}
				});

		this.RfReceiver.prototype.getName = function() {
			return this.remoteURL;
		};

		this.RfReceiver.prototype.getStatus = function() {
			switch (this.status) {
			case "inactive":
				return "Отключен";
			case "active":
				return "Подключен";
			case "ready":
				return "Работает";
			case "error":
				return "Ошибка";
			}
			return "???";
		};

		this.RfReceiver.prototype.create = function(success, error) {
			this.$create(
					{
						receiverId: 'create'
					},
					function(receiver) {
						receivers.list.push(receiver);
						if (success) success(receiver);
					}, function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось создать накопитель");
						if (error) error(response);
					});
		};

		this.RfReceiver.prototype.save = function(success, error) {
			function updateReceiver(receiver) {
				var index = receivers.receiverIndexById(receiver.id);
				if (index >= 0) {
					receivers.list[index] = receiver;
					return;
				}
				$notifier.error(
						"Ошибка обновления настроек накопителя",
						"Неизвестный накопитель: " + receiver.id);
			}
			this.$save(
					function(receiver) {
						updateReceiver(receiver);
						if (success) success(receiver);
					},
					function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось обновить настройки накопителя");
						if (error) error(response);
					});
		};

		this.RfReceiver.prototype.del = function(success, error) {
			var receiver = this;
			function removeReceiver() {
				var index = receivers.list.indexOf(receiver);
				if (index >= 0) receivers.list.splice(index, 1);
			}
			receiver.$del(
					function() {
						removeReceiver();
						if (success) success(receiver);
					},
					function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось удалить накопитель");
					});
		};
	}

	RfReceivers.prototype.newReceiver = function() {
		var RfReceiver = this.RfReceiver;
		return new RfReceiver();
	};

	RfReceivers.prototype.receiverIndexById = function(id) {
		var len = this.list.length;
		for (var i = 0; i < len; ++i) {
			if (this.list[i].id == id) {
				return i;
			}
		}
		return -1;
	};

	var receivers = new RfReceivers();

	function refreshReceivers() {
		receivers.RfReceiver.all(
				function(list) {
					receivers.list = list;
				},
				function(response) {
					$notifier.error(
							"ОШИБКА " + response.status,
							"Не удалось загрузить список накопителей");
				});
		$timeout(refreshReceivers, 5000);
	}

	refreshReceivers();

	return receivers;
})
.controller("RfReceiversCtrl", function($scope, $rfReceivers) {
	$scope.receivers = $rfReceivers;
	$scope.expanded = {};
})
.controller("RfReceiverCtrl", function($scope) {
	$scope.updating = false;
	function startUpdate() {
		$scope.updating = true;
	}
	function endUpdate() {
		$scope.updating = false;
	}
	$scope.isCollapsed = function() {
		return !$scope.expanded[$scope.receiver.id];
	};
	$scope.toggle = function() {
		var id = $scope.receiver.id;
		var expanded = $scope.expanded;
		if (expanded[id]) {
			delete expanded[id];
		} else {
			expanded[id] = true;
		}
	};
	$scope.del = function() {
		$scope.receiver.del();
	};
	$scope.start = function() {
		$scope.receiver.active = true;
		$scope.save();
	};
	$scope.stop = function() {
		$scope.receiver.active = false;
		$scope.save();
	};
	$scope.save = function() {
		startUpdate();
		$scope.receiver.save(endUpdate, endUpdate);
	};
})
.controller("NewRfReceiverCtrl", function(
		$scope,
		$document,
		$rfReceivers,
		$notifier) {
	$scope.updating = false;
	$scope.newReceiver = $rfReceivers.newReceiver();
	$scope.correctRemoteURL = function() {
		var receiver = $scope.newReceiver;
		var url = receiver.remoteURL;
		if (typeof url !== "string") return;
		if (url.indexOf("://") >= 0) return;
		if ("http:/".startsWith(url)) return;
		if ("https:/".startsWith(url)) return;
		receiver.remoteURL = "http://" + url;
	};
	function startUpdate() {
		$scope.updating = true;
	}
	function endUpdate(success) {
		$scope.updating = false;
		if (success) {
			angular.element(document.getElementById("newReceiverForm"))
			.controller("form")
			.$setDirty(false);
		}
	}
	$scope.create = function() {
		if ($scope.updating) return;
		startUpdate();
		$scope.newReceiver.create(
				function() {
					$scope.newReceiver = $rfReceivers.newReceiver();
					endUpdate(true);
				},
				endUpdate);
	};
});
