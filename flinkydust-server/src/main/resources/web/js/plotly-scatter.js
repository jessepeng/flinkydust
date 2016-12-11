function refreshScatterplot() {
    var chart = $('#chart');
    chart.empty();
    var timeline = $('#timeline');
    timeline.empty();
    chart.append('<div class="loading"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata">Sorry, no results were found for your request.</div>');
    timeline.append('<div class="loading"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata">Sorry, no results were found for your request.</div>');
    $('.errors').empty();

    // Retrieve Option values
    var x = $('#xAxis').val();
    var y = $('#yAxis').val();
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


    /* var selection = false;
     function selectionToggle(gr, ev){
     console.debug(gr,ev);

     selection = !selection;

     if(selection){
     var chart = $("#chart");
     $('.main-svg').css('pointer-events','none');
     chart.on('click mousedown', function(ev,gr) {
     ev.cancelBubble = true;
     ev.originalEvent.cancelBubble = true;
     ev.preventDefault();
     ev.stopPropagation();
     ev.stopImmediatePropagation();

     $.each(dataPoints, function(key,value){
     console.debug(key,value);

     });

     console.debug(ev,gr);
     })
     }else{
     chart.off('.flinkydust');
     }
     }*/


    // Create Charts
    var graphDiv = document.getElementById('chart');
    var timelineDiv = document.getElementById('timeline');

    var timelinePoints = {
        x: [],
        y: [],
        mode: 'markers',
        type: 'scattergl',
        text: [],
        marker: {symbol: 'square', size: 10, color: 'blue'}
    };

    var dataPoints = {
        x: [],
        y: [],
        mode: 'markers',
        type: 'scattergl',
        text: [],
        marker: {size: 3, color: 'blue'}
    };

    $.getJSON(restlink, function (data) {
        if (data.status !== "ok") {
            $(".errors").text(data.message);
            loading.hide();
        } else {

            $.each(data.data, function (key, value) {
                var date = value['date'].slice(0, 10);
                if (timelinePoints.x.indexOf(date) == -1) {
                    timelinePoints.x.push(date);
                    timelinePoints.y.push(1);
                    timelinePoints.text.push('Date:' + date);
                }
                dataPoints.x.push((x == 'date') ? date : value[x]);
                dataPoints.y.push((y == 'date') ? date : value[y]);
                //dataPoints.text.push(x + ': ' + value[x] + ', ' + y + ': ' + value[y] + ',  date: ' + value['date']);
                dataPoints.text.push('Date: ' + value['date']);
            });

            if (dataPoints.x.length == 0) {
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

            Plotly.newPlot(timelineDiv, [timelinePoints], timeLineLayout);

            var layout = {
                xaxis: {
                    title: x,
                },
                yaxis: {
                    title: y
                },
                title: x + ' vs. ' + y,

            };


            Plotly.newPlot(graphDiv, [dataPoints], layout,
                {
                    displaylogo: false,
                    displayModeBar: true
                    /* modeBarButtonsToAdd:[
                     {
                     name: 'select',
                     title: 'Box Select',
                     attr: 'dragmode',
                     val: 'select',
                     icon: Plotly.Icons.selectbox,
                     toggle: true,
                     click: handleCartesian
                     }
                     ] */
                }
            );

            /*      graphDiv.on('plotly_relayout', function(opt){
             console.debug(opt);
             if(opt.dragmode == 'select'){
             var isDragging = false;
             $('.gl-container <div></div>')
             .mousedown(function() {

             isDragging = false;
             })
             .mousemove(function() {
             isDragging = true;
             })
             .mouseup(function() {
             var wasDragging = isDragging;
             isDragging = false;
             if (!wasDragging) {
             console.debug('dragged');
             //$("#throbble").toggle();
             }
             });
             }
             else{

             }
             });*/

            loading.hide();
        }
    });

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