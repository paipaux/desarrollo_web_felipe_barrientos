const goformButton = document.getElementById("goform-button");
if (goformButton) {
    goformButton.addEventListener("click", () => {
        window.location.href = "/formulario"; 
    });
}

const volverButton = document.getElementById("volver-button");
if (volverButton) {
    volverButton.addEventListener("click", () => {
        window.location.href = "/"; 
    });
}

const statsButton = document.getElementById("stats-button");
if (statsButton) {
    statsButton.addEventListener("click", () => {
        window.location.href = "/estadisticas"; 
    });
}

const listButton = document.getElementById("list-button");
if (listButton) {
    listButton.addEventListener("click", () => {
        window.location.href = "/listado"; 
    });
}

const volverlistbutton = document.getElementById("volverlist-button");
if (volverlistbutton) {
    volverlistbutton.addEventListener("click", () => {
        window.location.href = "/listado"; 
    });
}

function ampliarFoto(img) {
    if (img.classList.contains('foto-grande')) return;
    img.classList.remove('foto-pequena');
    img.classList.add('foto-grande');
    
    const btnCerrar = document.createElement('button');
    btnCerrar.textContent = 'Cerrar';
    btnCerrar.className = 'btn-cerrar';
    btnCerrar.onclick = function() {
        img.classList.remove('foto-grande');
        img.classList.add('foto-pequena');
        btnCerrar.remove();
    };
    
    img.parentNode.insertBefore(btnCerrar, img.nextSibling);
}