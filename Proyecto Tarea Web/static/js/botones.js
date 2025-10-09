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