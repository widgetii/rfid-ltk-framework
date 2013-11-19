angular.module('rfid-tag-store.rcm', [])
.factory('$rcm', function($http, $modal, $rootScope) {

	function Rcm() {
		this.servers = [];
		this.uis = null;
	}
	Rcm.prototype.loadProfiles = function(server) {
		return $http.get("rcm/profiles.json", {params: {server: server}});
	};
	Rcm.prototype.newProfileId = function(profiles, providerId) {
		var largestId = 0;
		for (var i = 0; i < profiles.length; ++i) {
			var settings = profiles[i].settings;
			if (!settings) continue;
			if (providerId && settings.providerId != providerId) continue;
			var profileId = settings.profileId;
			var id = parseInt(profileId, 10);
			if (isNaN(id)) continue;
			if (id > largestId) largestId = id;
		}
		return (largestId + 1).toString();
	};
	Rcm.prototype.loadProfile = function(config, settings) {
		config.loading = true;
		$http.get(
				"rcm/profile.json",
				{params: {
					server: config.serverURL,
					providerId: config.providerId,
					profileId: config.profileId
				}})
		.success(function(data) {
			config.loading = false;
			angular.copy(data.settings, settings);
		})
		.error(function(data, status) {
			config.loading = false;
			if (data.error) {
				config.error = data.error;
			} else {
				config.error = "Ошибка " + status;
			}
		});
	};
	Rcm.prototype.editProfile = function(config, settings) {
		if (!settings) {
			settings = {};
			this.loadProfile(config, settings);
		} else if (!config.providerId) {
			config.providerId = settings.providerId;
		}
		if (config.newProfile) {
			if (!settings.profileId && config.allProfiles) {
				settings.profileId = this.newProfileId(
						config.allProfiles,
						settings.providerId);
			}
		}

		var scope = $rootScope.$new();

		scope.settings = settings;
		scope.config = config;

		return $modal.open({
			controller: 'RfProfileCtrl',
			scope: scope,
			templateUrl: 'rcm/profile.html',
			backdrop: 'static'
		});
	};
	Rcm.prototype.deleteProfile = function(config, settings) {

		var scope = $rootScope.$new();

		scope.config = config;
		scope.settings = settings;
		scope.receiver = config.receiver;

		return $modal.open({
			controller: 'DeleteRfProfileCtrl',
			scope: scope,
			templateUrl:
				config.receiver
				? 'rcm/delete-receiver.html' : 'rcm/delete-profile.html'
		});
	};
	Rcm.prototype.ui = function(providerId) {
		if (this.uis) {
			return this._ui(providerId);
		}
		var self = this;
		var ui = {
			loading: true
		};
		$http.get("rcm/ui.json")
		.success(function(data) {
			self.uis = data;
			ui.loading = false;
			angular.copy(self._ui(providerId), ui);
		})
		.error(function(data, status) {
			ui.loading = false;
			ui.error =
				"Не удалось загрузить редакторы профилей. Ошибка " + status;
		});
		return ui;
	};
	Rcm.prototype._ui = function(providerId) {
		var ui = this.uis[providerId];
		if (ui) return ui;
		var defaultUI = this.uis._;
		if (defaultUI) return defaultUI;
		return {
			error: "Не удалось создать редактор профиля для " + providerId
		};
	};

	var rcm = new Rcm();

	$http.get("rcm/servers.json")
	.success(function(data) {
		angular.copy(data.servers, rcm.servers);
	})
	.error(function(data, status) {
		$notifier.error(
				"Не удалось получить список доступных накопителей",
				"ОШИБКА " + status);
	});

	return rcm;
})
.controller('RfProfileCtrl', function(
		$scope,
		$modalInstance,
		$rcm,
		$http) {
	var ui = $scope.ui = $rcm.ui($scope.config.providerId);

	$scope.desc = {
		autostart:
			"Имеет смысл устанавливать лишь для логирующих провайдеров. "
			+ "Иначе будет создавать ненужную нагрузку на накопитель."
	};
	$scope.patterns = {
		number: /^\d*$/
	};

	function ProfileEditor() {
		this.updating = false;
		this.error = null;
		this.errors = {
			profileId: null
		};
	}
	ProfileEditor.prototype.validateProfileId = function() {
		if (!$scope.config.newProfile) return;
		var allProfiles = $scope.config.allProfiles;
		if (!allProfiles) return;
		this.errors.profileId = null;
		var profileId = $scope.settings.profileId;
		if (!profileId) return;
		var providerId = $scope.settings.providerId;
		for (var i = 0; i < allProfiles.length; ++i) {
			var settings = allProfiles[i].settings;
			if (!settings) continue;
			if (settings.providerId != providerId) continue;
			if (settings.profileId != profileId) continue;
			this.errors.profileId =
				"Профиль с таким идентификатором уже есть";
			break;
		}
	};
	ProfileEditor.prototype.save = function() {
		if (this.updating) return;
		this.updating = true;
		this.error = null;
		var self = this;
		var settings = $scope.settings;
		var tp = settings.trackingPolicy;
		if (tp.transactionTimeout == "") tp.transactionTimeout = null;
		if (tp.invalidationTimeout == "") tp.invalidationTimeout = null;
		$http.put(
				ui.mapping,
				settings,
				{params: {server: $scope.config.serverURL}})
		.success(function(data) {
			self.updating = false;
			$modalInstance.close(data);
		})
		.error(function(data, status) {
			self.updating = false;
			if (data.error) {
				self.error = data.error;
			} else {
				self.error = "Ошибка " + status;
			}
		});
	};
	ProfileEditor.prototype.del = function() {
		if (this.updating) return;
		$rcm.deleteProfile(angular.copy($scope.config), $scope.settings);
	};

	$scope.profile = new ProfileEditor();
})
.controller('DeleteRfProfileCtrl', function($scope, $modalInstance, $http) {
	var config = $scope.config;
	var settings = $scope.settings;
	if (settings) {
		var name = settings.profileName;
		var id = settings.profileId + '@' + settings.providerId;
		if (name) {
			$scope.profileName = name + " (" + id + ")";
		} else {
			$scope.profileName = id;
		}
	} else if (config.providerId) {
		$scope.profileName = config.profileId + '@' + config.providerId;
	}

	$scope.error = null;
	$scope.deleting = false;

	$scope.deleteProfile = function() {
		this.del(true);
	};
	$scope.deleteReceiver = function(withProfile) {
		this.del(withProfile);
	};
	$scope.del = function(deleteProfile) {
		$scope.error = false;
		$scope.deleting = true;
		var params = {
			server: config.serverURL,
		};
		if ($scope.receiver) params.receiverId = $scope.receiver.id;
		if (deleteProfile) {
			params.providerId =
				(settings ? settings.providerId : config.providerId);
			params.profileId =
				(settings ? settings.profileId : config.profileId);
		}
		$http['delete']('rcm/profile.json', {params: params})
		.success(function() {
			$scope.deleting = true;
			$modalInstance.close(deleteProfile);
		})
		.error(function(data, status) {
			$scope.deleting = true;
			if (data.error) {
				$scope.error = data.error;
			} else {
				$scope.error = "Ошибка " + status;
			}
		});
	};

});
