/*global angular */

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
angular = require('angular');
var _ = require('lodash');
var swal = require('sweetalert');

angular.module('todomvc')
	.controller('AtmController', function AtmController($scope, $routeParams, $filter, AtmService) {
		'use strict';
        $scope._ = _;
        var self = this;

				self.notes = [];
				self.supplyNote = {};
				self.supplyNotes = [];
				self.withdrawOptions = [];

				self.addNote = addNote;
				self.supply = supply;
				self.remove = remove;
				self.openWithdrawModal = openWithdrawModal;
				self.format = format;
				self.withdraw = withdraw;

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
					self.supplyNote = {};

				}

				function remove(bankNote){
						self.supplyNotes = _.filter(self.supplyNotes, function(sn){
							 return sn.note != bankNote.note;
						});
				}

				function supply(){
						AtmService.supply(self.supplyNotes).then(function(response){
							self.notes = response.data
							self.supplyNotes = [];
							self.supplyNote = {};
							$('#exampleModal').modal('hide');
							swal("Notas adicionadas com sucesso.");
						});
				}

				function openWithdrawModal(){

					swal("Valor:", {
					  content: "input",
					})
					.then(function(withdrawValue){
						swal("Como deseja suas cédulas?", {
							  buttons: {
							    choose: {
							      text: "Escolher",
							      value: "choose",
							    },
									bestOption: {
							      text: "Melhor Opção",
							      value: "bestOption",
							    },
									cancel: "Cancelar",
							  }
							})
							.then(function(value){
							  switch (value) {
							    case "choose":
									  AtmService.withdrawOptions(withdrawValue).then(function(response){
												self.withdrawOptions = response.data
												swal("Escolha uma opção de cédulas para seu saque.");
										})
							      break;

							    case "bestOption":
										AtmService.bestOption(withdrawValue).then(function(response){
												self.withdrawOptions = [response.data]
												swal("Achamos a melhor opção para seu saque.");
										})
							      break;

							    default:
							      swal("Ok");
							  }
							});
					})
				}

				function format(option){
						return _(option)
									.filter(function(f){return f.amount != 0})
									.map(function(m){ return m.amount+'x'+m.note })
									.value().join(', ');
				}

				function withdraw(option){
					swal({
						title: "Realização de Saque",
						text: "A sacar: "+self.format(option),})
						.then(function(willWithdraw){
							if (willWithdraw) {
									AtmService.withdraw(option).then(function(response){
										self.notes = response.data
										self.withdrawOptions = []
										swal("Saque Realizado", "Notas debidatas com sucesso.", "success");
									})
							}
						});
				}

	});
