/*global angular */

/**
 *
 * @type {angular.Module}
 */

require('angular');
require('angular-route');

angular.module('todomvc', ['ngRoute'])
	.config(function ($routeProvider) {
		'use strict';

		$routeProvider
			.when('/', {
				templateUrl: '/views/atm-index.html',
			})
			.otherwise({
				redirectTo: '/login'
			});
	});

require('atmController');
require('atmService');
