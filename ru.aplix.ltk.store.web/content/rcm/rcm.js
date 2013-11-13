angular.module('rfid-tag-store.rcm', [])
.factory('$rcm', function($http, $modal, $rootScope) {

	function Rcm() {
		this.servers = [];
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
	Rcm.prototype.editProfile = function(settings, newProfile) {
		var scope = $rootScope.$new();
		scope.settings = settings;
		scope.newProfile = newProfile;
		return $modal.open({
			controller: "RfProfileCtrl",
			scope: scope,
			templateUrl: 'rcm/profile.html',
			backdrop: 'static'
		});
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
.controller("RfProfileCtrl", function($scope, $modalInstance) {
});
