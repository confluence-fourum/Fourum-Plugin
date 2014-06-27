/**
 * restService for angular
 * @author Nico Smolders
 * @author Jur Braam
 * @author Rik Harink
 */
forumMacro.service('$forumRestAPI', ['$http', '$rootScope', function ($http, $rootScope) {
    var RESTAPIForum = AJS.contextPath() + "/rest/forum/1.0/threads";
    var RESTAPIThread = AJS.contextPath() + "/rest/forum/1.0/posts";
    var MANAGEMENTRESTAPI = AJS.contextPath() + "/rest/forum/1.0/RightsManagement";
    var CONFLUENCEUSERRESTAPI = AJS.contextPath() + "/rest/forum/1.0/confluenceUser";
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";

    this.getCurrentUser = function (callback) {
        $http.get(AJS.contextPath() + "/rest/gadget/1.0/currentUser").success(function (data) {
            if (data) {
                callback(data.username);
            }
        });
    };

    this.getThreads = function (callback) {
        $http({method: 'GET', url: RESTAPIForum + '/' + $rootScope.forumID}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.getThread = function (threadID, callback) {
        $http({method: 'GET', url: RESTAPIForum + '/' + $rootScope.forumID + '/' + threadID}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.saveThread = function (title, description, sticky, callback) {
        // Preserve the this pointer so we can refer to the service from within the callback-function.
        var that = this;
        console.error("ForumID: " + $rootScope.forumID);
        var stringdata = "title=" + title + "&description=" + description + "&sticky=" + sticky + "&forumID=" + $rootScope.forumID;

        $http({
            method: 'POST',
            url: RESTAPIForum,
            data: stringdata
        }).success(function () {
            that.getThreads(callback);
        });
    };

    this.deleteThread = function (threadId, callback) {
        // Preserve the this pointer so we can refer to the service from within the callback-function.
        var that = this;
        $http({method: 'DELETE', url: RESTAPIForum + threadId}).success(function (data) {
            that.getThreads(callback);
        });
    };

    this.allPermissions = function (callback) {
        $http({method: 'GET', url: MANAGEMENTRESTAPI + '/permissions'}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.allUsers = function (callback) {
        $http({method: 'GET', url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/users'}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.allRoles = function (callback) {
        $http({method: 'GET', url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/roles'}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.singleRole = function (roleName, callback) {
        $http({method: 'GET', url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/role/' + roleName}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.allConfluenceUsers = function (callback) {
        $http({method: 'GET', url: CONFLUENCEUSERRESTAPI + '/confluenceusers'}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.saveRolePermissions = function (roleName, permissions, callback) {
        // Preserve the this pointer so we can refer to the service from within the callback-function.
        var that = this;
        var permissionJSON = angular.toJson(permissions);
        var data = {"roleName": roleName, "permissions": permissionJSON}
        $http({
            method: 'POST',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/setPermissions',
            params: data
        }).success(function () {
            that.singleRole(roleName, callback);
        });
    };

    this.setRoleName = function (roleName, newRoleName, callback) {
        var that = this;
        $http({
            method: 'PUT',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/setRoleName/' + roleName + '/' + newRoleName
        }).success(function () {
            that.allRoles(callback);
        });
    };

    this.setRoleDescription = function (roleName, newRoleDescription, callback) {
        var that = this;
        $http({
            method: 'PUT',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/setRoleDescription/' + roleName + '/' + newRoleDescription
        }).success(function () {
            that.allRoles(callback);
        });
    };

    this.createRole = function (newRoleName, newRoleDescription, callback) {
        var that = this;
        var stringdata = "roleName=" + newRoleName + "&roleDescription=" + newRoleDescription;
        $http({
            method: 'POST',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/createRole',
            data: stringdata
        }).success(function () {
            that.allRoles(callback);
        });
    };

    this.deleteRole = function (roleName, callback) {
        var that = this;
        $http({
            method: 'DELETE',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/deleteRole/' + roleName
        }).success(function () {
            that.allRoles(callback);
        });
    };


    this.addUser = function (user, role, callback) {
        var that = this;
        var stringdata = "newUserId=" + user.userKey + "&rolename=" + role;
        $http({
            method: 'POST',
            url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/addUser',
            data: stringdata
        }).success(function () {
            that.allUsers(callback);
        });
    };

    this.deleteUser = function (user, callback) {
        var that = this;
        $http({method: 'DELETE', url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/deleteUser/' + user.userKey}).success(function () {
            that.allUsers(callback);
        });
    };

    this.setUserRole = function (user, role, callback) {
        var that = this;
        $http({method: 'PUT', url: MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/setUserRole/' + user.userKey + '/' + role.name}).success(function () {
            that.allUsers(callback);
        });
    };


    this.getPosts = function (threadID, callback) {
        $http({method: 'GET', url: RESTAPIThread + '/items/' + threadID}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.deleteItem = function (threadID, itemID, callback) {
        // Preserve the this pointer so we can refer to the service from within the callback-function.
        var that = this;
        $http({method: 'DELETE', url: RESTAPIThread + '/items/' + itemID}).success(function (data) {
            that.getPosts(threadID, callback);
        });
    };

    this.savePost = function (message, threadId, callback) {
        var that = this;
        var stringdata = "message=" + message + "&threadID=" + threadId;
        $http({method: 'POST', url: RESTAPIThread + '/items', data: stringdata}).success(function () {
            that.getPosts(threadId, callback);
        });
    };

    this.saveItemMessage = function (message, itemID, callback) {
        var stringdata = "itemID=" + itemID + "&message=" + message;
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http({method: 'POST', url: RESTAPIThread + '/saveItemMessage', data: stringdata}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.savePostWithAttachments = function (message, attachments, threadId, callback) {
        var that = this;
        var data = {"message": message, "attachments": angular.toJson(attachments), "threadID": threadId};
        $http({
            method: 'POST',
            url: RESTAPIThread + '/items/withAttachments',
            params: data
        }).success(function () {
            that.getPosts(threadId, callback);
        });
    };


    this.saveQuote = function (message, postID, callback) {
        var stringdata = "message=" + message + "&postID" + postID;
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http({method: 'POST', url: RESTAPIThread + '/items', data: stringdata}).success(function () {
            that.getPosts(threadId, callback);
        });
    };

    this.getProfileInfo = function (userKey, callback) {
        $http({method: 'GET', url: CONFLUENCEUSERRESTAPI + "/" + $rootScope.forumID + '/getProfileInfo/' + userKey}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.setState = function (threadID, state, callback) {
        var that = this;
        var stringdata = "threadID=" + threadID + "&closed=" + state;
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http({method: 'POST', url: RESTAPIForum + '/setState', data: stringdata}).success(function () {
            that.getState(threadID, callback);
        });
    };

    this.getState = function (threadID, callback) {
        $http({method: 'GET', url: RESTAPIForum + '/getState/' + threadID}).success(function (data) {
            if (data) {
                callback(data);
            }
        })
    }

    this.addAttachment = function (itemID, attachmentName, attachmentUrl, callback) {
        var that = this;
        var stringdata = "name=" + attachmentName + "&url=" + attachmentUrl;
        $http({method: 'POST', url: RESTAPIThread + '/items/' + itemID + '/addAttachment', data: stringdata}).success(function () {
            that.getPosts(threadId, callback);
        });
    };

    this.getAttachments = function (callback) {
        var url = AJS.contextPath() + '/rest/api/content/' + AJS.params.pageId + '/child/attachment';
        $http({method: 'GET', url: url}).success(function (data) {
            if (data) {
                callback(data);
            }
        });
    };

    this.isAuthorized = function (permission) {
        var url = AJS.contextPath() + MANAGEMENTRESTAPI + '/' + $rootScope.forumID + '/isAuthorized/' + permission;
        return $http({method: 'GET', url:url});
    };

    this.createNewPostWithQuote = function(message, threadID, quotedItemID, callback){
        var that = this;
        var stringdata = "message=" + message + "&threadID=" + threadID + "&quotedItemID=" + quotedItemID;
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http({method: 'POST',
            url: RESTAPIThread + '/items/createItemWithQuote',
            data: stringdata
        }).success(function (data) {
            if (data){
                that.getPosts(threadID, callback);
            };
        });
    };

    this.deleteQuote = function(itemID, threadID, callback) {
        var that = this;
        $http({method: 'DELETE', url: RESTAPIThread + '/items/deleteQuote/' + itemID}).success(function () {
            that.getPosts(threadID, callback);
        });
    };
}]);