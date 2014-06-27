var forumMacro = angular.module('forumMacro', ['ngRoute', 'ngSanitize', 'checklist-model', 'ui.bootstrap', 'emoji']);

forumMacro.config(['$routeProvider', '$locationProvider',
    function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/forum', {
                templateUrl: '/confluence/plugins/forum/forumVM.action',
                controller: 'mainController',
                controllerAs: 'forum',
                resolve: {}
            }).
            when('/thread/:threadId', {
                templateUrl: '/confluence/plugins/forum/threadVM.action',
                controller: 'threadController',
                controllerAs: 'thread'
            }).
            when('/management', {
                templateUrl: '/confluence/plugins/forum/managementVM.action',
                controller: 'managementController',
                controllerAs: 'managementController'
            }).
            otherwise({
                redirectTo: '/forum'
            });

        // configure html5 links
        $locationProvider.html5Mode(true);
    }])

forumMacro.filter('newlines', function () {
    return function (text) {
        return text.replace(/\n/g, '<br/>');
    }
})

forumMacro.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

forumMacro.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function (file, uploadUrl, callback) {
        var fd = new FormData();
        fd.append('file', file);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined,
                'X-Atlassian-Token': 'nocheck'}
        })
            .success(function () {
                callback();
            })
            .error(function () {
            });
    }
}]);


