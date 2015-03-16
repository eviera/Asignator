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
	
	app.controller('ConfigurationController', ['$scope', '$sce', 'ConfigurationRestService', function($scope, $sce, ConfigurationRestService) {
		$scope.configurationProperties = ConfigurationRestService.query();
	}]);

	app.controller('MainController', ['$scope', '$sce', '$routeParams', function($scope, $sce, $routeParams) {
		
	}]);
	
})();
