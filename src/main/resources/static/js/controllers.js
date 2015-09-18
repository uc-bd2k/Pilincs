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
    $scope.workspaces.push({name: 'Merge', alias: 'tableMerge', active: true});
    $scope.workspaces.push({name: 'Explore', alias: 'tableAPI', active: true});
    $scope.workspaces.push({name: 'HeatMap', alias: 'tableRelations', active: true});
    //$scope.workspaces.push({ name: 'Charts', alias: 'tableRelations',active:true });

    $scope.workspaces.push({name: 'API', alias: 'tableAPI', active: true});

    $scope.currentWorkspace = $scope.workspaces[1];
    $scope.previousWorkspace = $scope.workspaces[1];

    $scope.heatMap = {};

    $scope.exporeLoaded = false;

    $scope.changeCurrentWorkspace = function (wk) {
        $scope.currentWorkspace = wk;
        if(wk == $scope.workspaces[0]){
            $('#tablePeaks').parent().parent().parent().show();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableMerge').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').hide();

        }else if(wk == $scope.workspaces[1]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().show();
            $('#tableMerge').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').hide();

        }else if(wk == $scope.workspaces[2]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableMerge').parent().parent().parent().show();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').hide();


        }else if(wk == $scope.workspaces[3]){
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableMerge').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').show();
            $('#tableAPI').hide();
            $('#heatMapRadios').show();
            if (!$scope.exporeLoaded) {
                $scope.loadExploreTab();
                $scope.exporeLoaded = true;
            }


        } else if (wk == $scope.workspaces[4]) {
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableMerge').parent().parent().parent().hide();
            $('#tableRelations').show();
            $('#tableExplore').hide();
            $('#tableAPI').hide();
            $scope.loadHeatMap();

            //test();
        } else if (wk == $scope.workspaces[5]) {
            $('#tablePeaks').parent().parent().parent().hide();
            $('#tableProfiles').parent().parent().parent().hide();
            $('#tableMerge').parent().parent().parent().hide();
            $('#tableRelations').hide();
            $('#tableExplore').hide();
            $('#tableAPI').show();
            //test();
        }
    };

    $scope.refreshTables = function(){
        $('#tablePeaks').bootstrapTable('refresh');
        $('#tableProfiles').bootstrapTable('refresh');
        //$('#tableProfiles').bootstrapTable('mergeCells', {index: 3, field: 'cellId', colspan: 1, rowspan: 3});
        $('#tableMerge').bootstrapTable('refresh');
        $scope.loadHeatMap();
        $scope.loadExploreTab();
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
        return $http.get('/pilincs/api-tags', {cache: false}).then(function (response) {
            var annotations = response.data;
            return annotations.filter(function(annotation) {
                if ($scope.currentWorkspace != $scope.workspaces[0]) {
                    return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1
                        && annotation.flag != 'Peptide';
                } else {
                    return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1;
                }
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
                            return "pink";//"grey";
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

        columns: [
            {
                field: 'background',
                title: 'Background',
                visible: false
            },
            {
                field: 'assayType',
                title: 'Assay',
                cellStyle: function (value, row, index) {

                    if (row.background == '1') {
                        return {classes: ['info']}
                    } else {
                        return {classes: 'success'}
                    }

                }
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
                field: 'runId',
                title: 'RunId',
                visible: true,
                cellStyle: function (value, row, index) {

                    if (row.background == '1') {
                        return {classes: ['info']}
                    } else {
                        return {classes: 'success'}
                    }

                }
            }
        ],
        responseHandler: function (res) {
            return res;
        }
    });

    $('#tableMerge').bootstrapTable({
        url: '/pilincs/api-merged-profiles-paged/',
        queryParams: 'postQueryParams',
        pagination: 'true',
        sidePagination: 'server',
        onLoadSuccess: function () {
            d3.selectAll(".mergedchart").each(function (d) {

                    var parsed = JSON.parse(d3.select(this).attr("vector"));
                    var series = parsed.series;
                    var height = 30;
                    var seriesLength = series.length;
                    var labels = [];
                    var min = Number.POSITIVE_INFINITY;
                    var max = Number.NEGATIVE_INFINITY;

                    var firstP100 = false;
                    var firstGCP = false;

                    var p100Series = [];
                    var gcpSeries = [];


                    for (var n = 0; n < seriesLength; n++) {

                        var localSeries = series[n];
                        var dose = localSeries.dose;
                        var time = localSeries.time;
                        var assay = localSeries.assay;
                        var runid = localSeries.runId;
                        var replicates = localSeries.replicates;
                        labels[n] = assay + "  d=" + dose + ", t=" + time + ", (" + runid + ", " + replicates + ")";

                        var useAssay = false;
                        if (assay == "P100" && firstP100 == false) {
                            useAssay = true;
                            p100Series = localSeries.data;
                            firstP100 = true;
                        }

                        if (assay == "GCP" && firstGCP == false) {
                            useAssay = true;
                            gcpSeries = localSeries.data;
                            firstGCP = true;
                        }

                        if (useAssay == true) {

                            for (var j = 0; j < localSeries.data.length; j++) {
                                var tmpMin = localSeries.data[j].min;
                                var tmpMax = localSeries.data[j].max;
                                if (tmpMin < min) min = tmpMin;
                                if (tmpMax > max) max = tmpMax;
                            }
                        }
                    }

                    var range = max - min;
                    //console.log(min+" "+max);

                    // Legend
                    var legend = d3.select(this).selectAll('text').data(labels);
                    legend
                        .enter().append('text')
                        .attr('x', 800)
                        .attr('y', 6)
                        .attr("text-anchor", "start")
                        .attr('transform', function (d, i) {
                            return "translate(0," + i * 12 + ")";
                        })
                        .text(function (d) {
                            return d;
                        })
                        .style('font-size', '8px');

                    legend.exit().remove();

                    // Dots

                    var scaling = height / range;
                    max = max * scaling;

                    //console.log(p100Series);

                    p100Series = p100Series.map(function (x) {
                        return {"assay": "P100", "value": x.avg * scaling};
                    });

                    gcpSeries = gcpSeries.map(function (x) {
                        return {"assay": "GCP", "value": x.avg * scaling};
                    });

                    var mergedSeries = p100Series.concat(gcpSeries);

                    //console.log(mergedSeries);

                    var chart = d3.select(this).selectAll('rect').data(mergedSeries);
                    chart
                        .enter().append("rect")
                        .attr("fill", function (d) {
                            if (d.assay == "P100") {
                                return '#7B4EA4';
                            } else {
                                return '#8c564b';
                            }
                        })
                        .attr("x", function (d, i) {
                            if (d.assay == "GCP" && firstP100 == false) {
                                return 480 + i * 5;
                            } else {
                                return i * 5;
                            }
                        })
                        .attr("width", 4)
                        .attr("y", function (d) {
                            if (d.value <= 0) {
                                return max;
                            } else {
                                return max - d.value;
                            }
                        })
                        .attr("height", function (d) {
                            if (d.value < 0) {
                                return -d.value;
                            } else {
                                return d.value;
                            }
                        });

                    chart.exit().remove();

                }
            )
        },
        columns: [
            {
                field: 'nTuple',
                title: 'Cell - perturbation',
                visible: true
            },
            {
                field: 'chart',
                title: 'Merged Profiles'
            }
        ],
        responseHandler: function (res) {

            for (var i = 0; i < res.rows.length; i++) {
                res.rows[i].chart = "<svg height=\"30px\" width=\"900px\" class=\"mergedchart\" vector=" + res.rows[i].vector + "></div>";
                delete res.rows[i].vector;
            }


            return res;
        }
    });

    $scope.changeCurrentWorkspace($scope.workspaces[1]);

    // --------------
    //UI configuration


    // HeatMap tab
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


    // Explore tab
    $scope.loadExploreTab = function () {

        var scope = angular.element('#assayButtons').scope();
        var queryContent = [];
        queryContent.push({"p100": scope.p100}); // scope.p100
        queryContent.push({"gcp": scope.gcp});

        queryContent = queryContent.concat($scope.tags);

        $('#exploreMessage').show();

        var res = $http.post('/pilincs/api-explore', JSON.stringify(queryContent));
        res.success(function (data, status, headers, config) {

            console.log(data);

            var assayNames = data.assayNames,
                cellNames = data.cellNames,
                pertNames = data.pertNames,
                doseNames = data.doseNames,
                timeNames = data.timeNames,
                rows = data.rows;

            console.log("Assay names: " + assayNames);
            console.log("Cell names: " + cellNames);
            console.log("Pert names: " + pertNames);
            console.log("Dose names: " + doseNames);
            console.log("Time names: " + timeNames);
            console.log("Rows: " + rows.length);

            var width = 900,
                height = 500;

            var fill = ['#1f77b4', '#ff7f0e', '#2ca02c', '#9467bd', '#8c564b'];

            var spaceX = width / 6;
            var d = 0;
            var y0 = 20;
            var y1 = 130;
            var y2 = 370;
            var nodes = [];
            var foci = [
                [{x: spaceX - d, y: y1},
                    {x: 2 * spaceX - d, y: y1},
                    {x: 3 * spaceX - d, y: y1},
                    {x: 4 * spaceX - d, y: y1},
                    {x: 5 * spaceX - d, y: y1}],

                [{x: spaceX - d, y: y2},
                    {x: 2 * spaceX - d, y: y2},
                    {x: 3 * spaceX - d, y: y2},
                    {x: 4 * spaceX - d, y: y2},
                    {x: 5 * spaceX - d, y: y2}]
            ];

            var svg = d3.select("#explorer");

            svg.attr("width", width)
                .attr("height", height);

            var background = d3.select("#explorer").select('g#background'),
                dots = d3.select("#explorer").select('g#dots'),
                colLabels = d3.select("#explorer").select('g#colLabels'),
                rowLabels = d3.select("#explorer").select('g#rowLabels'),
                legendRect = d3.select("#explorer").select('g#legendRect'),
                legendLabel = d3.select("#explorer").select('g#legendLabel');

            var assayLabels = ['P100', 'GCP'];
            rowLabels = rowLabels.selectAll('text').data(assayLabels);
            rowLabels.enter()
                .append('text')
                .attr('x', 10)
                .attr('y', 5)
                .attr('transform', function (d, i) {
                    if (i == 0) {
                        return "translate(0," + y1 + ")";
                    } else {
                        return "translate(0," + y2 + ")";
                    }
                })
                .text(function (d) {
                    return d;
                })
                .style('font-size', '14px');


            var cellLabels = ['MCF7', 'NPC', 'A375', 'PC3', 'A549'];
            colLabels = colLabels.selectAll('text').data(cellLabels);
            colLabels.enter()
                .append('text')
                .attr('x', -10)
                .attr('y', 12)
                .attr('transform', function (d, i) {
                    return "translate(" + (i + 1) * spaceX + ",0)";
                })
                .text(function (d) {
                    return d;
                })
                .style('font-size', '14px');

            var backData = ['', ''];
            background = background.selectAll('rect').data(backData);

            background
                .enter()
                .append("rect")
                .attr('x', function (d, i) {
                    return (1.5 + 2 * i) * spaceX;
                })
                .attr('y', y0)
                .attr('width', spaceX)
                .attr('height', height - 2 * y0)
                .style("fill", d3.rgb(229, 231, 233));

            var legendData = [
                {color: fill[0], label: '0.0 - 0.1'},
                {color: fill[1], label: '0.5 - 1.5'},
                {color: fill[2], label: '2.0 - 3.5'},
                {color: fill[3], label: '5.0 - 10'},
                {color: fill[4], label: '12  - 25'}];

            // swatches
            legendRect = legendRect.selectAll('rect').data(legendData);
            legendRect
                .enter()
                .append("rect")
                .attr('x', width - 70)
                .attr('y', function (d, i) {
                    return height - y0 - 60 + i * 12;
                })
                .attr('width', 10)
                .attr('height', 10)
                .style("fill", function (d) {
                    return d3.rgb(d.color);
                });

            //labels
            legendLabel = legendLabel.selectAll('text').data(legendData);
            legendLabel
                .enter()
                .append('text')
                .attr('x', width - 55)
                .attr('y', function (d, i) {
                    return height - y0 - 60 + i * 12 + 9;
                })
                .text(function (d) {
                    return d.label;
                })
                .style('font-size', '12px');

            rowLabels.exit().remove();
            colLabels.exit().remove();
            background.exit().remove();
            legendRect.exit().remove();
            legendLabel.exit().remove();

            var howTick;

            var force = d3.layout.force()
                .nodes(nodes)
                .links([])
                .size([width, height])
                .friction(0.8)
                .linkDistance(0)
                .linkStrength(2)
                .charge(-3)
                .gravity(0.01)
                .theta(0.8)
                .alpha(0.1)
                .on("tick", tick);

            var node = dots.selectAll("circle");

            function tick(e) {
                var k = .15 * e.alpha;

                nodes.forEach(function (o, i) {
                    o.y += (o.foci.y - o.y) * k;
                    o.x += (o.foci.x - o.x) * k;
                });

                node
                    .attr("cx", function (d) {
                        return d.x;
                    })
                    .attr("cy", function (d) {
                        return d.y;
                    });
            }

            var sorted = [];

            for (var i = 0; i < rows.length; i++) {

                var assay = assayNames[rows[i].int0];
                var cell = cellNames[rows[i].int1];
                //var pert = rows[i].int2;
                var dose = doseNames[rows[i].int3];


                var assayIndex = assayLabels.indexOf(assay);
                var cellIndex = cellLabels.indexOf(cell);
                //var pert = pertNames.indexOf(pert);

                var fociLocal = foci[assayIndex][cellIndex];


                var subGroupId;
                var disturb = {};
                var delta = 20;

                switch (dose) {
                    case '0':
                    case '0.05':
                    case '0.1':
                        subGroupId = 0;
                        disturb = {dx: 0, dy: 0};
                        break;
                    case '0.5':
                    case '1':
                    case '1.5':
                        subGroupId = 1;
                        disturb = {dx: -delta, dy: 0};
                        break;
                    case '2':
                    case '3':
                    case '3.5':
                        subGroupId = 2;
                        disturb = {dx: 0, dy: -delta};
                        break;
                    case '5':
                    case '6':
                    case '10':
                        subGroupId = 3;
                        disturb = {dx: delta, dy: 0};
                        break;
                    case '12':
                    case '25':
                        subGroupId = 4;
                        disturb = {dx: 0, dy: delta};
                        break;
                }

                var fillColor = fill[subGroupId];
                //fillColor = '#FFFFFF';

                sorted.push({foci: fociLocal, color: fillColor, group: subGroupId});

            }

            function compare(a, b) {
                if (a.group < b.group)
                    return -1;
                if (a.group > b.group)
                    return 1;
                return 0;
            }

            sorted.sort(compare);

            for (var j = 0; j < sorted.length; j++) {

                nodes.push({foci: sorted[j].foci, color: sorted[j].color});

                force.start();

                node = node.data(nodes);

                node.enter().append("circle")
                    .attr("class", "node")
                    .attr("cx", function (d) {
                        return d.foci.x;//+ d.disturb.dx;
                    })
                    .attr("cy", function (d) {
                        return d.foci.y;// + d.disturb.dy;
                    })
                    .attr("r", 3)
                    .style("fill", function (d) {
                        return d3.rgb(d.color);
                    })
                    .style("stroke", function (d) {
                        return d3.rgb(d.color).darker(1);
                    });
                node.exit().remove();
            }
            $('#exploreMessage').hide();
        });
    };
}]);







