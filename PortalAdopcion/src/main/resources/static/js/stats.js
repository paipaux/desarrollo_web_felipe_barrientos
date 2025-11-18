document.addEventListener('DOMContentLoaded', function () {
            
    fetch('/api/stats/avisos-por-dia')
        .then(response => response.json())
        .then(data => {
            Highcharts.chart('container-lineas', {
                chart: {
                    type: 'line'
                },
                title: {
                    text: 'Avisos de Adopción por Día'
                },
                xAxis: {
                    categories: data.categorias,
                    title: {
                        text: 'Día'
                    }
                },
                yAxis: {
                    title: {
                        text: 'Cantidad de Avisos'
                    }
                },
                series: [{
                    name: 'Avisos',
                    data: data.valores
                }]
            });
        })
        .catch(error => console.error('Error al cargar gráfico de líneas:', error));

    fetch('/api/stats/avisos-por-tipo')
        .then(response => response.json())
        .then(data => {
            Highcharts.chart('container-torta', {
                chart: { type: 'pie' },
                title: { text: 'Total de Avisos por Tipo de Mascota' },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                series: [{
                    name: 'Mascotas',
                    data: data
                }]
            });
        })
        .catch(error => console.error('Error al cargar gráfico de torta:', error));

    fetch('/api/stats/avisos-por-mes')
        .then(response => response.json())
        .then(data => {
            Highcharts.chart('container-barras', {
                chart: { type: 'bar' },
                title: { text: 'Avisos por Mes (Gatos vs. Perros)' },
                xAxis: {
                    categories: data.categorias,
                    title: { text: 'Mes' }
                },
                yAxis: {
                    min: 0,
                    title: { text: 'Cantidad de Avisos' }
                },
                series: data.series
            });
        })
        .catch(error => console.error('Error al cargar gráfico de barras:', error));
});