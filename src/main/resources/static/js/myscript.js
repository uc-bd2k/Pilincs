

//var $table = $('#table');
//$(function () {
//    $table.on('page-change.bs.table', function (e, number, size) {
//        getData(number, size);
//    });
//    var options = $table.bootstrapTable('getOptions');
//    getData(options.pageNumber, options.pageSize);
//});
//function getData(number, size) {
//    $.get('/pilincs/api-assays-paged/', {
//        offset: (number - 1) * size,
//        limit: size
//    }, function (res) {
//        $table.bootstrapTable('load', res);
//    });
//}

var app = angular.module('plunker', ['ngTagsInput']);

app.controller('MainCtrl', ['$scope', '$http', function($scope, $http) {

    $scope.showTable = true;
    $scope.searchText = "Add annotation";

    $scope.hideRecommend = true;

    $scope.tags = ['gsk126'];

    $scope.add = function() {

        $('#table1').bootstrapTable('refresh');
        //$scope.hideRecommend = true;
        //var res = $http.post('/pilincs/api-assays', $scope.tags);
        ////var res = $http.post('http://www.eh3.uc.edu/pilincs/api/assays', $scope.tags);
        //res.success(function(data, status, headers, config) {
        //    $scope.message = data;
        //
        //    $('#table').bootstrapTable('destroy');
        //    $('#table').bootstrapTable({
        //        data: data
        //    });
        //});
        //res.error(function(data, status, headers, config) {
        //    alert( "failure message: " + JSON.stringify({data: data}));
        //});
    };



    $scope.loadTags = function($query) {

        return $http.get('/pilincs/api-tags').then(function(response) {
            var annotations = response.data;
            return annotations.filter(function(annotation) {
                return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1;
            });
        });
    };

    $scope.tagInputClicked = function() {

        //alert ($scope.recommended.length > 1);
        $scope.hideRecommend = true;
        var res = $http.get('/pilincs/api-recommend');//, $scope.tags);
        res.success(function(data, status, headers, config) {
            $scope.recommended = data;
        });
        //res.error(function(data, status, headers, config) {
        //    alert( "failure message: " + JSON.stringify({data: data}));
        //});
        $scope.hideRecommend = false;
    };

    $scope.updateTags = function($newTag) {

        //if ($scope.recommended.length > 1) return;
        $scope.hideRecommend = true;
        $scope.tags.push({"text":$newTag});

        var index = $scope.recommended.indexOf($newTag);
        $scope.recommended.splice(index,1);

        var res = $http.get('/pilincs/api-recommend');//, $scope.tags);
        //var res = $http.post('http://www.eh3.uc.edu/pilincs/api/recommend', $scope.tags);
        res.success(function(data, status, headers, config) {
            $scope.recommend = data;
        });
        $scope.hideRecommend = false;
    };


}]);

// Table Wenz...
$(function () {
    $('#table1').bootstrapTable({
        url: 'http://localhost:8080/pilincs/api-assays-paged/',
        showColumns: 'true',
        queryParams: 'postQueryParams',
        pagination: 'true',
        sidePagination: 'server',
        showExport: 'true',
        detailFormatter: 'detailFormatter',
        columns: [{
            field: 'assayType',
            title: 'Assay type'
        }, {
            field: 'runIdUrl',
            title: 'Panorama'
        }, {
            field: 'peptideId',
            title: 'Peptide'
        }, {
            field: 'replicateId',
            title: 'Replicate',
            visible: false
        }, {
            field: 'cellId',
            title: 'Cell'
        }, {
            field: 'pertIname',
            title: 'Pert Iname'
        }, {
            field: 'pertDose',
            title: 'Dose [uM]'
        }, {
            field: 'pertTime',
            title: 'Time [h]'
        }, {
            field: 'pertType',
            title: 'Pert Type',
            visible: false
        }, {
            field: 'pertVehicle',
            title: 'Vehicle',
            visible: false
        }, {
            field: 'pubchemCid',
            title: 'PubchemCid',
            visible: false
        }, {
            field: 'value',
            title: 'Peak Area'
        }, {
            field: 'sourceUrl',
            title: 'Chromatograms',
            halign: 'center'
        }, {
            field: 'pertId',
            title: 'Pert Id',
            visible: false
        }, {
            field: 'downloadUrl',
            title: 'Source'
        }],
        responseHandler: function (res) {
            return res;
        }
    });
});

function postQueryParams(params) {
    //console.log($scope.tags);
    var scope = angular.element('#table1').scope();
    params.tags = JSON.stringify(scope.tags);
    return params;
}



