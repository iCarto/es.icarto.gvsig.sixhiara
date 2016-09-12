document.myChart = function myChart() {
  var ctx = document.getElementById("myChart");
    var options = {
      resposive: true,
	};
	
  var data = {
    labels: chartLabels,
    datasets: datasets
  };

  var myChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: options
  });
}
