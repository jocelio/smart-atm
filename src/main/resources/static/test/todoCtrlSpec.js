/*global describe, it, beforeEach, inject, expect*/
(function () {
	'use strict';

	describe('Todo Controller', function () {
		var ctrl, scope, store;

		// Load the module containing the app, only 'ng' is loaded by default.
		beforeEach(module('smartatm'));

		beforeEach(inject(function ($controller, $rootScope, localStorage) {
			scope = $rootScope.$new();

			store = localStorage;

			localStorage.todos = [];
			localStorage._getFromLocalStorage = function () {
				return [];
			};
			localStorage._saveToLocalStorage = function (todos) {
				localStorage.todos = todos;
			};

			ctrl = $controller('TodoCtrl', {
				$scope: scope,
				store: store
			});
		}));

		it('should not have an edited Todo on start', function () {
			expect(scope.editedTodo).toBeNull();
		});

	});
}());
