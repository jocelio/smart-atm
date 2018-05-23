
'use strict';
var _ = require('lodash');

angular.module('todomvc').factory('AtmService',
    ['$http', '$q',
        function ($http, $q) {

            var factory = {
                  loadAllNotes: loadAllNotes
                , supply: supply
                , withdrawOptions: withdrawOptions
                , withdraw: withdraw
                , bestOption: bestOption
                , reset: reset
            };

            var host = _.includes(window.location.origin, 'localhost')? 'http://localhost:8080/':window.location.origin;

            function loadAllNotes() {
                var deferred = $q.defer();
                $http.get(host+'/api/atm/')
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function withdrawOptions(value) {
                var deferred = $q.defer();
                $http.get(host+'/api/atm/options/'+value)
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function bestOption(value) {
                var deferred = $q.defer();
                $http.get(host+'/api/atm/best-option/'+value)
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function supply(bankNotes) {
                var deferred = $q.defer();
                $http.post(host+'/api/atm/supply', bankNotes)
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function withdraw(withdrawOption) {
                var deferred = $q.defer();
                $http.post(host+'/api/atm/withdraw/', withdrawOption)
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function reset() {
                var deferred = $q.defer();
                $http.delete(host+'/api/atm/')
                    .then(
                        function (response) {
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            return factory;

        }
    ]);
