/**
 * Angular main controller that connects the restAPI to useable functions that are used on the main page of the forum.
 * @param $scope
 * @param $forumRestAPI
 * @author Nico Smolders
 * @author Jur Braam
 */
forumMacro.controller('mainController', ['$route', '$routeParams', '$location', '$scope', '$forumRestAPI', '$rootScope', function ($route, $routeParams, $location, $scope, $forumRestAPI) {

    $scope.threadData = {
        sticky: "false"
    };

    $forumRestAPI.getThreads(function (forumModel) {
        $scope.threads = forumModel;
        
        angular.forEach($scope.threads, function (thread) {
        	 $forumRestAPI.getState(thread.id, function (stateInfo) {
        	        thread.state = stateInfo.state;
        	    }) ;
        	 $forumRestAPI.getProfileInfo(thread.userKey, function (profileInfo) {
     	        thread.profileInfo = profileInfo;
     	    });
        });
        
    });
    
    $forumRestAPI.getCurrentUser(function (currentUser) {
        $scope.currentUser = currentUser;
    });

    $scope.deleteForum = function (forumId) {
        $forumRestAPI.deleteForum(forumId, function (forumModel) {
            $scope.forums = forumModel;
            $scope.clearMessages();
            AJS.messages.success("#forumFooter", {
                body: "<p>Forum deleted successfully.</p>",
                closeable: true,
                shadowed: true
            });
        });
    };

    $scope.createThread = function () {
        $forumRestAPI.saveThread($scope.threadData.title, $scope.threadData.description, $scope.threadData.sticky, function (forumModel) {
            $scope.threads = forumModel;
        });
    };

    $scope.clearThreadData = function () {
        $scope.threadData.title = null;
        $scope.threadData.description = null;
        $scope.createThreadForm.$setPristine();
    };

    $scope.refresh = function () {
        $route.reload();
    };

    $scope.goToView = function (path) {
        $location.path(path);
    };

    $scope.goToManagementView = function () {
        $scope.goToView('management');
    };

    $scope.goToThreadView = function (threadID) {
        $scope.goToView('thread/' + threadID);
    };

    $scope.goToMainView = function(){
        $scope.goToView('forum');
    }

}]);