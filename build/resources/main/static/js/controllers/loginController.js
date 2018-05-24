
angular = require('angular');
require('angular-resource')
require('angular-cookies')
var _ = require('lodash');
var swal = require('sweetalert');

angular.module('todomvc', ["ngResource","ngRoute","ngCookies"])
	.controller('LoginController', function LoginController($scope, $routeParams, $resource, $http, $httpParamSerializer, $cookies, AtmService) {
		'use strict';

        var self = this;

        

	});
