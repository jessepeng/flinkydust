var restData;
var x;
var y;

function refreshScatterplot() {
    var chart = $('#chart');
    chart.empty();
    var timeline = $('#timeline');
    timeline.empty();
    chart.append('<div class="loading"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata">Sorry, no results were found for your request.</div>');
    timeline.append('<div class="loading"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata">Sorry, no results were found for your request.</div>');
    $('.errors').empty();

    // Retrieve Option values
    x = $('#xAxis').val();
    y = $('#yAxis').val();

    var filters = [];
    var filterError = false;
    $('#filter-container .filter').each(function () {
        var dim = $(this).find('.dimension').val();
        var fil = $(this).find('.filterVal').val();
        var com = $(this).find('.comparator').val();
        if (dim == 'date') {
            var selDate = $(this).find('.filterDate').datepicker('getDate');
            if (selDate == null) {
                $('.errors').text('Filter not completely set!');
                filterError = true;
                return;
            }
            fil = selDate.getFullYear() + "-" +  ("0" + (selDate.getMonth() + 1)).slice(-2) +"-" + ("0" + selDate.getDate()).slice(-2)
                 + " " + ("0" + selDate.getHours()).slice(-2) + ":" + ("0" + selDate.getMinutes()).slice(-2) + ":" + ("0" + selDate.getSeconds()).slice(-2);
        }

        if (dim != '' && fil != '' && com != '') {
            filters.push(
                {
                    'dimension': dim,
                    'comparator': com,
                    'filterVal': fil,
                });
        } else {
            $('.errors').text('Filter not completely set!');
            filterError = true;
            return;
        }

    });

    var loading = $('.loading');

    if (filterError) {
        loading.hide();
        return false;
    }

    // Create REST URL
    var restlink = '/rest/projection/' + x + '/' + y + '/date/';

    if (filters.length > 0) {
        restlink += 'filter/';
        $.each(filters, function (key, val) {
            restlink += val.dimension + '/' + val.comparator + '/' + val.filterVal + '/';
        });
    }

    // Create Charts
    var graphDiv = document.getElementById('chart');
    var timelineDiv = document.getElementById('timeline');

    $.getJSON(restlink, function (data) {
        if (data.status !== "ok") {
            $(".errors").text(data.message);
            loading.hide();
        } else {
            restData = data.data;

            if (restData.length == 0) {
                loading.hide();
                $('.nodata').show();
                return;
            }

            var timeLineLayout = {
                yaxis: {
                    range: [0, 2],

                    showgrid: false,
                    zeroline: false,
                    showline: false,
                    autotick: true,
                    ticks: '',
                    showticklabels: false,
                    fixedrange: true
                },
                xaxis: {type: 'date', title: 'Data Availability'}
            };

            Plotly.newPlot(timelineDiv, [], timeLineLayout);

            var layout = {
                xaxis: {
                    title: x,
                },
                yaxis: {
                    title: y
                },
                title: x + ' vs. ' + y,

            };

            redrawing = true;
            var plots = createPlots(null, null, null, null);
            Plotly.newPlot(graphDiv, [], layout,
                {
                    displaylogo: false,
                    displayModeBar: true,
                    modeBarButtonsToAdd: [
                        {
                            name: 'select',
                            title: 'Box Select',
                            attr: 'dragmode',
                            val: 'select',
                            icon: Plotly.Icons.selectbox,
                            toggle: true,
                            click: toggleSelectMode
                        }
                    ]
                }
            );

            Plotly.addTraces(graphDiv, [plots.dataPoints]);
            Plotly.addTraces(timelineDiv, [plots.timelinePoints]);
            graphDiv.on('plotly_relayout', select);

            loading.hide();
        }
    });

}

function createPlots(lowerX, upperX, lowerY, upperY) {
    var selection = (lowerX != null && upperX != null && lowerY != null && upperY != null);
    var dataPoints = {
        x: [],
        y: [],
        mode: 'markers',
        type: 'scattergl',
        text: [],
        marker: {size: 3, color: selection ? 'grey' : 'blue'},
        name: selection ? 'Not selected' : 'All'
    };
    var timelinePoints = {
        x: [],
        y: [],
        mode: 'markers',
        type: 'scattergl',
        text: [],
        marker: {symbol: 'square', size: 10, color: selection ? 'grey' : 'blue'},
        name: selection ? 'Not selected' : 'All'
    };
    if (selection) {
        var dataPointsSelected = {
            x: [],
            y: [],
            mode: 'markers',
            type: 'scattergl',
            text: [],
            marker: {size: 3, color: 'blue'},
            name: 'Selected'
        };
        var timelinePointsSelected = {
            x: [],
            y: [],
            mode: 'markers',
            type: 'scattergl',
            text: [],
            marker: {symbol: 'square', size: 10, color: 'blue'},
            name: 'Selected'
        };
    }
    $.each(restData, function (key, value) {
        var date = value['date'].slice(0, 10);
        if (selection && value[x] >= lowerX && value[x] <= upperX && value[y] >= lowerY && value[y] <= upperY) {
            dataPointsSelected.x.push((x == 'date') ? date : value[x]);
            dataPointsSelected.y.push((y == 'date') ? date : value[y]);
            dataPointsSelected.text.push('Date: ' + value['date']);
            if (timelinePointsSelected.x.indexOf(date) == -1) {
                timelinePointsSelected.x.push(date);
                timelinePointsSelected.y.push(1);
                timelinePointsSelected.text.push('Date:' + date);
            }
        } else {
            dataPoints.x.push((x == 'date') ? date : value[x]);
            dataPoints.y.push((y == 'date') ? date : value[y]);
            dataPoints.text.push('Date: ' + value['date']);
            if (timelinePoints.x.indexOf(date) == -1) {
                timelinePoints.x.push(date);
                timelinePoints.y.push(1);
                timelinePoints.text.push('Date:' + date);
            }
        }
    });
    return {
        dataPoints: dataPoints,
        dataPointsSelected: dataPointsSelected,
        timelinePoints: timelinePoints,
        timelinePointsSelected: timelinePointsSelected
    };

}

