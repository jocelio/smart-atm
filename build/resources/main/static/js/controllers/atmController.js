/*global angular */

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
angular = require('angular');
var _ = require('lodash');
angular.module('todomvc')
	.controller('AtmController', function AtmController($scope, $routeParams, $filter, AtmService) {
		'use strict';
        $scope._ = _;
        var self = this;
				self.addNote = addNote;
				self.notes = [];
				self.supplyNote = {};
				self.supplyNotes = [];
				self.supply = supply;

				getAllNotes();

        function getAllNotes(){
              AtmService.loadAllNotes().then(function(response){
								self.notes = response.data
							}) ;
        }

				function addNote(){
					var sameNote = _.filter(self.supplyNotes, function(sn){
						 return sn.note == self.supplyNote.note;
					});
					if(_.isEmpty(sameNote)){
					   self.supplyNotes = self.supplyNotes.concat(_.clone(self.supplyNote))
					}else{
							console.log(sameNote,'ja tem, senhor');
					}
					self.supplyNote = {}
				}

				function supply(){
						AtmService.supply(self.supplyNotes).then(function(response){
							self.notes = response.data
						}) ;
				}

	});
