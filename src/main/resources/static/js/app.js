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

	});

require('atmController');
require('atmService');
