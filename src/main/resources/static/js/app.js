var app = angular.module('pilincs', ['ngTagsInput','ngRoute','appControllers']);

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/data', {
                templateUrl: 'data.html',
                controller: 'MainCtrl'
            }).
            when('/profiles', {
                templateUrl: 'profiles.html',
                controller: 'MainCtrl'
            }).
            otherwise({
                redirectTo: '/data'
            });
    }]);