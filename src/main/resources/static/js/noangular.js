// HeatMap view


jQuery(document).ready(function () {
    $("input[name='inlineradio']").change(radioValueChanged);
});

function radioValueChanged() {
    var radioValue = $(this).val();
    var grid = 10;

    //alert(radioValue);

    if ($(this).is(":checked") && radioValue == "0") {
        var rowLabels = d3.select('#heatMap').select('svg').select('g#rowLabels').selectAll('text');
        rowLabels.transition().attr('transform', function (d, i) {
            return "translate(0," + i * grid + ")";
        });


        var matrixRect = d3.select('#heatMap').select('svg').select('g#matrix').selectAll('rect');
        matrixRect.attr('transform', function (d) {
            return "translate(" + d.columnId * grid + "," + d.rowIndex * grid + ")";
        });
    }
    else {
        var rowLabels = d3.select('#heatMap').select('svg').select('g#rowLabels').selectAll('text');
        rowLabels.attr('transform', function (d) {
            return "translate(0," + d.clusterOrder * grid + ")";
        });

        var matrixRect = d3.select('#heatMap').select('svg').select('g#matrix').selectAll('rect');
        matrixRect.attr('transform', function (d) {
            return "translate(" + d.columnId * grid + "," + d.clusterOrder * grid + ")";
        });
    }
}

// Explore view

jQuery(document).ready(function () {
    //$("input[name='inlineradio']").change(radioValueChanged);
});

function radioValueChanged() {
    var radioValue = $(this).val();
    var grid = 10;

    //alert(radioValue);

    if ($(this).is(":checked") && radioValue == "0") {
        var rowLabels = d3.select('#heatMap').select('svg').select('g#rowLabels').selectAll('text');
        rowLabels.transition().attr('transform', function (d, i) {
            return "translate(0," + i * grid + ")";
        });


        var matrixRect = d3.select('#heatMap').select('svg').select('g#matrix').selectAll('rect');
        matrixRect.attr('transform', function (d) {
            return "translate(" + d.columnId * grid + "," + d.rowIndex * grid + ")";
        });
    }
    else {
        var rowLabels = d3.select('#heatMap').select('svg').select('g#rowLabels').selectAll('text');
        rowLabels.attr('transform', function (d) {
            return "translate(0," + d.clusterOrder * grid + ")";
        });

        var matrixRect = d3.select('#heatMap').select('svg').select('g#matrix').selectAll('rect');
        matrixRect.attr('transform', function (d) {
            return "translate(" + d.columnId * grid + "," + d.clusterOrder * grid + ")";
        });
    }
}

function test() {
    var width = 800,
        height = 500;

    var fill = d3.scale.category20();

    var spaceX = width / 6;
    var d = 6;
    var nodes = [],
        foci = [{x: spaceX - d, y: 50}, {x: 2 * spaceX - d, y: 50}, {x: 3 * spaceX - d, y: 50}, {
            x: 4 * spaceX - d,
            y: 50
        }, {x: 5 * spaceX - d, y: 50},
            {x: spaceX + d, y: 50}, {x: 2 * spaceX + d, y: 50}, {x: 3 * spaceX + d, y: 50}, {
                x: 4 * spaceX + d,
                y: 50
            }, {x: 5 * spaceX + d, y: 50}];

    var svg = d3.select("#explorer")
        .attr("width", width)
        .attr("height", height);

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

    var node = svg.selectAll("circle");

    function tick(e) {
        var k = .15 * e.alpha;

        // Push nodes toward their designated focus.
        nodes.forEach(function (o, i) {
            o.y += (foci[o.id].y - o.y) * k;
            o.x += (foci[o.id].x - o.x) * k;
        });

        node
            .attr("cx", function (d) {
                return d.x;
            })
            .attr("cy", function (d) {
                return d.y;
            });
    }

    for (var i = 0; i < 500; i++) {

        nodes.push({id: ~~(Math.random() * foci.length)});

        force.start();

        node = node.data(nodes);

        node.enter().append("circle")
            .attr("class", "node")
            .attr("cx", function (d) {
                return foci[d.id].x;
            })
            .attr("cy", function (d) {
                return foci[d.id].y;
            })
            .attr("r", 3)
            .style("fill", function (d) {

                if (Math.random() > -1) {
                    return fill(d.id);
                } else {
                    return fill(2 * d.id);
                }
            })
            .style("stroke", function (d) {
                return d3.rgb(fill(d.id)).darker(1);
            });
        //.call(force.drag);
    }

}