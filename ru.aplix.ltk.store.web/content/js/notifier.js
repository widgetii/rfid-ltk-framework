angular.module("notifier", ["ui.bootstrap"])
.factory('$notifier', function($timeout) {
	function Notifier() {
		this.messages = [];
	}
	Notifier.prototype.addMessage = function(message) {
		this.removeTemp();
		var self = this;
		message.close = function() {
			var index = self.messages.indexOf(message);
			if (index >= 0) self.messages.splice(index, 1);
		};
		message.setScope = function(scope) {
			scope.$on('$destroy', message.close);
			return message;
		};
		if (!message.url) {
			message.url = '/info.msg';
		}
		var cssClass = message.cssClass;
		if (typeof cssClass !== "function") {
			if (typeof cssClass === "undefined") {
				cssClass = "alert alert-block alert-info";
			}
			message.cssClass = function() {
				return cssClass;
			};
		}
		var condition = message.condition;
		if (typeof condition !== "function") {
			if (typeof condition === "undefined") condition = true;
			message.condition = function() {
				return condition;
			};
		}
		this.messages.push(message);
		return message;
	};
	Notifier.prototype.removeTemp = function(len) {
		var last = len ? len - 1 : this.messages.length - 1;
		for (var m = last; m >= 0 ; --m) {
			if (this.messages[m].temp) this.messages.splice(m, 1);
		}
	};
	Notifier.prototype.visibleMessages = function() {
		for (var m = 0; m < this.messages.length; ++m) {
			var message = this.messages[m];
			if (!message.condition()) continue;
			if (!message.temp) {
				this.removeTemp(m);
				break;
			}
		}
		return this.messages;
	};
	Notifier.prototype.info = function(title, text) {
		if (!text) {
			text = title;
			title = null;
		}
		return this.addMessage({
			title: title,
			text: text
		});
	};
	Notifier.prototype.error = function(title, text) {
		if (!text) {
			text = title;
			title = "Ошибка";
		}
		var message = {
			cssClass: "alert alert-block alert-error",
			url: "/error.msg",
			title: title,
			text: text,
		};
		message = this.addMessage(message);
		$timeout(message.close, 3145);
		return message;
	};
	Notifier.prototype.done = function(message) {
		if (!message.url) {
			message.url = '/done.msg';
		}
		if (!message.cssClass) {
			message.cssClass = "alert alert-block alert-success";
		}
		var undo = message.undo;
		if (typeof undo === "function") {
			message.canUndo = true;
			message.undo = function() {
				undo.apply(message);
				message.close();
			};
		}
		message.temp = true;
		return this.addMessage(message);
	};
	return new Notifier();
})
.controller("NotifierCtrl", function($scope, $notifier) {
	$scope.notifier = $notifier;
	$scope.cssClass = function() {
		var messages = $notifier.messages;
		for (var i = 0; i < messages.length; ++i) {
			if (messages[i].condition()) return "show";
		}
		return "hide";
	};
});
