/* global angular: false */	

(function() {
	'use strict';
	
	var app = angular.module('asignator', ['ngSanitize', 'ngResource', 'ngRoute']);
	
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider.
			when('/configuration', {
				templateUrl: 'partials/configuration.html',
				controller: 'ConfigurationController'
			}).
			when('/main', {
				templateUrl: 'partials/main.html',
		        controller: 'MainController'
			}).			
			otherwise({
				redirectTo: '/main'
			});
	}]);
	
	app.factory('ConfigurationRestService', ['$resource', function($resource) {
		return $resource('/Asignator/rest/configuration');
	}]);

	app.factory('SchedulerRestService', ['$resource', function($resource) {
		return $resource('/Asignator/rest/scheduler');
	}]);
	
	app.controller('ConfigurationController', ['$scope', '$sce', 'ConfigurationRestService', 'SchedulerRestService', function($scope, $sce, ConfigurationRestService, SchedulerRestService) {
		$scope.configurationProperties = ConfigurationRestService.get();
		$scope.schedulerStatus = SchedulerRestService.get();
		
		
		$scope.save = function() {
			ConfigurationRestService.save($scope.configurationProperties, function() {
				alert('Datos guardados!');
			});
		};
		
		$scope.startScheduler = function() {
			alert('start');
		};

		$scope.stopScheduler = function() {
			alert('stop');
		};

	}]);

	app.controller('MainController', ['$scope', '$sce', '$routeParams', function($scope, $sce, $routeParams) {
		
	}]);
	
})();
