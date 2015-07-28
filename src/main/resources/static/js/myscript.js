
var app = angular.module('plunker', ['ngTagsInput']);

app.controller('MainCtrl', ['$scope', '$http', function($scope, $http) {

    $scope.recommended = [
        "methylstat",
        "UNC1215",
        "MS-275",
        "trichostatin A",
        "BIX-01294"
    ];
    $scope.tags = [];
    $scope.p100 = true;
    $scope.gcp = false;


    $scope.toggleP100 = function(){
        $scope.p100 = !$scope.p100;
    }

    $scope.toggleGcp = function(){
        $scope.gcp = !$scope.gcp;
    }



    $scope.refreshTable = function() {
        $('#tablePeaks').bootstrapTable('refresh');
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
        var res = $http.get('/pilincs/api-recommend');//, $scope.tags);
        res.success(function(data, status, headers, config) {
            $scope.recommended = data;
        });
        $('#tablePeaks').bootstrapTable('refresh');
        //res.error(function(data, status, headers, config) {
        //    alert( "failure message: " + JSON.stringify({data: data}));
        //});

    };

    $scope.updateTags = function($newTag) {

        //if ($scope.recommended.length > 1) return;
        $scope.tags.push({"name":$newTag});

        var index = $scope.recommended.indexOf($newTag);
        $scope.recommended.splice(index,1);

        var res = $http.get('/pilincs/api-recommend');//, $scope.tags);
        //var res = $http.post('http://www.eh3.uc.edu/pilincs/api/recommend', $scope.tags);
        res.success(function(data, status, headers, config) {
            $scope.recommend = data;
        });

    };


}]);

// Table Wenz...
$(function () {
    $('#tablePeaks').bootstrapTable({
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
    var scope = angular.element('#tablePeaks').scope();
    params.tags = JSON.stringify(scope.tags);
    return params;
}



