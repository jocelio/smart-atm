
'use strict';
var _ = require('lodash');
require('angular-resource');

angular.module('todomvc')
.run(['$http','$cookies', function($http, $cookies) {
  var isLoginPage = window.location.href.indexOf("login") != -1;
//  if(isLoginPage){
//      if($cookies.get("access_token")){
//          window.location.href = "index.html";
//      }
//  } else{
//      if($cookies.get("access_token")){
//          $http.defaults.headers.common.Authorization =
//            'Bearer ' + $cookies.get("access_token");
//      } else{
//          window.location.href = "login.html";
//      }
//  }
}])
.factory('AtmService',
    ['$http', '$q', '$cookies', function ($http, $q, $cookies) {

            var factory = {
                  loadAllNotes: loadAllNotes
                , supply: supply
                , withdrawOptions: withdrawOptions
                , withdraw: withdraw
                , bestOption: bestOption
                , reset: reset
                , login: login
                , logout: logout
            };

            var host = _.includes(window.location.origin, 'localhost')? 'http://localhost:8080':window.location.origin;

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

            function login(user, encoded){
            var deferred = $q.defer();

                var req = {
                            method: 'POST',
                            url: host+"/oauth/token?"+user,
                            headers: {
                                "Authorization": "Basic " + encoded,
                                "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                            },
                            data: user
                        }
                $http(req)
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

            function logout(){
               $cookies.put('access_token');
                window.location.href = "login.html";
            }

            return factory;

        }
    ]);
