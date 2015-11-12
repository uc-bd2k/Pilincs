var app = angular.module('pilincs', [
    'ngRoute',
    'ngTagsInput',
    'pilincsModule']);

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            //when('/', {
            //    template: '',
            //    controller: 'MainCtrl'
            //}).
            when('/raw-data', {
                templateUrl: 'partials/rawdata.html',
                controller: 'TableCtrl'
            }).
            when('/technical-profiles', {
                templateUrl: 'partials/technical-profiles.html',
                controller: 'TableCtrl'
            }).
            when('/merged-profiles', {
                templateUrl: 'partials/merged-profiles.html',
                controller: 'TableCtrl'
            }).
            when('/explore', {
                templateUrl: 'partials/explore.html',
                controller: 'ExploreCtrl',
                controllerAs: 'ctrl'
            }).
            when('/export/:level', {
                templateUrl: 'partials/export.html',
                controller: 'ExportCtrl',
                controllerAs: 'ctrl'
            }).
            when('/qc', {
                templateUrl: 'partials/qc.html',
                controller: 'QualityCtrl',
                controllerAs: 'ctrl'
            }).
            when('/api', {
                templateUrl: 'partials/api.html'
            }).
            when('/about', {
                templateUrl: 'partials/about.html'
            }).
            otherwise({
                redirectTo: '/raw-data'
            });
    }]);