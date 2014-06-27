/**
 * Created by Rik on 10/06/2014.
 */
forumMacro.controller("attachmentController", function ($scope, $upload) {
    $scope.onFileSelect = function ($files) {
        //$files: an array of files selected, each file has name, size, and type.
        for (var i = 0; i < $files.length; i++) {
            var file = $files[i];
            $scope.upload = $upload.upload({
                url: AJS.contextPath() + '/rest/api/1/content/' + AJS.params.pageId + '/attachments',
                method: 'POST',
                headers: {' X-Atlassian-Token': 'nocheck'},
                data: {myObj: $scope.uploadFiles},
                file: file, // or list of files: $files for html5 only
                fileFormDataName: 'file_' + i
            }).progress(function (evt) {
                console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
            }).success(function (data, status, headers, config) {
                // file is uploaded successfully
                console.log(data);
            });
        }
    };
});