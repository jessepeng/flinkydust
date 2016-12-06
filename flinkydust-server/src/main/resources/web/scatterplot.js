




function refreshScatterplot(){

            console.debug("test");
            var w = 1000;
            var h = 1000    ;


            var x = $('#xAxis').val();
            var y = $('#yAxis').val();
            d3.json('/rest/projection/date/' + x + '/' + y, function(data){
                        //Create SVG element

            var dustData = data.data;
            console.debug(dustData);

            nv.addGraph(function() {
              var chart = nv.models.scatterChart()
                            .showDistX(true)    //showDist, when true, will display those little distribution lines on the axis.
                            .showDistY(true)
//                            .transitionDuration(350)
                            .color(d3.scale.category10().range());

              //Configure how the tooltip looks.
//              chart.tooltipContent(function(key) {
//                  return '<h3>' + key + '</h3>';
 //             });

              //Axis settings
              chart.xAxis.tickFormat(d3.format('.02f'));
              chart.yAxis.tickFormat(d3.format('.02f'));

              //We want to show shapes other than circles.
              //chart.scatter.onlyCircles(false);

              d3.select('#svg-container')
                  //.data(dustData[x],dustData[y])
                  .data(dustData)
                  .call(chart);

              nv.utils.windowResize(chart.update);

              return chart;
            });

//                        var svg = d3.select("#svg-container")
//                                    .append("svg")
//                                    .attr("width", w)
//                                    .attr("height", h);
//
//                        svg.selectAll("circle")
//                           .data(data.data)
//                           .enter()
//                           .append("circle")
//                           .attr("cx", function(d) {
//                                   return d[x];
//                           })
//                           .attr("cy", function(d) {
//                                   return d[y];
//                           })
//                           .attr("r", function(d) {
//                                   return 1;
//                           });
//
//                        svg.selectAll("text")
//                           .data(data)
//                           .enter()
//                           .append("text")
//                           .text(function(d) {
//                                   return d[x] + "," + d[y];
//                           })
//                           .attr("x", function(d) {
//                                   return d[x];
//                           })
//                           .attr("y", function(d) {
//                                   return d[y];
//                           })
//                           .attr("font-family", "sans-serif")
//                           .attr("font-size", "11px")
//                           .attr("fill", "red");
            })

}


