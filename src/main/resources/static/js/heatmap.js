/**
 * Created by chojnasm on 10/7/15.
 */
loadHeatMap = function ($scope, $http) {

    var queryContent = [];
    queryContent.push({"p100": true});
    queryContent.push({"gcp": false});
    queryContent = queryContent.concat($scope.tags);

    $('#heatMapMessageNew').show();

    // Color scale
    var scaleData = d3.range(1, 20, 1);
    var grid = 10;

    var scale = d3.select('#heatMapNew').select('svg').select('g#colorScale').attr("transform", 'translate(0,0)');
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


        var topMargin = 80,
            leftMargin = 90,
            shortenLabel = 15,
            grid = 10;

        var peptideNameOrders = data.peptideNames,
            profileNames = data.profileNames,
            rows = data.rows;

        d3.select('#heatMapNew').select('svg').attr('height', 50 + topMargin + rows.length * 10);

        // Shorten profile names
        profileNames = profileNames.map(function (item) {
            if (item.length > shortenLabel) {
                return item.substring(0, shortenLabel) + '';
            } else {
                return item;
            }
        });

        // Row Labels
        var rowLabels = d3.select('#heatMapNew').select('svg').select('g#rowLabels').attr("transform", 'translate(0,' + (topMargin + 10) + ')');

        rowLabels = rowLabels.selectAll('text').data(peptideNameOrders);
        rowLabels.enter()
            .append('text')
            .attr('x', 0)
            .attr('y', 30)
            .attr("text-anchor", "start")
            .attr('transform', function (d, i) {
                return "translate(0," + i * grid + ") rotate(0)";
            })
            .text(function (d) {
                return d.peptideName;
            })
            .style('font-size', '8px');

        rowLabels.exit().remove();

        // Column Labels
        var colLabels = d3.select('#heatMapNew').select('svg').select('g#colLabels').attr("transform", 'translate(97,100)');

        colLabels = colLabels.selectAll('text').data(profileNames);
        colLabels.enter()
            .append('text')
            .attr('x', 0)
            .attr('y', 0)
            .attr("text-anchor", "start")
            .attr('transform', function (d, i) {
                return "translate(" + i * grid + ",0) rotate(270)";
            })
            .text(function (d) {
                return d;
            })
            .style('font-size', '8px');

        colLabels.exit().remove();

        // Matrix
        var matrixRect = d3.select('#heatMapNew').select('svg').select('g#matrix')
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
        $('#heatMapMessageNew').hide();

    });

};
