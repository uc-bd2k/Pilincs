var appModule = angular.module('pilincsModule', []);

appModule.controller('NavigationCtrl', ['$scope', '$http', 'SharedService',
    function ($scope, $http, SharedService) {

        var self = this;
        self.tags = [];
        self.activeSite = '';

        self.changeSite = function (el) {
            self.activeSite = el;
        };

        // Load tags for autocomplete
        self.loadTags = function ($query) {
            return $http.get('/pilincs/api-tags', {cache: true}).then(function (response) {
                var annotations = response.data;
                return annotations.filter(function (annotation) {
                    return annotation.name.toLowerCase().indexOf($query.toLowerCase()) != -1;
                });
            });
        };

        // Trigger when tag was added or removed
        self.tagAddedRemoved = function () {
            SharedService.updateTags(self.tags);
            $scope.$broadcast('updateTags');
        };
    }]);

appModule.factory('SharedService', [function () {

    var localTags = [];

    return {
        getTags: function () {
            var queryContent = [];
            queryContent.push({"p100": true});
            queryContent.push({"gcp": true});

            if (localTags != null) {
                queryContent = queryContent.concat(localTags);
            }

            return JSON.stringify(queryContent);
        },
        getTagsAsArray: function () {
            return localTags;
        },
        updateTags: function (tags) {
            localTags = tags;
        }
    }
}]);

