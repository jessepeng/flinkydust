function refreshScatterplot() {
            $('#loading').css('display','block');
            $('#nodata').css('display','none');
            var x = $('#xAxis').val();
            var y = $('#yAxis').val();

            var filters = [];
            $('#filter-container .filter').each(function(){
                var dim = $(this).find('.dimension').val();
                var fil = $(this).find('.filterVal').val();


                if(dim=='date'){
                    selDate = $(this).find('.filterDate').datepicker('getDate');
                    mon = selDate.getMonth();
                    month = ( mon < 10)? '0' + mon : mon;
                    fil = selDate.getFullYear() + '-' + month + '-' + selDate.getDate() + '%2000:00:00';
                }


                filters.push(
                    {
                        'dimension': dim,
                        'comparator': $(this).find('.comparator').val(),
                        'filterVal': fil,
                    });
            });

            var restlink = '/rest/projection/' + x + '/' + y + '/date/';

            if(filters.length > 0){
                restlink += 'filter/';
                $.each(filters, function(key, val){
                            restlink += val.dimension + '/' + val.comparator + '/' + val.filterVal + '/';
                });
            }






            var graphDiv = document.getElementById('chart');
            var dataPoints = {
              x: [],
              y: [],
              mode: 'markers',
              type: 'scattergl',
              text: [],
              marker: { size: 3,color:'blue'}
            };

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

            $.getJSON(restlink, function(data) {

                $.each(data.data, function(key, value){
                    dataPoints.x.push(value[x]);
                    dataPoints.y.push(value[y]);
                    //dataPoints.text.push(x + ': ' + value[x] + ', ' + y + ': ' + value[y] + ',  date: ' + value['date']);
                    dataPoints.text.push('Date: ' + value['date']);
                });

                if(dataPoints.x.length == 0){
                    $('#loading').css('display','none');
                    $('#nodata').css('display','block');
                    return;
                }

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

                $('#loading').css('display','none');
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

