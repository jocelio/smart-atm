/*global angular */

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
angular = require('angular');
var _ = require('lodash');
var swal = require('sweetalert');
require('angular-resource');
require('angular-cookies');

angular.module('todomvc', ["ngResource","ngRoute","ngCookies"])
	.controller('AtmController', function AtmController($scope, $routeParams, $resource, $http, $httpParamSerializer, $cookies, AtmService) {
		'use strict';
        $scope._ = _;
        var self = this;

				self.withdrawValue;
				self.notes = [];
				self.supplyNote = {};
				self.supplyNotes = [];
				self.withdrawOptions = [];
				self.user = {grant_type:"password", username: "", password: ""};

				self.addNote = addNote;
				self.supply = supply;
				self.remove = remove;
				self.openWithdrawModal = openWithdrawModal;
				self.format = format;
				self.withdraw = withdraw;
				self.reset = reset;
				self.login = login;
				self.logout = logout;

				$scope.getAllNotes = getAllNotes;


                function getAllNotes(){
                      AtmService.loadAllNotes().then(function(response){
                          self.notes = response.data
                      });
                }

				function addNote(){

					if(self.supplyNote.note < 0 || self.supplyNote.amount < 0){
						  swal("Não é possível cadastrar cédula ou quantidade negativa.")
							return;
					}

					var sameNote = _.filter(self.supplyNotes, function(sn){
						 return sn.note == self.supplyNote.note;
					});

					if(_.isEmpty(sameNote)){
					   self.supplyNotes = self.supplyNotes.concat(_.clone(self.supplyNote))
					}else{
							swal("Cédula já adicionada.")
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

					swal("Valor:", {content: "input"})
					.then(function(withdrawValue){

						if(isNaN(withdrawValue)){
								swal("Selecione um número inteiro ;)");
								return;
						}

						var littleNote = _(self.notes).filter(function(n){return n.amount>0}).sortBy(function(n){return n.note}).head();

						if(withdrawValue % littleNote.note !== 0){
								self.withdrawOptions = []
								self.withdrawValue = 0;
								swal("Não é possível sacar esse valor com as notas disponíveis.");
								return;
						}

						self.withdrawValue = withdrawValue;

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
										})
							      break;

							    case "bestOption":
										AtmService.bestOption(withdrawValue).then(function(response){
												self.withdrawOptions = [response.data]
										})
							      break;
							    default:

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
						text: "Sacar: "+self.format(option)+"?",
						buttons: {
							yes: {
								text: "Sim",
								value: "yes",
							},
							cancel: "Cancelar"
						}})
						.then(function(value){
								switch (value) {
									case "yes":
											AtmService.withdraw(option).then(function(response){
												self.notes = response.data
												self.withdrawOptions = []
												self.withdrawValue = 0;
												swal("Saque Realizado", "Notas debidatas com sucesso.", "success");
											})
										break;

									default:

							}
						});
				}

				function reset(){
					swal({
						title: "Lipar todos os dados?",
						text: "Apagar todas as notas?",
						buttons: {
							yes: {
								text: "Sim",
								value: "yes",
							},
							cancel: "Cancelar"
						}})
						.then(function(value){
								switch (value) {
									case "yes":
											AtmService.reset().then(function(response){
												self.notes = []
												self.withdrawOptions = []
												self.withdrawValue = 0;
												swal("", "", "success");
											})
										break;
									default:
							}
						})

				}

                function login() {
                    AtmService.login($httpParamSerializer(self.user), btoa("clientapp:123456")).then(function(data){
                        $cookies.put("access_token", data.data.access_token);
                        window.location.href="index.html";
                    }, function(reason) {
                        swal({
                            title: "Falha ao realizar Login",
                            text: "Usuário ou senha incorretos",
                            icon: "warning"
                            })
                        });
                }

                function logout() {
                    AtmService.logout();
                }

	});
