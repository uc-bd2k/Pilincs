function postQueryParams(params) {
    //console.log($scope.tags);


    var scope = angular.element('#assayButtons').scope();
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

appControllers.controller('ExploreCtrl', ['$scope', function ($scope) {
}]);

appControllers.controller('MainCtrl', ['$scope', '$http', '$timeout',function($scope, $http, $timeout) {

    $scope.recommendText = 'You may like: ';
    $scope.showRecommend = false;
    $scope.tags = [];
    $scope.p100 = true;
    $scope.gcp = false;
    $scope.l1000 = false;
    $scope.heatmapMessage = '';

    $scope.workspaces = [];
    $scope.workspaces.push({name: 'Raw Data', alias: 'tablePeaks', active: true});
    $scope.workspaces.push({name: 'Profiles', alias: 'tableProfiles', active: true});
    $scope.workspaces.push({name: 'HeatMap', alias: 'tableRelations', active: true});
    //$scope.workspaces.push({ name: 'Charts', alias: 'tableRelations',active:true });
    $scope.workspaces.push({name: 'Explore', alias: 'tableAPI', active: true});
    $scope.workspaces.push({ name: 'Show API', alias: 'tableAPI',active:true });

    $scope.currentWorkspace = $scope.workspaces[1];
    $scope.previousWorkspace = $scope.workspaces[1];

    $scope.heatMap = {};

    $scope.changeCurrentWorkspace = function (wk) {
        $scope.currentWorkspace = wk;
        if(wk == $scope.workspaces[0]){
            $('#tablePeaks').parent().parent().parent().show();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').hide();

        }else if(wk == $scope.workspaces[1]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().show();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').hide();

        }else if(wk == $scope.workspaces[2]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableRelations').show();
            $('#tableExplore').hide();
            $('#tableAPI').hide();
            $('#heatMapRadios').show();
            $scope.loadHeatMap();

        }else if(wk == $scope.workspaces[3]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').show();
            $('#tableAPI').hide();
            test();

        } else if (wk == $scope.workspaces[4]) {
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').show();
            test();
        }
    };

    $scope.refreshTables = function(){
        $('#tablePeaks').bootstrapTable('refresh');
        $('#tableProfiles').bootstrapTable('refresh');
        $scope.loadHeatMap();
    };

    $timeout(callAtTimeout, 0);
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

    };

    $scope.getToggleClass = function(status){
            return {
                toggle: status,
                untoggle: !status
            };
    };

    $scope.loadTags = function($query) {
        return $http.get('/pilincs/api-tags', { cache: true}).then(function(response) {
            var annotations = response.data;
            return annotations.filter(function(annotation) {
                return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1;
            });
        });
    };



    $scope.getRecommendation = function() {

        if($scope.currentWorkspace == $scope.workspaces[1]){
            if($scope.tags[$scope.tags.length - 1].annotation=='PrGeneSymbol'){
                $scope.recommendText = 'Ups! GeneSymbol can\'t be used to filter profiles. ';
                $scope.recommended = [{'name':"PC3",'flag':'Cell','annotation':'CellId'}];
                return;
            }else{
                $scope.recommendText = 'You may like: ';
            }
        }

        if ($scope.tags.length < 5) {
            var res = $http.post('/pilincs/api-recommend', JSON.stringify($scope.tags));
            res.success(function (data, status, headers, config) {
                if(data.length == 0){
                    callAtTimeout();
                }
                if($scope.currentWorkspace == $scope.workspaces[1]){
                    data = data.filter(function(value){
                        if(value.annotation=='PrGeneSymbol'){
                            return false;
                        }else{
                            return true;
                        }
                    })
                }
                $scope.recommended = data;
                $scope.showRecommend = true;
                //console.log(data);
            });
        }else{
            $scope.showRecommend = false;
        }
    };

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
                title: 'Perturbation'
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
                field: 'chromatogramsUrl',
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
    //});
///    $('#tablePeaks').parent().parent().parent().hide();


        //$(".barchart").peity("bar",{width:700});

    //$(function () {
    $('#tableProfiles').bootstrapTable({
        url: '/pilincs/api-profiles-paged/',
        queryParams: 'postQueryParams',
        pagination: 'true',
        sidePagination: 'server',
        toolbar: '#ilincs',
        //showExport: true,
        toolbarAlign: 'right',
        //showColumns: true,
        detailView: true,
        detailFormatter: function (index, row) {
            return "<div class=\"row\">" +
                "<div class=\"col-md-3\"><b style=\"color: #23527c;\">Selected profile</b> <br/>" +
                "<br/><b>Assay: </b>" + row.assayType +
                "<br/><b>RunId: </b>" + row.runId +
                "<br/><b>ReplicateId: </b>" + row.replicateId +
                "<br/><b>PertIname: </b>" + row.pertIname +
                "<br/><b>CellId: </b>" + row.cellId +
                "<br/><b>Dose: </b>" + row.pertDose +
                "<br/><b>Time: </b>" + row.pertTime +
                "<br/><br/><b style=\"color: #23527c;\">Pearson correlation:</b>" +
                "</div>" +
                "<div class=\"col-md-3\"><b style=\"color: #23527c;\">Most correlated profile</b><br/><br/> " + row.positiveCorrelation + "</div>" +
                "<small><b style=\"color: #23527c;\">Contribution to correlation </b><br/> " + row.positivePeptides + "</small>";
        },

        onLoadSuccess: function(){

            //  Tooltip Object
            var tooltip = d3.select("body")
                .append("div").attr("id", "tooltip")
                .style("position", "absolute")
                .style("z-index", "10")
                .style("visibility", "hidden") //hidden
                .text("a simple tooltip");

            function mouseover(d) {
                if ((d+"").length > 0) {
                    tooltip.style("visibility", "visible");
                    tooltip.transition()
                        .duration(200)
                        .style("opacity", .9);
                    tooltip.html(d.name)
                        .style("left", (d3.event.pageX) + 30 + "px")
                        .style("top", (d3.event.pageY) + "px");
                }
            }

            function mouseout (d) {
                //tooltip.transition()
                //    .duration(500)
                //    .style("opacity", 0);
                tooltip.style("visibility", "hidden");
                var $tooltip = $("#tooltip");
                $tooltip.empty();
            }


            d3.selectAll(".barchart").each( function(d, i){

                var data = JSON.parse(d3.select(this).attr("vector")).data;
                //console.log(data[0]);

                var min = Number.POSITIVE_INFINITY;
                var max = Number.NEGATIVE_INFINITY;
                var tmp;
                for (var i=data.length-1; i>=0; i--) {
                    tmp = data[i].value;
                    if (tmp < min) min = tmp;
                    if (tmp > max) max = tmp;
                }

                //console.log(min+" "+max);
                var range = max - min;

                var chart = d3.select(this);
                var height = 30; //chart.attr("height");
                var width = data.length * 5;//chart.attr("width");

                //console.log(height+" "+width);
                d3.select(this).attr("height" , height);
                d3.select(this).attr("width" , width);

                var scaling = height / range;
                max = max * scaling;
                data = data.map(function(x) {
                    if(x.imputed === true){
                        return {"name": "IMPUTED >> "+x.name, "value": x.value * scaling, "imputed": x.imputed};
                    }else {
                        return {"name": x.name, "value": x.value * scaling, "imputed": x.imputed};
                    }
                });


                chart.selectAll("rect")
                    .data(data)
                    .enter().append("rect")
                    .attr("class","bar")
                    .on("mouseover", mouseover)
                    .on("mouseout", mouseout)
                    .attr("fill",function(d){
                        if(d.imputed === true){
                            return "grey";
                        }
                        if(d.value < 5 && d.value > -5 ){
                        return "rgb(79,159,207)";
                    }else{
                        return "steelblue";
                    }})
                    .attr("x", function(d,i){return i*5;})
                    .attr("width", 4)
                    .attr("y",function(d){
                        if(d.value <= 0){
                            return max;
                        }else{
                            return max - d.value;
                        }})
                    .attr("height", function(d) {
                        if(d.value < 0){
                            return -d.value;
                        }else{
                            return d.value;
                        }
                    })

                ;

            })},
       // onColumnSwitch: function(){},//this.onLoadSuccess(),//function(){
            //$(".barchart").peity("bar",{width:700});
        //},
        columns: [
            {
                field: 'assayType',
                title: 'Assay'
            },
            {
                field: 'runId',
                title: 'RunId',
                visible: false
            },
            {
                field: 'replicateId',
                title: 'ReplicateId',
                visible: false
            },
            {
                field: 'pertIname',
                title: 'Perturbation'
            }
            , {
                field: 'cellId',
                title: 'Cell'
            },
            {
                field: 'pertDose',
                title: 'Dose',
                visible: true
            },
            {
                field: 'pertTime',
                title: 'Time',
                visible: true
            },
            {
                field: 'vector',
                title: 'Profile'
            },
            {
                field: 'correlatedVector',
                title: 'Most correlated',
                visible: false
            },
            {
                field: 'sth',
                title: 'Panorama',
                visible: false
            }],
        responseHandler: function (res) {
            return res;
        }
    });

    //$('#tableProfiles').parent().parent().parent().hide();
    $scope.changeCurrentWorkspace($scope.workspaces[1]);

    // --------------
    //UI configuration

    $scope.loadHeatMap = function() {

        var queryContent = [];
        queryContent.push({"p100": true});
        queryContent.push({"gcp": false});
        queryContent = queryContent.concat($scope.tags);

        //$('#heatMapMessage').text('Loading data ...');
        $('#heatMapMessage').show();
        //$scope.heatmapMessage = 'Loading data ...';

        // Color scale
        var scaleData = d3.range(1, 20, 1);
        var grid = 10;

        var scale = d3.select('#heatMap').select('svg').select('g#colorScale').attr("transform", 'translate(0,0)');
        scale = scale.selectAll('rect').data(scaleData);
        scale.enter()
            .append('rect')
            .attr('x', 0)
            .attr('y', 0)
            //.transition().duration(function(d,i){return (i % 5) * 1000;})
            .attr('width', grid)
            .attr('height', grid)
            .attr('transform', function (d, i) {
                return "translate(" + (i * (grid + 1)) + ",0)";
            })
            .attr('class', function (d, i) {
                return 'cs-' + (i + 1);
            })
        ;

        scale.exit().remove();

        // Query server for profiles

        var res = $http.post('/pilincs/api-heatmap', JSON.stringify(queryContent));
        res.success(function (data, status, headers, config) {


            //console.log($('#heatMapMessage').text());
            console.log(data);

            var topMargin = 80,
                leftMargin = 90,
                shortenLabel = 15,
                grid = 10;

            var peptideNameOrders = data.peptideNames,
                profileNames = data.profileNames,
                rows = data.rows;

            // Shorten profile names
            profileNames = profileNames.map(function (item) {
                if(item.length > shortenLabel){
                    return item.substring(0,shortenLabel) + '';
                }else{
                    return item;
                }
            });

            console.log("Profile names: " + profileNames.length);
            console.log("Peptide name-orders: " + peptideNameOrders.length);
            console.log("Rows: " + rows.length);
            //$('#heatMapMessage').text('Rendering data');

            // Row Labels
            var rowLabels = d3.select('#heatMap').select('svg').select('g#rowLabels').attr("transform", 'translate(0,' + (topMargin + 10) + ')');

            rowLabels = rowLabels.selectAll('text').data(peptideNameOrders);
            rowLabels.enter()
                .append('text')
                .attr('x',0)
                .attr('y',30)
                .attr("text-anchor", "start")
                .attr('transform',function(d,i){ return "translate(0," + i * grid + ") rotate(0)";})
                .text(function (d) {
                    return d.peptideName;
                })
                .style('font-size','8px');

            rowLabels.exit().remove();

            // Column Labels
            var colLabels = d3.select('#heatMap').select('svg').select('g#colLabels').attr("transform", 'translate(97,100)');

            colLabels = colLabels.selectAll('text').data(profileNames);
            colLabels.enter()
                .append('text')
                .attr('x',0)
                .attr('y',0)
                .attr("text-anchor", "start")
                .attr('transform', function (d, i) {
                    return "translate(" + i * grid + ",0) rotate(270)";
                })
                .text(function(d){return d;})
                .style('font-size','8px');

            colLabels.exit().remove();

            // Matrix
            var matrixRect = d3.select('#heatMap').select('svg').select('g#matrix')
                .attr("transform", 'translate(' + leftMargin + ',' + (topMargin + 30) + ')');

            var cells = [];

            for (var rowId = 0; rowId < rows.length; rowId++) {
                var row = rows[rowId];
                var colors = row.colors;
                var rowIndex = row.rowIndex;
                var clusterOrder = row.clusterOrder;

                for (var columnId = 0; columnId < colors.length; columnId++) {
                    cells.push({
                        "rowIndex": rowIndex,
                        "clusterOrder": clusterOrder,
                        "columnId": columnId,
                        "colorId": colors[columnId]
                    });
                }
            }
            matrixRect = matrixRect.selectAll('rect').data(cells);
            matrixRect.enter()
                .append('rect')
                .attr('x', 0)
                .attr('y', 0)
                .attr('width', (grid - 1))
                .attr('height', (grid - 1))
                .attr('transform', function (d) {
                    return "translate(" + d.columnId * grid + "," + d.rowIndex * grid + ")";
                })
                .attr('class', function (d) {
                    return 'cs-' + (d.colorId + 1);
                });

            matrixRect.exit().remove();
            $('#heatMapMessage').hide();

        });
        //$scope.heatmapMessage ='';
    };

}]);







