var restData;
var x;
var y;

function turnOnFinishListener() {
    var graphDiv = document.getElementById('chart');
    var timelineDiv = document.getElementById('timeline');
    graphDiv.on('plotly_finished', function (data) {
        redrawing.datapoints = false;
    });
    timelineDiv.on('plotly_finished', function (data) {
        redrawing.timeline = false;
    });
    $('.loading').hide();
}

function turnOffFinishListener() {
    var graphDiv = $('#chart');
    var timelineDiv = $('#timeline');
    graphDiv.off('plotly_finished');
    timelineDiv.off('plotly_finished');
}

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
        var dim = $(this).children('.dimension').val();
        var fil = $(this).children('.filterVal').val();
        var com = $(this).children('.comparator').val();
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

        var or = '';
        $(this).children('.subfilter').each(function(){
            var dim1 = $(this).children('.dimension').val();
            var fil1 = $(this).children('.filterVal').val();
            var com1 = $(this).children('.comparator').val();
            if (dim1 == 'date') {
                var selDate = $(this).find('.filterDate').datepicker('getDate');
                if (selDate == null) {
                    $('.errors').text('Filter not completely set!');
                    filterError = true;
                    return;
                }
                fil1 = selDate.getFullYear() + "-" +  ("0" + (selDate.getMonth() + 1)).slice(-2) +"-" + ("0" + selDate.getDate()).slice(-2)
                     + " " + ("0" + selDate.getHours()).slice(-2) + ":" + ("0" + selDate.getMinutes()).slice(-2) + ":" + ("0" + selDate.getSeconds()).slice(-2);
            }

            if (dim1 != '' && fil1 != '' && com1 != '') {
                or += "or/" + dim1 + '/' + com1 + '/' + fil1 + '/';
            }
        });
        if (dim != '' && fil != '' && com != '') {
            filters.push(
                {
                    'dimension': dim,
                    'comparator': com,
                    'filterVal': fil,
                    'subFilters' : or
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
            restlink += val.dimension + '/' + val.comparator + '/' + val.filterVal + '/' + val.subFilters;
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

            createPlots(null, null, null, null, false, function(data) {
                var plots = data;

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
                var layout = {
                    xaxis: {
                        title: x,
                    },
                    yaxis: {
                        title: y
                    },
                    title: x + ' vs. ' + y,

                };
                redrawing.datapoints = true;
                redrawing.timeline = true;
                Plotly.newPlot(timelineDiv, [], timeLineLayout, {
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
                            click: function() {
                                toggleSelectMode('timeline');
                            }
                        }
                    ]
                }).then(function() {
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
                                    click: function () {
                                        toggleSelectMode('datapoints');
                                    }
                                }
                            ]
                        }
                    )
                }).then(function () {
                    timelineDiv.on('plotly_relayout', function (data) {
                        select(data, 'timeline');
                    });
                    graphDiv.on('plotly_relayout', function (data) {
                        select(data, 'datapoints');
                    });
                    Plotly.addTraces(graphDiv, [plots.dataPoints]);
                    redrawing.datapoints = true;
                }).then(function () {
                    Plotly.addTraces(timelineDiv, [plots.timelinePoints]);
                    redrawing.timeline = true;
                }).then(function () {
                    //Add history
                    var historyDiv = document.createElement("div");
                    historyDiv.style.width = "97%";
                    historyDiv.style.height = "400px";
                    historyDiv.className = "row";

                    var divHistoryChart = document.createElement("div");
                    divHistoryChart.style.width = "47%";
                    divHistoryChart.style.height = "400px";
                    divHistoryChart.className = "col-md-12";

                    Plotly.newPlot(divHistoryChart, [], layout,
                        {
                            displaylogo: false,
                            displayModeBar: true
                        }
                    );

                    Plotly.addTraces(divHistoryChart, [plots.dataPoints]);

                    historyDiv.appendChild(divHistoryChart);

                    var divHistoryTimeline = document.createElement("div");
                    divHistoryTimeline.style.width = "47%";
                    divHistoryTimeline.style.height = "400px";
                    divHistoryTimeline.className = "col-md-12";

                    var timeLineLayout = {
                        yaxis: {range: [0,2],

                            showgrid: false,
                            zeroline: false,
                            showline: false,
                            autotick: true,
                            ticks: '',
                            showticklabels: false,
                            fixedrange: true
                        },
                        xaxis: {type:'date', title:'Data Availability'}
                    };

                    Plotly.newPlot(divHistoryTimeline, [], timeLineLayout);

                    Plotly.addTraces(divHistoryTimeline, [plots.timelinePoints]);

                    historyDiv.appendChild(divHistoryTimeline);
                    document.getElementById("history").appendChild(historyDiv);
                }).then(function() {
                    loading.hide();
                    turnOnFinishListener();
                });
            });
        }
    });

}