appModule.directive('profileBarchartMerged', ['SharedService', function (SharedService) {
    function link(scope, element, attr) {
        var data = scope.data;
        var chart = d3.select(element[0]).append('svg');

        var height = 30;
        chart.attr({width: 900, height: height});

        var labels = [];
        var min = Number.POSITIVE_INFINITY;
        var max = Number.NEGATIVE_INFINITY;

        var firstP100 = false;
        var firstGCP = false;

        var p100Series = [];
        var gcpSeries = [];

        // Find MIN and MAX
        for (var n = 0; n < data.length; n++) {

            var localSeries = data[n];
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
        var legend = chart.selectAll('text').data(labels);//d3.select(this).selectAll('text').data(labels);
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

        var rects = chart.selectAll('rect').data(mergedSeries);
        rects
            .enter().append("rect")
            .attr("fill", function (d) {
                if (d.assay == "P100") {
                    return 'steelblue';
                } else {
                    return 'brown';
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

        rects.exit().remove();
    }

    return {
        link: link,
        restrict: 'E',
        scope: {data: '='}
    }
}]);

appModule.directive('profileBarchart', ['SharedService', function (SharedService) {
    function link(scope, element, attr) {

        var data = scope.data;

        var selectedPeptides = SharedService.getTagsAsArray().map(function (x) {
            if (x.annotation == "PrGeneSymbol") {
                return x.name;
            } else {
                return '-';
            }
        });

        var tooltip = d3.select("body")
            .append("div").attr("id", "tooltip")
            .style("position", "absolute")
            .style("z-index", "10")
            .style("visibility", "hidden") //hidden
            .text("a simple tooltip");

        var chart = d3.select(element[0]).append('svg');
        var width = data.length * 5;
        var height = 30;
        chart.attr({width: width, height: height});

        function mouseover(d) {
            if ((d + "").length > 0) {
                tooltip.style("visibility", "visible");
                tooltip.transition()
                    .duration(200)
                    .style("opacity", .9);
                tooltip.html("GeneName: " + d.gene + ", Id: " + d.name)
                    .style("left", (d3.event.pageX) + 30 + "px")
                    .style("top", (d3.event.pageY) + "px");
            }
        }

        function mouseout(d) {
            tooltip.style("visibility", "hidden");
        }

        var min = Number.POSITIVE_INFINITY;
        var max = Number.NEGATIVE_INFINITY;
        var tmp;
        for (var i = data.length - 1; i >= 0; i--) {
            tmp = data[i].value;
            if (tmp < min) min = tmp;
            if (tmp > max) max = tmp;
        }

        //console.log(min+" "+max);
        var range = max - min;

        var scaling = height / range;
        max = max * scaling;
        data = data.map(function (x) {
            if (x.imputed === true) {
                return {
                    "name": "IMPUTED >> " + x.name,
                    "value": x.value * scaling,
                    "imputed": x.imputed,
                    "gene": x.gene
                };
            } else {
                return {
                    "name": x.name,
                    "value": x.value * scaling,
                    "imputed": x.imputed,
                    "gene": x.gene
                };
            }
        });


        chart.selectAll("rect")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .on("mouseover", mouseover)
            .on("mouseout", mouseout)
            .attr("fill", function (d) {
                if (selectedPeptides.indexOf(d.gene) != -1) {
                    return "brown";
                }
                if (d.imputed === true) {
                    return "pink";//"grey";
                }
                if (d.value < 5 && d.value > -5) {
                    return "rgb(79,159,207)";
                } else {
                    return "steelblue";
                }
            })
            .attr("x", function (d, i) {
                return i * 5;
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

    }

    return {
        link: link,
        restrict: 'E',
        scope: {data: '='}
    }
}]);

appModule.controller("TableCtrl", ['$scope', 'SharedService', '$http', '$location',
    function ($scope, SharedService, $http, $location) {

        var site = $location.path();

        $scope.itemsPerPage = 10;
        $scope.currentPage = 0;

        $scope.range = function () {
            var rangeSize = 5;
            var ret = [];
            var start;

            start = $scope.currentPage;
            if (start > $scope.pageCount() - rangeSize) {
                start = $scope.pageCount() - rangeSize;
            }

            for (var i = start; i < start + rangeSize; i++) {
                ret.push(i);
            }
            return ret;
        };

        $scope.prevPage = function () {
            if ($scope.currentPage > 0) {
                $scope.currentPage--;
            }
        };

        $scope.prevPageDisabled = function () {
            return $scope.currentPage === 0 ? "disabled" : "";
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pageCount() - 1) {
                $scope.currentPage++;
            }
        };

        $scope.nextPageDisabled = function () {
            return $scope.currentPage === $scope.pageCount() - 1 ? "disabled" : "";
        };

        $scope.pageCount = function () {
            return Math.ceil($scope.total / $scope.itemsPerPage);
        };

        $scope.setPage = function (n) {
            if (n > 0 && n < $scope.pageCount()) {
                $scope.currentPage = n;
            }
        };

        $scope.$on('updateTags', function (e) {
            $scope.loadData($scope.currentPage);
        });

        $scope.loadData = function (currPage) {

            switch (site) {

                case "/raw-data":
                    var url = '/pilincs/api-assays-paged/?order=asc&limit=' + $scope.itemsPerPage
                        + '&offset=' + currPage * $scope.itemsPerPage
                        + '&tags=' + SharedService.getTags();

                    $http.get(url)
                        .success(function (data) {
                            //console.log("NewValue: " + newValue);
                            $scope.total = data.total;
                            $scope.pagedItems = data.rows.map(function (row) {
                                row.value = row.value.toFixed(5);
                                row.panorama = {charts: row.chromatogramsUrl, gct: row.downloadUrl};
                                return row;
                            });
                        });
                    break;

                case "/technical-profiles":

                    var url = '/pilincs/api-profiles-paged/?order=asc&limit=' + $scope.itemsPerPage
                        + '&offset=' + currPage * $scope.itemsPerPage
                        + '&tags=' + SharedService.getTags();

                    $http.get(url)
                        .success(function (data) {
                            //console.log("NewValue: " + newValue);
                            $scope.total = data.total;
                            $scope.pagedItems = data.rows.map(function (row) {
                                row.vector = JSON.parse(row.vector).data;
                                return row;
                            });
                        });
                    break;
                case "/merged-profiles":
                    var url = '/pilincs/api-merged-profiles-paged/?order=asc&limit=' + $scope.itemsPerPage
                        + '&offset=' + currPage * $scope.itemsPerPage
                        + '&tags=' + SharedService.getTags();

                    $http.get(url)
                        .success(function (data) {
                            //console.log("NewValue: " + newValue);
                            $scope.total = data.total;
                            $scope.pagedItems = data.rows.map(function (row) {
                                row.vector = JSON.parse(row.vector).series;
                                return row;
                            });
                        });
                    break;
            }

        };

        $scope.$watch("currentPage", function (newValue, oldValue) {
            $scope.loadData(newValue);
        });
    }]);

appModule.controller("ExportCtrl" , ['SharedService', function(SharedService){
    var self = this;

    self.processingLevelRadio = 0;
    self.outputFormatRadio = 0;

    self.processingLevel = [
        {id: 0, name: 'Raw Data', selected: true},
        {id: 1, name: 'Profiles', selected: false},
        {id: 2, name: 'Merged profiles', selected: false}
    ];
    self.outputFormat = [
        {id: 0, name: 'JSON', selected: true}
    ];

    self.getURL = function(){

        var url;

        switch(self.processingLevelRadio){
            case 0:
                url = '/pilincs/api-assays-paged/?order=asc&limit=' + 1000
                    + '&offset=0'
                    + '&tags=' + SharedService.getTags();
                break;
            case 1:
                url = '/pilincs/api-profiles-paged/?order=asc&limit=' + 1000
                    + '&offset=0'
                    + '&tags=' + SharedService.getTags();
                break;
            case 2:
                url = '/pilincs/api-merged-profiles-paged/?order=asc&limit=' + 1000
                    + '&offset=0'
                    + '&tags=' + SharedService.getTags();
                break;
        }
        return url;
    }


}])

appModule.controller("ExploreCtrl", ['SharedService', '$http', function (SharedService, $http) {

    var self = this;

    self.showLoading = true;
    self.showFilter = false;
    self.filterFeature = [];
    self.groupRadio = 0;
    self.colorRadio = 'Dose';
    self.assayNamesOryginal = [];
    self.cellNamesOryginal = [];
    self.pertNamesOryginal = [];
    self.doseNamesOryginal = [];
    self.timeNamesOryginal = [];
    self.assayNames = [];
    self.cellNames = [];
    self.pertNames = [];
    self.doseNames = [];
    self.timeNames = [];
    self.rows = [];
    //
    self.assayCellStats = [];

    self.grouping = [
        {id: 0, name: 'Cell', selected: true}
    ];
    self.coloring = [
        //{id: 0, name: 'Cell', selected: false},
        //{id: 1, name: 'Perturbation', selected: false},
        {id: 2, name: 'Dose', selected: true},
        //{id: 3, name: 'Time', selected: false}
    ];


    self.computeCounts = function () {

        for (var i = 0; i < self.assayNames.length * self.cellNames.length; i++) {
            self.assayCellStats[i] = {
                count: 0,
                cell: self.cellNames.map(function (d) {
                    return 0;
                }),
                pert: self.pertNames.map(function (d) {
                    return 0;
                }),
                dose: self.doseNames.map(function (d) {
                    return 0;
                }),
                time: self.timeNames.map(function (d) {
                    return 0;
                })
            }
        }

        for (var i = 0; i < self.rows.length; i++) {

            var assay = self.assayNamesOryginal[self.rows[i].int0];
            var cell = self.cellNamesOryginal[self.rows[i].int1];
            var pert = self.pertNamesOryginal[self.rows[i].int2];
            var dose = self.doseNamesOryginal[self.rows[i].int3];
            var time = self.timeNamesOryginal[self.rows[i].int4];

            // Sorted IDs
            var assayId = self.assayNames.indexOf(assay);
            var cellId = self.cellNames.indexOf(cell);
            var pertId = self.pertNames.indexOf(pert);
            var doseId = self.doseNames.indexOf(dose);
            var timeId = self.timeNames.indexOf(time);

            // Quantities
            var clusterIndex = (assayId * self.cellNames.length) + cellId;

            self.assayCellStats[clusterIndex].count++;
            self.assayCellStats[clusterIndex].cell[cellId]++;
            self.assayCellStats[clusterIndex].pert[pertId]++;
            self.assayCellStats[clusterIndex].dose[doseId]++;
            self.assayCellStats[clusterIndex].time[timeId]++;
        }
    }

    self.updateFilterFeature = function () {
        console.log("Update filter feature");

        switch (self.colorRadio) {
            case 'Cell':
                self.filterFeature = self.cellNames;
                break;
            case 'Perturbation':
                self.filterFeature = self.pertNames;
                break;
            case 'Dose':
                self.filterFeature = self.doseNames;
                break;
            case 'Time':
                self.filterFeature = self.timeNames;
                break;
        }
        self.updateSVGlegend();
        self.updateSVGdots();
    }


    var res = $http.post('/pilincs/api-explore', JSON.stringify([{"p100": true}, {"gcp": true}]), {cache: true});

    res.success(function (data, status, headers, config) {

        self.assayNamesOryginal = data.assayNames;
        self.cellNamesOryginal = data.cellNames;
        self.pertNamesOryginal = data.pertNames;
        self.doseNamesOryginal = data.doseNames.map(function (d) {
            return parseFloat(d);
        });
        self.timeNamesOryginal = data.timeNames.map(function (d) {
            return parseInt(d);
        });
        self.rows = data.rows; // rows with indexes referring to unsorted labels

        // SORT
        self.assayNames = self.assayNamesOryginal.sort();
        self.cellNames = self.cellNamesOryginal.sort();
        self.pertNames = self.pertNamesOryginal.sort();
        self.doseNames = self.doseNamesOryginal.sort(function (a, b) {
            return a - b
        });
        self.timeNames = self.timeNamesOryginal.sort(function (a, b) {
            return a - b
        });

        // Here sort pertNames, cellNames, doseNames, assayNames

        self.computeCounts();
        self.updateFilterFeature();
        self.showLoading = false;
    });

    self.updateSVGlegend = function () {

        console.log("Update SVG legend");

        var svg = d3.select("#explorer");
        var width = svg.attr("width"),
            height = svg.attr("height");

        var colorScale = d3.scale.category20();

        var legendRect = d3.select("#explorer").select('g#legendRect'),
            legendLabel = d3.select("#explorer").select('g#legendLabel');

        legendRect.selectAll("*").remove();
        legendLabel.selectAll("*").remove();

        var legendData = self.filterFeature.map(function (d, i) {
            return {color: colorScale(i % 20), label: d};
        });

        // Legend Swatches
        legendRect = legendRect.selectAll('rect').data(legendData);
        legendRect
            .enter()
            .append("rect")
            .attr('transform', function (d, i) {
                return 'translate(' + (width - 60) + ',' + (20 + i * 12) + ') rotate(0)';
            })
            .attr('width', 10)
            .attr('height', 10)
            .style("fill", function (d) {
                return d3.rgb(d.color);
            })
            .style("stroke", function (d) {
                return d3.rgb(d.color).darker(1);
            });

        // Legend text
        legendLabel = legendLabel.selectAll('text').data(legendData);
        legendLabel
            .enter()
            .append('text')
            .text(function (d) {
                if (d.label.length > 7) {
                    return d.label.substring(0, 7) + '..';
                } else {
                    return d.label;
                }
            })
            .attr('transform', function (d, i) {
                return 'translate(' + (width - 45) + ',' + (29 + i * 12) + ') rotate(0)';
            })
            .style('font-size', '10px');

    }

    self.updateSVGdots = function () {

        console.log("Update SVG dots and quantities");
        var diameter = 100,
            format = d3.format(",d"),
            color = d3.scale.category20();

        var svg = d3.select("#explorer").select('g#dots')
            .attr("class", "bubble");


        for (var i = 0; i < self.assayCellStats.length; i++) {
            var levels = [];
            switch (self.colorRadio) {
                case 'Cell':
                    levels = self.assayCellStats[i].cell;
                    break;
                case 'Perturbation':
                    levels = self.assayCellStats[i].pert;
                    break;
                case 'Dose':
                    levels = self.assayCellStats[i].dose;
                    break;
                case 'Time':
                    levels = self.assayCellStats[i].count;
                    break;
            }
            var total = 0;
            levels.forEach(function (d) {
                total += d;
            });

            levels = levels.map(function (d, i) {
                return {className: "", packageName: i, value: d, percent: parseInt(100 * d / total)};
            })

            var data = {
                "children": levels
            };

            var bubble = d3.layout.pack()
                .sort(null)
                .size([diameter, diameter])
                .padding(1.5);

            var node = svg.selectAll(".node" + i)
                .data(bubble.nodes(data).filter(function (d) {
                    return !d.children;
                }))
                .enter().append("g")
                .attr("class", "node" + i)
                .attr("transform", function (d) {
                    return "translate(" + (100 + d.x + (i % 5) * 150) + "," + (d.y + (i < 5 ? 80 : 300)) + ")";
                });

            node.append("title")
                .text(function (d) {
                    return d.className + "Count: " + format(d.value) + " (" + d.percent + "%)";
                });

            node.append("circle")
                .attr("r", function (d) {
                    return d.r;
                })
                .style("fill", function (d) {
                    return color(d.packageName);
                });

            node.append("text")
                .attr("dy", ".3em")
                .style("text-anchor", "middle")
                .text(function (d) {
                    if (d.r > 10) {
                        return d.className.substring(0, d.r / 3);
                    }
                });

            svg.append("text")
                .attr("x",(100 + (i % 5) * 150))
                .attr("y",(i < 5 ? 80 : 300))
                .style("font-size", "10px")
                .text("Total: "+total);
        }
    }
}]);

