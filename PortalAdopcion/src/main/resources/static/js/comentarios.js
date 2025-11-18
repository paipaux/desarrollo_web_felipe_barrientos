

document.addEventListener('DOMContentLoaded', function() {
    const seccionComentarios = document.getElementById('seccion-comentarios');
    if (!seccionComentarios) return; 

    const avisoId = seccionComentarios.dataset.avisoId;
    const listaComentarios = document.getElementById('lista-comentarios');
    const formComentario = document.getElementById('form-comentario');
    const errorDiv = document.getElementById('comentario-errores');

    function formatearFecha(fechaISO) {
        const fecha = new Date(fechaISO);
        return fecha.toLocaleString('es-CL', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function renderizarComentario(comentario, alPrincipio = false) {
        const divComentario = document.createElement('div');
        divComentario.style.border = "1px solid #ccc";
        divComentario.style.padding = "10px";
        divComentario.style.marginBottom = "10px";
        
        const fechaFormateada = formatearFecha(comentario.fecha);

        divComentario.innerHTML = `
            <p><strong>${comentario.nombre}</strong> <span style="font-size: 0.9em; color: #555;">(${fechaFormateada})</span></p>
            <p>${comentario.texto}</p>
        `;
        
        if (alPrincipio) {
            listaComentarios.prepend(divComentario);
        } else {
            listaComentarios.appendChild(divComentario);
        }
    }

    async function cargarComentarios() {
        try {
            const response = await fetch(`/api/aviso/${avisoId}/comentarios`);
            if (!response.ok) {
                throw new Error('No se pudieron cargar los comentarios.');
            }
            const comentarios = await response.json();
            
            listaComentarios.innerHTML = '';
            if (comentarios.length === 0) {
                listaComentarios.innerHTML = '<p>Aún no hay comentarios. ¡Sé el primero!</p>';
            } else {
                comentarios.forEach(comentario => renderizarComentario(comentario));
            }
        } catch (error) {
            listaComentarios.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
        }
    }

    async function enviarComentario(e) {
        e.preventDefault();
        errorDiv.innerHTML = '';

        const nombre = document.getElementById('comentario-nombre').value;
        const texto = document.getElementById('comentario-texto').value;
        
        const data = {
            nombre: nombre,
            texto: texto
        };

        try {
            const response = await fetch(`/api/aviso/${avisoId}/comentario`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            const result = await response.json();

            if (response.ok && result.success) {
                if (listaComentarios.querySelector('p')) {
                    listaComentarios.innerHTML = '';
                }
                renderizarComentario(result.comentario, true);
                formComentario.reset();
            } else {
                if (result.errores) {
                    errorDiv.innerHTML = result.errores.join('<br>');
                } else {
                    errorDiv.innerHTML = 'Error desconocido al enviar el comentario.';
                }
            }
        } catch (error) {
            errorDiv.innerHTML = `Error de conexión: ${error.message}`;
        }
    }

    cargarComentarios();
    formComentario.addEventListener('submit', enviarComentario);
});