function select(data) {
    function addTraces() {
        redrawing = true;
        var dataPointTraces = [];
        dataPointTraces.push(plots.dataPoints);
        if (!autosize) {
            dataPointTraces.push(plots.dataPointsSelected);
        }
        var timelineTraces = [];
        timelineTraces.push(plots.timelinePoints);
        if (!autosize) {
            timelineTraces.push(plots.timelinePointsSelected);
        }
        Plotly.addTraces(timelineDiv, timelineTraces).then(function() {
            Plotly.addTraces(graphDiv, dataPointTraces).then(function() {
                var update = {
                    'xaxis.autorange': true,
                    'yaxis.autorange': true
                };
                redrawing = true;
                Plotly.relayout(graphDiv, update);
                Plotly.relayout(timelineDiv, update);
            });
        });
    }

    if (selectMode && !redrawing) {
        var graphDiv = document.getElementById('chart');
        var timelineDiv = document.getElementById('timeline');
        var autosize = (typeof data["xaxis.autorange"] !== 'undefined') && (typeof data["yaxis.autorange"] !== 'undefined');
        var plots;
        if (autosize) {
            plots = createPlots(null, null, null, null);
        } else {
            plots = createPlots(data.xaxis[0], data.xaxis[1], data.yaxis[0], data.yaxis[1]);
        }
        redrawing = true;
        try {
            Plotly.deleteTraces(graphDiv, [0, 1]).then(function() {
                Plotly.deleteTraces(timelineDiv, [0, 1]).then(addTraces);
            });
        } catch (err) {
            Plotly.deleteTraces(graphDiv, 0).then(function() {
                Plotly.deleteTraces(timelineDiv, 0).then(addTraces);
            });
        }
    } else if (redrawing) {
        redrawing = false;
    }
}

var selectMode = true;
var redrawing = false;
function toggleSelectMode() {
    selectMode = !selectMode;
}

var filterCnt = 0;
function addFilter() {
    filterCnt += 1;
    $('#filter-container').append(
        '<div class="col-md-12 filter" id="filter-' + filterCnt + '">' +
        '<select class="dimension" name="filter' + filterCnt + '"  id="filter' + filterCnt + '">' +
        '<option value="" disabled selected>Dimension</option>' +
        '<option value="date">Date</option>' +
        '<option value="small">Small</option>' +
        '<option value="large">Large</option>' +
        '<option value="relHumid">Rel. Humidity</option>' +
        '<option value="temp">Temperature</option>' +
        '</select>' +
        '<select class="comparator" name="comparator' + filterCnt + '" id="comparator' + filterCnt + '">' +
        '<option value="" disabled selected>Comparator</option>' +
        '<option value="atLeast">greater</option>' +
        '<option value="same">equal</option>' +
        '<option value="lessThan">less</option>' +
        '</select>' +
        '<input class="filterVal" type="text" placeholder="Insert value">' +
        '<input class="filterDate" type="text" placeholder="Insert value">' +
        '<span class="filter-button" onclick="removeFilter(' + filterCnt + ')">-</span>' +
        '</div>');
    $('.dimension').on('change', function () {
        var inputText = $(this).parent().find('.filterVal');
        var inputDate = $(this).parent().find('.filterDate');

        if ($(this).val() == 'date') {
            inputDate.datepicker({
                dateFormat: 'yy-mm-dd',
                minDate: '2014-01-01',
                maxDate: '2015-01-01',
                defaultDate: '2014-01-01'
            });

            inputDate.css('display', 'inline');
            inputText.css('display', 'none');
        } else {
            inputDate.css('display', 'none');
            inputText.css('display', 'inline');
        }
    });
}

function removeFilter(id) {
    $("#filter-" + id).remove();
}

$(document).ready(function () {
    $.ajax("/rest/data/loadTest");
});