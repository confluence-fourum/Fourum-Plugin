/**
 * Created by Joost on 02/06/2014.
 */
forumMacro.controller("managementController", function ($scope, $forumRestAPI, $location, $route) {
    $scope.usersToDelete = [];
    $scope.usersToChange = [];

    $forumRestAPI.allPermissions(function (permissions) {
        $scope.permissions = permissions;
    });

    $forumRestAPI.allUsers(function (users) {
        $scope.users = users;
    });

    $forumRestAPI.allRoles(function (roles) {
        $scope.roles = roles;
    });

    $forumRestAPI.allConfluenceUsers(function (confUsers) {
        $scope.confluenceUsers = confUsers;
    });

    $scope.createNewRole = function (roleName, roleDescription) {
        if ($scope.roles[roleName] == null) {
            var roleElement = {'name': roleName, 'description': roleDescription, 'permission': []};
            $scope.roles.push(roleElement);
        }
    };

    $scope.deleteRole = function (roleName) {
        $scope.roles.splice($scope.roles.indexOf(roleName), 1);
    };

    $scope.saveRolePermissions = function () {
        angular.forEach($scope.roles, function (role) {
            $forumRestAPI.saveRolePermissions(role.name, role.permissions, function (savedRole) {
                role = savedRole
            });
        });
    };

    $scope.userNotExists = (function (userNE) {
        var notExists = true;
        angular.forEach($scope.users, function (user, key) {
            if (user.name == userNE.fullName) {
                notExists = false;
            }
        });
        return notExists;
    });

    $scope.addUser = (function (user, role) {
        $forumRestAPI.addUser(user, role.name, function (users) {
            $scope.users = users;
        });
    });

    $scope.deleteUsers = (function () {
        angular.forEach($scope.usersToDelete, function (user, key) {
            $forumRestAPI.deleteUser(user, function (users) {
                $scope.users = users;
            });
        });
        $scope.usersToDelete = [];
    });

    $scope.update = (function (user, selectedRole) {
        var push = true;
        angular.forEach($scope.usersToChange, function (userChange, key) {
            if (userChange.user.userKey == user.userKey) {
                userChange.newRole = selectedRole;
                push = false;
            }
        })
        if (push) {
            $scope.usersToChange.push({'user': user, 'newRole': selectedRole});
        }
    });

    $scope.saveUsers = (function () {
        angular.forEach($scope.usersToChange, function (user, key) {
            angular.forEach($scope.users, function (fUser, key) {
                if (fUser.userKey == user.user.userKey) {
                    $forumRestAPI.setUserRole(fUser, user.newRole, function (users) {
                        $scope.users = users;
                    });
                }
            })
        });
    });

    $scope.setRoleDescription = function (role) {
        $forumRestAPI.setRoleDescription(role.name, role.description, function (updatedRoles) {
            $scope.roles = updatedRoles;
        });
    };

    $scope.setRoleName = function (role, newRoleName) {
        $forumRestAPI.setRoleName(role.name, newRoleName, function (updatedRoles) {
            $scope.roles = updatedRoles;
        });
    };

    $scope.createRole = function (newRoleName, newRoleDescription) {
        $forumRestAPI.createRole(newRoleName, newRoleDescription, function (updatedRoles) {
            $scope.roles = updatedRoles;
        });
    };

    $scope.deleteRole = function (roleName) {
        $forumRestAPI.deleteRole(roleName, function (updatedRoles) {
            $scope.roles = updatedRoles;
        });
    };

    $scope.goToMainView = function () {
        $location.path('forum');
    };

    $scope.refresh = function () {
        $route.reload();
    };
});