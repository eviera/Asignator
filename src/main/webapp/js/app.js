/* global angular: false */	

(function() {
	'use strict';
	
	var app = angular.module('tubeCurator', ['ngSanitize', 'ngResource', 'ngRoute']);
	
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider.
			when('/entries', {
				templateUrl: 'partials/entries.html',
				controller: 'TubeController'
			}).
			when('/entries/:id', {
				templateUrl: 'partials/entry.html',
		        controller: 'TubeDetailController'
			}).			
			otherwise({
				redirectTo: '/entries'
			});
	}]);
	
	app.factory('TubeEntryService', ['$resource', function($resource) {
		return $resource('/tubecurator/rest/tubeentries/:id');
	}]);
	
	app.controller('TubeController', ['$scope', '$sce', 'TubeEntryService', function($scope, $sce, TubeEntryService) {
		$scope.tubeEntries = TubeEntryService.query();
		
		$scope.trustSrc = function(src){
			return $sce.trustAsResourceUrl(src);
		};
		
	}]);

	app.controller('TubeDetailController', ['$scope', '$sce', '$routeParams', 'TubeEntryService', function($scope, $sce, $routeParams, TubeEntryService) {
		$scope.tubeEntry = TubeEntryService.get({ id: $routeParams.id});
			
		$scope.trustSrc = function(src){
			return $sce.trustAsResourceUrl(src);
		};
		
	}]);
	
})();
