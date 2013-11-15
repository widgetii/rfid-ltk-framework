angular.module(
		'rfid-tag-store.receivers',
		[
			"ngResource",
			"notifier",
			"rfid-tag-store.rcm"
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
								"Не удалось создать приёмник");
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
						"Ошибка обновления настроек приёмника",
						"Неизвестный приёмник: " + receiver.id);
			}
			this.$save(
					function(receiver) {
						updateReceiver(receiver);
						if (success) success(receiver);
					},
					function(response) {
						$notifier.error(
								"ОШИБКА " + response.status,
								"Не удалось обновить настройки приёмника");
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
								"Не удалось удалить приёмник");
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
		function mergeReceivers(list) {
			var len1 = receivers.list.length;
			var len2 = list.length;
			var i1 = 0;
			var i2 = 0;
			for (;;) {
				if (i2 >= len2) {
					receivers.list.splice(i1, len1 - i1);
					break;
				}
				var r2 = list[i2];
				if (i1 >= len1) {
					receivers.list.splice(i1, 0, r2);
					++i1;
					++i2;
					continue;
				}
				var r1 = receivers.list[i1];
				if (r1.id < r2.id) {
					receivers.list.splice(i1, 1);
					--len1;
					continue;
				}
				if (r1.id > r2.id) {
					receivers.list.splice(i1, 0, r2);
					++i1;
					++i2;
					continue;
				}
				angular.copy(r2, r1);
				++i1;
				++i2;
			}
		}
		receivers.RfReceiver.all(
				mergeReceivers,
				function(response) {
					$notifier.error(
							"ОШИБКА " + response.status,
							"Не удалось загрузить список приёмников");
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
.controller("RfReceiverCtrl", function($scope, $rcm) {
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
	$scope.configure = function() {
		var receiver = $scope.receiver;
		$rcm.editProfile({
			serverURL: receiver.remoteURL,
			providerId: receiver.providerId,
			profileId: receiver.profileId,
			receiver: receiver
		});
	};
})
.controller("AddRfReceiverCtrl", function($scope, $modal) {
	$scope.open = function() {
		$modal.open({
			templateUrl: 'receivers/new-receiver.html',
			controller: 'NewRfReceiverCtrl',
			backdrop: 'static',
			windowClass: 'new-receiver-modal'
		})
		.result.then(function(receiver) {
			$scope.expanded[receiver.id] = true;
		});
	};
})
.controller("NewRfReceiverCtrl", function(
		$scope,
		$rfReceivers,
		$notifier,
		$modalInstance,
		$rcm) {
	$scope.receivers = $rfReceivers;

	function Profile(profile) {
		this.update(profile);
	}
	Profile.prototype.update = function(profile) {
		angular.copy(profile, this);
		this.updateLabel();
	};
	Profile.prototype.updateLabel = function() {
		if (!this.settings) {
			this.label = null;
			return;
		}
		if (!this.settings.profileId) {
			this.label = "<Новый профиль>";
			return;
		}
		var name;
		if (this.settings.profileName) {
			name =
				this.settings.profileName
				+ " (" + this.settings.profileId + ")";
		}  else {
			name = this.settings.profileId;
		}
		if (this.receiver) {
			this.label = name + " - приёмник #" + this.receiver.id;
		} else {
			this.label = name;
		}
	};
	Profile.prototype.removeReceiver = function() {
		this.receiver = null;
		this.updateLabel();
	};

	function Profiles() {
		this.reset();
	}
	Profiles.prototype.reset = function() {
		this.collectorURL = null;
		this.list = [];
		this.noProfiles = false;
		this.error = false;
		this.invalidServer = false;
		this.selected = null;
	};
	Profiles.prototype.set = function(profiles, selected) {
		this.list = [];
		for (var i = 0; i < profiles.length; ++i) {
			var profile = new Profile(profiles[i]);
			this.list.push(profile);
			if (profile.id == selected) this.selected = profile;
		}
		this.noProfiles = this.list.length == 0;
		if (!this.selected) {
			this.selected = new Profile({
				id: selected || "",
				label: selected
					? "Неизвестный профиль <" + selected + ">"
					: "--- Профиль не выбран ---"
			});
			if (!this.noProfiles) this.list.splice(0, 0, this.selected);
		}
	};
	Profiles.prototype.addCreated = function(newProfile) {
		var profile = new Profile(newProfile);
		var index = this.list.indexOf(this.selected);
		this.list.splice(index, 0, profile);
		this.selected = profile;
	};
	Profiles.prototype.removeSelected = function() {
		var index = this.list.indexOf(this.selected);
		this.list.splice(index, 1);
		this.selected = this.list[index];
	};

	var profiles = $scope.profiles = new Profiles();

	function Query() {
		this.url = "";
		this.profiles = [];
		this.inProgress= false;
	}
	Query.prototype.correctURL = function() {
		var url = this.url;
		if (typeof url !== "string") return;
		if (url.indexOf("://") >= 0) return;
		var len = url.length;
		if ("http://".substring(0, len)
				== url.substring(0, Math.min(len, 7))) return;
		if ("https://".substring(0, len)
				== url.substring(0, Math.min(len, 8))) return;
		this.url = "http://" + url;
	};
	Query.prototype.find = function() {
		profiles.reset();
		var self = this;
		var request = this.inProgress = $rcm.loadProfiles(this.url);
		request.success(function(data) {
			if (self.inProgress !== request) return;
			self.inProgress = false;
			profiles.collectorURL = data.collectorURL;
			profiles.set(data.profiles, data.selected);
		})
		.error(function(data, status) {
			if (self.inProgress !== request) return;
			self.inProgress = false;
			if (!data.error) {
				profiles.error =
					"Не удалось получить список профилей. Ошибка " + status;
			} else {
				profiles.collectorURL = data.collectorURL;
				profiles.error = data.error;
				profiles.invalidServer = data.invalidServer;
			}
		});
	};

	var query = $scope.query = new Query();

	function Servers() {
		this.list = $rcm.servers;
		this.display = false;
	}
	Servers.prototype.select = function(url) {
		this.display = false;
		query.url = url;
		query.find();
	};

	$scope.servers = new Servers();

	function NewReceiver() {
		this.updating = false;
		this.error = null;
	}
	NewReceiver.prototype.create = function() {
		if (this.updating) return;
		var selected = profiles.selected;
		if (!profiles.collectorURL) {
			this.error = "Адрес накопителя неизвестен";
			return;
		}
		this.error = null;
		this.updating = false;
		var newReceiver = $rfReceivers.newReceiver();
		if (selected && selected.id) {
			newReceiver.remoteURL = profiles.collectorURL + "/" + selected.id;
		} else {
			newReceiver.remoteURL = profiles.collectorURL;
		}
		var self = this;
		newReceiver.create(
				function(receiver) {
					self.updating = false;
					$modalInstance.close(receiver);
				},
				function(data, status) {
					self.updating = false;
					self.error = "Неизвестная ошибка (" + status + ")";
				});
	};

	$scope.newReceiver = new NewReceiver();

	function SelectedProfile() {
		this.updating = false;
		this.error = null;
	}
	SelectedProfile.prototype.create = function() {
		if (this.updating) return;
		var selected = profiles.selected;
		if (!selected) {
			this.error = "Провайдер не выбран";
			return;
		}
		var settings = selected.settings;
		if (!settings || !settings.providerId) {
			this.error = "Провайдер неизвестен";
			return;
		}
		var newSettings = angular.copy(settings);
		this.error = null;
		this.updating = true;
		var self = this;
		$rcm.editProfile(
				{
					newProfile: true,
					serverURL: profiles.collectorURL,
					allProfiles: profiles.list
				},
				newSettings)
		.result.then(
				function(newProfile) {
					self.updating = false;
					profiles.addCreated(newProfile);
				},
				function() {
					self.updating = false;
				});
	};
	SelectedProfile.prototype.mode = function() {
		if (profiles.invalidServer) return "anyway";
		var selected = profiles.selected;
		if (!selected) return null;
		if (!selected.settings) return selected.id ? "anyway" : null;
		if (!selected.settings.profileId) return "new";
		if (selected.receiver) return "again";
		return "connect";
	};
	SelectedProfile.prototype.configurable = function() {
		var mode = this.mode();
		return mode == "connect" || mode == "again";
	};
	SelectedProfile.prototype.configure = function() {
		var selected = profiles.selected;
		$rcm.editProfile(
				{
					serverURL: profiles.collectorURL,
					allProfiles: profiles.list,
					receiver: selected.receiver
				},
				angular.copy(selected.settings))
		.result.then(
				function(profile) {
					selected.update(profile);
				});
	};
	SelectedProfile.prototype.del = function() {
		var selected = profiles.selected;
		$rcm.deleteProfile(
				{
					serverURL: profiles.collectorURL,
					receiver: selected.receiver
				},
				angular.copy(selected.settings))
		.result.then(
				function(profileDeleted) {
					if (profileDeleted) profiles.removeSelected();
					else profiles.selected.removeReceiver();
				});
	};

	$scope.selectedProfile = new SelectedProfile();
});