function createPlots(lowerX, upperX, lowerY, upperY, compareDates, callback) {
    var minX, minY, maxX, maxY;
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

        if (!compareDates) {
            $.getJSON(createAggregationUrl(x, 'min'), function(data) {
                minX = data.data[0][x];
                $.getJSON(createAggregationUrl(x, 'max'), function(data) {
                    maxX = data.data[0][x];
                    $.getJSON(createAggregationUrl(y, 'min'), function(data) {
                        minY = data.data[0][y];
                        $.getJSON(createAggregationUrl(y, 'max'), function(data) {
                            maxY = data.data[0][y];
                            callback(preparePlots());
                        })
                    })
                })
            });

            return;
        }
    }

    function createAggregationUrl(field, op) {
        return '/rest/aggregation/' + op + '/' + field + '/filter/' + x + '/atLeast/' + lowerX + '/' + x + '/lessThan/' + upperX + '/' + y + '/atLeast/' + lowerY + '/' + y + '/lessThan/' + upperY;
    }

    function preparePlots() {
        $.each(restData, function (key, value) {
            var date = value['date'].slice(0, 10);
            if (selection) {
                var xValue = value[x];
                var yValue = value[y];
                if (compareDates) {
                    var dateSplit = date.split('-');
                    var dateObject = new Date(dateSplit[0], dateSplit[1] - 1, dateSplit[2]);
                    var lowerXSplit = lowerX.slice(0, 10).split('-');
                    var compareDateLower = new Date(lowerXSplit[0], lowerXSplit[1] - 1, lowerXSplit[2]);
                    var upperXSplit = upperX.slice(0, 10).split('-');
                    var compareDateUpper = new Date(upperXSplit[0], upperXSplit[1] - 1, upperXSplit[2]);
                    if (dateObject > compareDateLower && dateObject < compareDateUpper) {
                        dataPointsSelected.x.push((x == 'date') ? date : xValue);
                        dataPointsSelected.y.push((y == 'date') ? date : yValue);
                        dataPointsSelected.text.push('Date: ' + value['date']);
                        if (timelinePointsSelected.x.indexOf(date) == -1) {
                            timelinePointsSelected.x.push(date);
                            timelinePointsSelected.y.push(1);
                            timelinePointsSelected.text.push('Date:' + date);
                        }
                        return;
                    }
                } else {
                    if (xValue > lowerX && xValue < upperX && yValue > lowerY && yValue < upperY) {
                        dataPointsSelected.x.push((x == 'date') ? date : xValue);
                        dataPointsSelected.y.push((y == 'date') ? date : yValue);
                        dataPointsSelected.text.push('Date: ' + value['date']);
                        if (timelinePointsSelected.x.indexOf(date) == -1) {
                            timelinePointsSelected.x.push(date);
                            timelinePointsSelected.y.push(1);
                            timelinePointsSelected.text.push('Date:' + date);
                        }
                        return;
                    }
                }
            }
            dataPoints.x.push((x == 'date') ? date : value[x]);
            dataPoints.y.push((y == 'date') ? date : value[y]);
            dataPoints.text.push('Date: ' + value['date']);
            if (timelinePoints.x.indexOf(date) == -1) {
                timelinePoints.x.push(date);
                timelinePoints.y.push(1);
                timelinePoints.text.push('Date:' + date);
            }
        });
        if (selection) {
            $.each(dataPointsSelected.text, function(key, value) {
                var tooltipText = value;
                tooltipText += ', Points Selected: ' + dataPointsSelected.x.length;
                if (typeof maxX !== 'undefined' && typeof maxY !== 'undefined' && typeof minX !== 'undefined' && typeof minY !== 'undefined') {
                    tooltipText += ', Min ' + x + ': ' + minX + ', Max ' + x + ': ' + maxX + ', Min ' + y + ': ' + minY + ', Max ' + y + ': ' + maxY;
                }
                dataPointsSelected.text[key] = tooltipText;
            });
        }
        return {
            dataPoints: dataPoints,
            dataPointsSelected: dataPointsSelected,
            timelinePoints: timelinePoints,
            timelinePointsSelected: timelinePointsSelected
        };
    }

    callback(preparePlots());
}

