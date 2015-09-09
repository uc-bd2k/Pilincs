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
    //$("input[name='inlineradio']").change(exploreColorChanged);
});

jQuery(document).ready(function () {
    //$("input[name='inlineradio']").change(exploreFilterChanged);
});

function exploreColorChanged() {
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

