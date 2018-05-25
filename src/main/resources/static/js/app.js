/*global angular */

/**
 *
 * @type {angular.Module}
 */

require('angular');
require('angular-route');

angular.module('smartatm', ['ngRoute'])
	.config(function ($routeProvider) {
		'use strict';

	});

require('atmController');
require('atmService');
