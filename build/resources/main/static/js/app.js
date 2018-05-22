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
					templateUrl: '/partials/atm-index.html',
					// controller: 'atmController'
			})
			.otherwise({
				redirectTo: '/login'
			});
	});

// require('todoStorage');
// require('todoCtrl');
require('atmController');
require('atmService');

// require('todoFocus');
// require('todoEscape');
// require('footer');
