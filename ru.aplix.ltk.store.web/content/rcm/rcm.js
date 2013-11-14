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
	Rcm.prototype.editProfile = function(serverURL, settings, newProfile) {
		var scope = $rootScope.$new();
		scope.serverURL = serverURL;
		scope.settings = settings;
		scope.newProfile = newProfile;
		return $modal.open({
			controller: "RfProfileCtrl",
			scope: scope,
			templateUrl: 'rcm/profile.html',
			backdrop: 'static'
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
.controller("RfProfileCtrl", function(
		$scope,
		$modalInstance,
		$rcm,
		$http) {
	var ui = $scope.ui = $rcm.ui($scope.settings.providerId);

	function ProfileEditor() {
		this.updating = false;
		this.error = null;
	}
	ProfileEditor.prototype.save = function() {
		if (this.updating) return;
		this.updating = true;
		this.error = null;
		var self = this;
		$http.put(
				ui.mapping,
				$scope.settings,
				{params: {server: $scope.serverURL}})
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

	$scope.profile = new ProfileEditor();
});
