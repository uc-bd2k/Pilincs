var appControllers = angular.module('appControllers', []);

appControllers.controller('MainCtrl', ['$scope', '$http', '$timeout',function($scope, $http, $timeout) {


    $scope.showRecommend = false;
    $scope.tags = [];
    $scope.p100 = true;
    $scope.gcp = false;

    $timeout(callAtTimeout, 2500);
    function callAtTimeout() {
        $scope.showRecommend = true;
        $scope.recommended = [
            {'name':"PC3",'flag':'Cell','annotation':'CellId'}];
        $timeout(function(){ $scope.recommended.push({'name':"NPC",'flag':'Cell','annotation':'CellId'}); }, 300);
        $timeout(function(){ $scope.recommended.push({'name':"MCF7",'flag':'Cell','annotation':'CellId'}); }, 600);
        $timeout(function(){ $scope.recommended.push({'name':"UNC1215",'flag':'Replicate','annotation':'Pertiname'}); }, 900);
        $timeout(function(){ $scope.recommended.push({'name':"MS-275",'flag':'Replicate','annotation':'Pertiname'}); }, 1200);
        $timeout(function(){ $scope.recommended.push({'name':"methylstat",'flag':'Replicate','annotation':'Pertiname'}); }, 1500);
        $timeout(function(){ $scope.recommended.push({'name':"DYRK1A",'flag':'Peptide','annotation':'PrGeneSymbol'}); }, 1800);
        $timeout(function(){ $scope.recommended.push({'name':"HN1",'flag':'Peptide','annotation':'PrGeneSymbol'}); }, 2100);
    }

    $scope.toggleP100 = function(){
        $scope.p100 = !$scope.p100;
        if($scope.p100 == false && $scope.gcp == false){
            $scope.gcp = true;
        }
        $('#tablePeaks').bootstrapTable('refresh');
    }

    $scope.toggleGcp = function(){
        $scope.gcp = !$scope.gcp;
        if($scope.p100 == false && $scope.gcp == false){
            $scope.p100 = true;
        }
        $('#tablePeaks').bootstrapTable('refresh');
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

    $scope.getRecommendation = function() {

        if ($scope.tags.length < 5) {
            var res = $http.post('/pilincs/api-recommend', JSON.stringify($scope.tags));
            res.success(function (data, status, headers, config) {
                if(data.length == 0){
                    callAtTimeout();
                }
                $scope.recommended = data;
                $scope.showRecommend = true;
                //console.log(data);
            });
        }else{
            $scope.showRecommend = false;
        }
    }

    $scope.tagAddedRemoved = function() {
        $scope.getRecommendation();
        $('#tablePeaks').bootstrapTable('refresh');
    };


    $scope.searchClicked = function() {
        $scope.getRecommendation();
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
        $('#tablePeaks').bootstrapTable('refresh');
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

    var queryContent = [];
    queryContent.push({"p100":scope.p100});
    queryContent.push({"gcp":scope.gcp});
    queryContent = queryContent.concat(scope.tags);

    params.tags = JSON.stringify(queryContent);
    console.log(params.tags);
    return params;
}



