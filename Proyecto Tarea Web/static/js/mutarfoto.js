function ampliarFoto(img) {
    // Cambiar a tamaño grande
    img.classList.remove('foto-pequena');
    img.classList.add('foto-grande');
    
    // Crear botón cerrar
    const btnCerrar = document.createElement('button');
    btnCerrar.textContent = 'Cerrar';
    btnCerrar.className = 'btn-cerrar';
    btnCerrar.onclick = function() {
        // Volver a tamaño original y quitar botón
        img.classList.remove('foto-grande');
        img.classList.add('foto-pequena');
        btnCerrar.remove();
    };
    
    // Agregar botón después de la imagen
    img.parentNode.insertBefore(btnCerrar, img.nextSibling);
}