
var app = angular.module('plunker', ['ngTagsInput']);

app.controller('MainCtrl', ['$scope', '$http', function($scope, $http) {

    $scope.recommended = [
        {'name':"PC3",'flag':'Cell','annotation':'CellId'},
        {'name':"NPC",'flag':'Cell','annotation':'CellId'},
        {'name':"MCF7",'flag':'Cell','annotation':'CellId'},
        {'name':"UNC1215",'flag':'Replicate','annotation':'Pertiname'},
        {'name':"MS-275",'flag':'Replicate','annotation':'Pertiname'},
        {'name':"methylstat",'flag':'Replicate','annotation':'Pertiname'},
        {'name':"CL15",'flag':'Peptide','annotation':'PrCluster'},
        {'name':"CL23",'flag':'Peptide','annotation':'PrCluster'},
        {'name':"DYRK1A",'flag':'Peptide','annotation':'PrGeneSymbol'},
        {'name':"HN1",'flag':'Peptide','annotation':'PrGeneSymbol'}
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

    // ok
    $scope.loadTags = function($query) {
        return $http.get('/pilincs/api-tags', { cache: true}).then(function(response) {
            var annotations = response.data;
            return annotations.filter(function(annotation) {
                return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1;
            });
        });
    };

    $scope.getRecommendation = function(){
        var res = $http.post('/pilincs/api-recommend', JSON.stringify($scope.tags));
        res.success(function(data, status, headers, config) {
            $scope.recommended = data;
            //console.log(data);
        });
    }

    $scope.tagAddedRemoved = function() {
        $scope.getRecommendation();
    };


    $scope.searchClicked = function() {
        $('#tablePeaks').bootstrapTable('refresh');
    };

    // ok
    $scope.recommendedClicked = function($newTag) {

        $scope.tags.push($newTag);

        var index = $scope.recommended.indexOf($newTag);
        $scope.recommended.splice(index,1);

        if($scope.recommended.length == 0){
            $scope.getRecommendation();
        }
    };

}]);

// Table Wenz...
$(function () {
    $('#tablePeaks').bootstrapTable({
        url: '/pilincs/api-assays-paged/',
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
            title: 'Dose (uM)'
        }, {
            field: 'pertTime',
            title: 'Time (h)'
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
            title: 'Analyze'
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
    console.log(params.tags);
    return params;
}



