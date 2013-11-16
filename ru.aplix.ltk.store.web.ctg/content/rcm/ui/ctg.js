angular.module('rfid-tag-store.rcm.ui.ctg', [])
.controller('CtgRcmSettingsCtrl', function($scope) {
	$scope.patterns.antennas = /^(\*|-|(\d+(-\d+)?(,\d+(-\d+)?)*)*)$/;
	$scope.desc.antennas =
		'"1", "4-7", "1,5-7,10", "*", "-". По умолчанию - все антенны';
});
