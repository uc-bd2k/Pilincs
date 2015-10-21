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

appModule.controller("ExploreCtrl", ['SharedService', '$http', function (SharedService, $http) {

    var self = this;
    self.showFilter = false;
    self.filterFeature = [];
    self.groupRadio = 0;
    self.colorRadio = '2';
    self.assayNames = [];
    self.cellNames = [];
    self.pertNames = [];
    self.doseNames = [];
    self.timeNames = [];
    self.rows = [];

    self.toggleFilter = function () {
        self.showFilter = !self.showFilter;
    };

    self.updateFilter = function () {
        switch (self.colorRadio) {
            case '0':
                self.filterFeature = self.cellNames.map(function (d, i) {
                    return {id: i, name: d, selected: true};
                });
                break;
            case '1':
                self.filterFeature = self.pertNames.map(function (d, i) {
                    return {id: i, name: d, selected: true};
                });
                break;
            case '2':
                self.filterFeature = self.doseNames.map(function (d, i) {
                    return {id: i, name: d, selected: true};
                });
                break;
            case '3':
                self.filterFeature = self.timeNames.map(function (d, i) {
                    return {id: i, name: d, selected: true};
                });
                break;
        }
        self.updateSVG();
    };

    self.updateSVG = function () {

    };

    self.grouping = [
        {id: 0, name: 'Cell', selected: true}
        //,
        //{id: 1, name: 'Perturbation', selected: false},
        //{id: 2, name: 'Dose', selected: false},
        //{id: 3, name: 'Time', selected: false}
    ];

    self.coloring = [
        {id: 0, name: 'Cell', selected: false},
        {id: 1, name: 'Perturbation', selected: false},
        {id: 2, name: 'Dose', selected: true},
        {id: 3, name: 'Time', selected: false}
    ];


    var res = $http.post('/pilincs/api-explore', JSON.stringify([{"p100": true}, {"gcp": true}]), {cache: true});

    res.success(function (data, status, headers, config) {

        self.assayNames = data.assayNames;
        self.cellNames = data.cellNames;
        self.pertNames = data.pertNames;
        self.doseNames = data.doseNames;
        self.timeNames = data.timeNames;
        self.rows = data.rows;

        self.updateFilter();
    });

    //
    //console.log("Assay names: " + self.assayNames);
    //console.log("Cell names: " + self.cellNames);
    //console.log("Pert names: " + self.pertNames);
    //console.log("Dose names: " + self.doseNames);
    //console.log("Time names: " + self.timeNames);
    //console.log("Rows: " + self.rows.length);

    //    var width = 900,
    //        height = 500;
    //
    //    var fill = ['#1f77b4', '#ff7f0e', '#2ca02c', '#9467bd', '#8c564b'];
    //
    //    var spaceX = width / 6;
    //    var d = 0;
    //    var y0 = 20;
    //    var y1 = 130;
    //    var y2 = 370;
    //    var nodes = [];
    //    var foci = [
    //        [{x: spaceX - d, y: y1},
    //            {x: 2 * spaceX - d, y: y1},
    //            {x: 3 * spaceX - d, y: y1},
    //            {x: 4 * spaceX - d, y: y1},
    //            {x: 5 * spaceX - d, y: y1}],
    //
    //        [{x: spaceX - d, y: y2},
    //            {x: 2 * spaceX - d, y: y2},
    //            {x: 3 * spaceX - d, y: y2},
    //            {x: 4 * spaceX - d, y: y2},
    //            {x: 5 * spaceX - d, y: y2}]
    //    ];
    //
    //    var svg = d3.select("#explorer");
    //
    //    svg.attr("width", width)
    //        .attr("height", height);
    //
    //    var background = d3.select("#explorer").select('g#background'),
    //        dots = d3.select("#explorer").select('g#dots'),
    //        colLabels = d3.select("#explorer").select('g#colLabels'),
    //        rowLabels = d3.select("#explorer").select('g#rowLabels'),
    //        legendRect = d3.select("#explorer").select('g#legendRect'),
    //        legendLabel = d3.select("#explorer").select('g#legendLabel');
    //
    //    var assayLabels = ['P100', 'GCP'];
    //    rowLabels = rowLabels.selectAll('text').data(assayLabels);
    //    rowLabels.enter()
    //        .append('text')
    //        .attr('x', 10)
    //        .attr('y', 5)
    //        .attr('transform', function (d, i) {
    //            if (i == 0) {
    //                return "translate(0," + y1 + ")";
    //            } else {
    //                return "translate(0," + y2 + ")";
    //            }
    //        })
    //        .text(function (d) {
    //            return d;
    //        })
    //        .style('font-size', '14px');
    //
    //
    //    var cellLabels = ['MCF7', 'NPC', 'A375', 'PC3', 'A549'];
    //    colLabels = colLabels.selectAll('text').data(cellLabels);
    //    colLabels.enter()
    //        .append('text')
    //        .attr('x', -10)
    //        .attr('y', 12)
    //        .attr('transform', function (d, i) {
    //            return "translate(" + (i + 1) * spaceX + ",0)";
    //        })
    //        .text(function (d) {
    //            return d;
    //        })
    //        .style('font-size', '14px');
    //
    //    var backData = ['', ''];
    //    background = background.selectAll('rect').data(backData);
    //
    //    background
    //        .enter()
    //        .append("rect")
    //        .attr('x', function (d, i) {
    //            return (1.5 + 2 * i) * spaceX;
    //        })
    //        .attr('y', y0)
    //        .attr('width', spaceX)
    //        .attr('height', height - 2 * y0)
    //        .style("fill", d3.rgb(229, 231, 233));
    //
    //    var legendData = [
    //        {color: fill[0], label: '0.0 - 0.1'},
    //        {color: fill[1], label: '0.5 - 1.5'},
    //        {color: fill[2], label: '2.0 - 3.5'},
    //        {color: fill[3], label: '5.0 - 10'},
    //        {color: fill[4], label: '12  - 25'}];
    //
    //    // swatches
    //    legendRect = legendRect.selectAll('rect').data(legendData);
    //    legendRect
    //        .enter()
    //        .append("rect")
    //        .attr('x', width - 70)
    //        .attr('y', function (d, i) {
    //            return height - y0 - 60 + i * 12;
    //        })
    //        .attr('width', 10)
    //        .attr('height', 10)
    //        .style("fill", function (d) {
    //            return d3.rgb(d.color);
    //        });
    //
    //    //labels
    //    legendLabel = legendLabel.selectAll('text').data(legendData);
    //    legendLabel
    //        .enter()
    //        .append('text')
    //        .attr('x', width - 55)
    //        .attr('y', function (d, i) {
    //            return height - y0 - 60 + i * 12 + 9;
    //        })
    //        .text(function (d) {
    //            return d.label;
    //        })
    //        .style('font-size', '12px');
    //
    //    rowLabels.exit().remove();
    //    colLabels.exit().remove();
    //    background.exit().remove();
    //    legendRect.exit().remove();
    //    legendLabel.exit().remove();
    //
    //    var howTick;
    //
    //    var force = d3.layout.force()
    //        .nodes(nodes)
    //        .links([])
    //        .size([width, height])
    //        .friction(0.8)
    //        .linkDistance(0)
    //        .linkStrength(2)
    //        .charge(-3)
    //        .gravity(0.01)
    //        .theta(0.8)
    //        .alpha(0.1)
    //        .on("tick", tick);
    //
    //    var node = dots.selectAll("circle");
    //
    //    function tick(e) {
    //        var k = .15 * e.alpha;
    //
    //        nodes.forEach(function (o, i) {
    //            o.y += (o.foci.y - o.y) * k;
    //            o.x += (o.foci.x - o.x) * k;
    //        });
    //
    //        node
    //            .attr("cx", function (d) {
    //                return d.x;
    //            })
    //            .attr("cy", function (d) {
    //                return d.y;
    //            });
    //    }
    //
    //    var sorted = [];
    //
    //    for (var i = 0; i < rows.length; i++) {
    //
    //        var assay = assayNames[rows[i].int0];
    //        var cell = cellNames[rows[i].int1];
    //        //var pert = rows[i].int2;
    //        var dose = doseNames[rows[i].int3];
    //
    //
    //        var assayIndex = assayLabels.indexOf(assay);
    //        var cellIndex = cellLabels.indexOf(cell);
    //        //var pert = pertNames.indexOf(pert);
    //
    //        var fociLocal = foci[assayIndex][cellIndex];
    //
    //
    //        var subGroupId;
    //        var disturb = {};
    //        var delta = 20;
    //
    //        switch (dose) {
    //            case '0':
    //            case '0.05':
    //            case '0.1':
    //                subGroupId = 0;
    //                disturb = {dx: 0, dy: 0};
    //                break;
    //            case '0.5':
    //            case '1':
    //            case '1.5':
    //                subGroupId = 1;
    //                disturb = {dx: -delta, dy: 0};
    //                break;
    //            case '2':
    //            case '3':
    //            case '3.5':
    //                subGroupId = 2;
    //                disturb = {dx: 0, dy: -delta};
    //                break;
    //            case '5':
    //            case '6':
    //            case '10':
    //                subGroupId = 3;
    //                disturb = {dx: delta, dy: 0};
    //                break;
    //            case '12':
    //            case '25':
    //                subGroupId = 4;
    //                disturb = {dx: 0, dy: delta};
    //                break;
    //        }
    //
    //        var fillColor = fill[subGroupId];
    //        //fillColor = '#FFFFFF';
    //
    //        sorted.push({foci: fociLocal, color: fillColor, group: subGroupId});
    //
    //    }
    //
    //    function compare(a, b) {
    //        if (a.group < b.group)
    //            return -1;
    //        if (a.group > b.group)
    //            return 1;
    //        return 0;
    //    }
    //
    //    sorted.sort(compare);
    //
    //    for (var j = 0; j < sorted.length; j++) {
    //
    //        nodes.push({foci: sorted[j].foci, color: sorted[j].color});
    //
    //        force.start();
    //
    //        node = node.data(nodes);
    //
    //        node.enter().append("circle")
    //            .attr("class", "node")
    //            .attr("cx", function (d) {
    //                return d.foci.x;//+ d.disturb.dx;
    //            })
    //            .attr("cy", function (d) {
    //                return d.foci.y;// + d.disturb.dy;
    //            })
    //            .attr("r", 3)
    //            .style("fill", function (d) {
    //                return d3.rgb(d.color);
    //            })
    //            .style("stroke", function (d) {
    //                return d3.rgb(d.color).darker(1);
    //            });
    //        node.exit().remove();
    //    }
    //    $('#exploreMessage').hide();
    //});
}]);

