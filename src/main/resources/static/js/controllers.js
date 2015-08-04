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


var appControllers = angular.module('appControllers', []);
//appControllers.service('ItemService', [ItemService]);

appControllers.controller('MainCtrl', ['$scope', '$http', '$timeout',function($scope, $http, $timeout) {

    $scope.showRecommend = false;
    $scope.tags = [];
    $scope.p100 = true;
    $scope.gcp = false;
    $scope.l1000 = false;

    $scope.workspaces = [];
    $scope.workspaces.push({ name: 'Show Data',alias:'tablePeaks' });
    $scope.workspaces.push({ name: 'Show Profiles', alias: 'tableProfiles' });
    $scope.workspaces.push({ name: 'Show Relations', alias: 'tableRelations' });
    $scope.workspaces.push({ name: 'Show API', alias: 'tableAPI' });

    $scope.changeCurrentWorkspace = function (wk) {
        $scope.currentWorkspace = wk;
        if(wk == $scope.workspaces[0]){
            $('#tablePeaks').parent().parent().parent().show();
            $('#tableProfiles').parent().parent().parent().hide();

        }else if(wk == $scope.workspaces[1]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().show();
        }
    }


    $scope.refreshTables = function(){
        $('#tablePeaks').bootstrapTable('refresh');
        $('#tableProfiles').bootstrapTable('refresh');
        $('#tableRelations').bootstrapTable('refresh');
        $('#tableAPI').bootstrapTable('refresh');
    }

    $timeout(callAtTimeout, 2500);
    function callAtTimeout() {
        $scope.showRecommend = true;
        $scope.recommended = [
            {'name':"PC3",'flag':'Cell','annotation':'CellId'},
            {'name':"NPC",'flag':'Cell','annotation':'CellId'},
            {'name':"MCF7",'flag':'Cell','annotation':'CellId'},
            {'name':"UNC1215",'flag':'Replicate','annotation':'Pertiname'},
            {'name':"MS-275",'flag':'Replicate','annotation':'Pertiname'},
            {'name':"methylstat",'flag':'Replicate','annotation':'Pertiname'},
            {'name':"DYRK1A",'flag':'Peptide','annotation':'PrGeneSymbol'},
            {'name':"HN1",'flag':'Peptide','annotation':'PrGeneSymbol'}
        ];
    }

    $scope.toggle = function(el){

            $scope[el] = !$scope[el];
            if($scope.p100 == false && $scope.gcp == false) {
                if (el == 'p100') {
                    $scope.gcp = true;
                } else {
                    $scope.p100 = true;
                }
            }

        $scope.refreshTables();

    }

    $scope.getToggleClass = function(status){
            return {
                toggle: status,
                untoggle: !status
            };
    }

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
        $scope.refreshTables();
    };

    $scope.searchClicked = function() {
        $scope.getRecommendation();
        $scope.refreshTables();
    };

    // ok
    $scope.recommendedClicked = function($newTag) {

        $scope.tags.push($newTag);

        var index = $scope.recommended.indexOf($newTag);
        $scope.recommended.splice(index,1);

        if($scope.recommended.length == 0){
            $scope.getRecommendation();
        }
        $scope.refreshTables();
    };

    //

    //generate random rows
    // Tables Wenz...
    //$(function () {
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
    //});

    //$(function () {
    $('#tableProfiles').bootstrapTable({
        url: '/pilincs/api-assays-paged/',
        queryParams: 'postQueryParams',
        pagination: 'true',
        sidePagination: 'server',
        showExport: 'true',
        columns: [{
            field: 'assayType',
            title: 'Profile'
        }, {
            field: 'cellId',
            title: 'Cell'
        }],
        responseHandler: function (res) {
            return res;
        }
    });

    $('#tableProfiles').parent().parent().parent().hide();

}]);







