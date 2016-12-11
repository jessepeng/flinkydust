function refreshScatterplot() {
            $('#chart').empty();
            $('#timeline').empty();
            $('#chart').append('<div class="loading" style="display:block; position:absolute; left:44%;top:48%;"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata" style="display:none; position:absolute; left:44%;top:48%;">Sorry, no results were found for your request.</div>');
            $('#timeline').append('<div class="loading" style="display:block; position:absolute; left:44%;top:48%;"><i class="fa fa-refresh fa-spin fa-5x"></i></div><div class="nodata" style="display:none; position:absolute; left:44%;top:48%;">Sorry, no results were found for your request.</div>');
            $('#errors').css('display','none');

            // Retrieve Option values
            var x = $('#xAxis').val();
            var y = $('#yAxis').val();
            var filters = [];
            $('#filter-container .filter').each(function(){
                var dim = $(this).find('.dimension').val();
                var fil = $(this).find('.filterVal').val();
                var com =  $(this).find('.comparator').val();
                if(dim=='date'){
                    selDate = $(this).find('.filterDate').datepicker('getDate');
                    if(selDate == null){
                        $('#errors').text('Filter not completely set!').css({'display':'block','color':'red'});
                        return true;
                    }
                    mon = selDate.getMonth();
                    month = ( mon < 10)? '0' + mon : mon;
                    fil = selDate.getFullYear() + '-' + month + '-' + selDate.getDate() + '%2000:00:00';
                }

                if(dim!='' && fil!='' && com!=''){
                    filters.push(
                            {
                                'dimension': dim,
                                'comparator': com,
                                'filterVal': fil,
                            });
                }else{
                    $('#errors').text('Filter not completely set!').css({'display':'block','color':'red'});
                }

            });

            // Create REST URL
            var restlink = '/rest/projection/' + x + '/' + y + '/date/';

            if(filters.length > 0){
                restlink += 'filter/';
                $.each(filters, function(key, val){
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

            var timelinePoints={
                x: [],
                y: [],
                mode: 'markers',
                type: 'scattergl',
                text: [],
                marker: {symbol:'square',size:10, color:'blue'}
            };

            var dataPoints = {
              x: [],
              y: [],
              mode: 'markers',
              type: 'scattergl',
              text: [],
              marker: { size: 3,color:'blue'}
            };

            $.getJSON(restlink, function(data) {

                $.each(data.data, function(key, value){
                    var date = value['date'].slice(0,10);
                    if(timelinePoints.x.indexOf(date) == -1){
                        timelinePoints.x.push(date);
                        timelinePoints.y.push(1);
                        timelinePoints.text.push('Date:' + date);
                    }
                    dataPoints.x.push((x=='date')? date : value[x]);
                    dataPoints.y.push((y=='date')? date : value[y]);
                    //dataPoints.text.push(x + ': ' + value[x] + ', ' + y + ': ' + value[y] + ',  date: ' + value['date']);
                    dataPoints.text.push('Date: ' + value['date']);
                });

                if(dataPoints.x.length == 0){
                    $('.loading').css('display','none');
                    $('.nodata').css('display','block');
                    return;
                }

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

                $('.loading').css('display','none');

                //Add history
                var historyDiv = document.createElement("div");
                historyDiv.style.width = "97%";
                historyDiv.style.height = "400px";
                historyDiv.className = "row";

                var divHistoryChart = document.createElement("div");
                divHistoryChart.style.width = "47%";
                divHistoryChart.style.height = "400px";
                divHistoryChart.className = "col-md-12";

                Plotly.newPlot(divHistoryChart, [dataPoints], layout,
                    {
                        displaylogo: false,
                        displayModeBar: true
                    }
                );

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

                Plotly.newPlot(divHistoryTimeline, [timelinePoints], timeLineLayout);

                historyDiv.appendChild(divHistoryTimeline);
                document.getElementById("history").appendChild(historyDiv);
            });

}

var filterCnt = 0;
function addFilter(){
    filterCnt += 1;
    $('#filter-container').append(
        '<div class="col-md-12 filter" style="margin-top:5px;padding:0;">' +
            '<select class="dimension" name="filter' + filterCnt + '" style="width:32%;margin-right:1%;" id="filter' + filterCnt + '">' +
                 '<option value="" disabled selected>Dimension</option>' +
                 '<option value="date">Date</option>' +
                 '<option value="small">Small</option>' +
                 '<option value="large">Large</option>' +
                 '<option value="relHumid">Rel. Humidity</option>' +
                 '<option value="temp">Temperature</option>' +
            '</select>' +
            '<select class="comparator" name="comparator' + filterCnt + '" style="width:32%;margin-right:1%;" id="comparator' + filterCnt + '">' +
                 '<option value="" disabled selected>Comparator</option>' +
                 '<option value="atLeast">greater</option>' +
                 '<option value="same">equal</option>' +
                 '<option value="lessThan">less</option>' +
            '</select>' +
            '<input class="filterVal" type="text" style="width:33%;display:inline;" placeholder="Insert value"></input>' +
            '<input class="filterDate" type="text" style="width:33%;display:none;" placeholder="Insert value"></input>' +
        "</div>");
    $('.dimension').on('change', function() {
            var inputText = $(this).parent().find('.filterVal');
            var inputDate = $(this).parent().find('.filterDate');

            if($(this).val() == 'date'){
                inputDate.datepicker({
                    dateFormat:'yy-mm-dd',
                    minDate: '2014-01-01',
                    maxDate: '2015-01-01',
                    defaultDate: '2014-01-01'
                });

                inputDate.css('display','inline');
                inputText.css('display','none');
            }else{
                inputDate.css('display','none');
                inputText.css('display','inline');
            }
    });
}

$(document).ready(function(){
    $.ajax("/rest/data/loadTest");
})

