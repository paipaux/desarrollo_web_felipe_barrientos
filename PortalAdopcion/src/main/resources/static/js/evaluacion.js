function evaluar(button) {
    const avisoId = button.dataset.avisoId;
    
    let nota = prompt("Ingrese la nota (entre 1 y 7) para el aviso " + avisoId + ":");
    
    if (nota === null) {
        return;
    }

    nota = parseInt(nota.trim());

    if (isNaN(nota) || nota < 1 || nota > 7) {
        alert("Nota inválida. Debe ingresar un número entero entre 1 y 7.");
        return;
    }

    const url = `/api/notas/evaluar/${avisoId}`;
    const data = {
        nota: nota
    };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 400) {
            return response.json().then(errorData => {
                 throw new Error(errorData.error || "Error al procesar la solicitud.");
            });
        }
        throw new Error('Error al conectar con el servidor.');
    })
    .then(nuevoPromedio => {
        const celdaPromedio = document.getElementById('promedio-' + avisoId);
        
        const promedioFormateado = (nuevoPromedio !== null) ? nuevoPromedio.toFixed(1) : '-';
        celdaPromedio.textContent = promedioFormateado;
        
        alert(`Nota (${nota}) guardada. Nuevo promedio: ${promedioFormateado}`);
    })
    .catch(error => {
        alert("Error al guardar la nota: " + error.message);
        console.error('Error:', error);
    });
}