function select(data, chart) {
    function addTraces() {
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
        redrawing.timeline = true;
        Plotly.addTraces(timelineDiv, timelineTraces).then(function() {
            redrawing.datapoints = true;
            Plotly.addTraces(graphDiv, dataPointTraces);
        }).then(function() {
            var update = {
                'xaxis.autorange': true,
                'yaxis.autorange': true
            };
            redrawing.datapoints = true;
            Plotly.relayout(graphDiv, update);
        }).then(function() {
            var update = {
                'xaxis.autorange': true,
                'yaxis.autorange': true
            };
            redrawing.timeline = true;
            Plotly.relayout(timelineDiv, update).then(turnOnFinishListener);
        });
    }

    var createPlotsCallback = function(data) {
        plots = data;
        redrawing.timeline = true;
        redrawing.datapoints = true;
        turnOffFinishListener();
        try {
            Plotly.deleteTraces(graphDiv, [0, 1]).then(function () {
                Plotly.deleteTraces(timelineDiv, [0, 1]);
            }).then(addTraces);
        } catch (err) {
            Plotly.deleteTraces(graphDiv, 0).then(function () {
                Plotly.deleteTraces(timelineDiv, 0);
            }).then(addTraces);
        }
    };

    if (selectMode[chart] && !redrawing[chart]) {
        $('.loading').show();
        var graphDiv = document.getElementById('chart');
        var timelineDiv = document.getElementById('timeline');
        var autosize = (typeof data["xaxis.autorange"] !== 'undefined') || (typeof data["yaxis.autorange"] !== 'undefined');
        var plots;
        if (autosize) {
            createPlots(null, null, null, null, (chart === "timeline"), createPlotsCallback);
        } else {
            createPlots(data.xaxis[0], data.xaxis[1], data.yaxis[0], data.yaxis[1], (chart === "timeline"), createPlotsCallback);
        }

    }
}

var selectMode = {timeline: true, datapoints: true};
var redrawing = {timeline: false, datapoints: false};
function toggleSelectMode(chart) {
    selectMode[chart] = !selectMode[chart];
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
            '<span style="width:49%;margin-right:1%;">' +
                '<span class="filter-delete" onclick="removeFilter(' + filterCnt + ')">x</span>' +
                '<span class="filter-button" onclick="addOrToFilter(' + filterCnt + ')">Add "or"</span>' +
            '</span>' +
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

function addOrToFilter(id) {
    filterCnt += 1;
    $("#filter-" + id).append(
    '<div id="filter-' + filterCnt + '" class="subfilter">' +
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
        '<span style="width:49%;margin-right:1%;text-align:right;">' +
            '<span class="filter-delete" onclick="removeFilter(' + filterCnt + ')">x</span>' +
        '</span>' +
    '</div>'
     );

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

$(document).ready(function () {
    $.ajax("/rest/data/loadTest");
});
