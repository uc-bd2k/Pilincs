/**
 * Created by chojnasm on 10/7/15.
 */
loadExplorerTab = function ($scope, $http) {

    var scope = angular.element('#assayButtons').scope();
    var queryContent = [];
    queryContent.push({"p100": scope.p100}); // scope.p100
    queryContent.push({"gcp": scope.gcp});

    queryContent = queryContent.concat($scope.tags);

    $('#exploreMessage').show();

    var res = $http.post('/pilincs/api-explore', JSON.stringify(queryContent));
    res.success(function (data, status, headers, config) {

        var assayNames = data.assayNames,
            cellNames = data.cellNames,
            pertNames = data.pertNames,
            doseNames = data.doseNames,
            timeNames = data.timeNames,
            rows = data.rows;


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
