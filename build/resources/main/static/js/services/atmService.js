
'use strict';

angular.module('todomvc').factory('AtmService',
    ['$http', '$q',
        function ($http, $q) {

            var factory = {
                loadAllNotes: loadAllNotes,
                supply: supply
            };

            return factory;

            function loadAllNotes() {
                console.log('Fetching all notes');
                var deferred = $q.defer();
                $http.get('http://localhost:8080/api/atm/')
                    .then(
                        function (response) {
                            console.log('Fetched successfully all users');
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading users');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function supply(bankNotes) {
                console.log('Fetching all notes');
                var deferred = $q.defer();
                $http.post('http://localhost:8080/api/atm/supply', bankNotes)
                    .then(
                        function (response) {
                            console.log('Fetched successfully all users');
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading users');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

        }
    ]);
