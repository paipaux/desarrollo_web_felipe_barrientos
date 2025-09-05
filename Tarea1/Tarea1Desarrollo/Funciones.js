// Getters
const adoptButton = document.getElementById('adoptbutton');
const listButton = document.getElementById("listbutton")
const statsButton = document.getElementById("statsbutton")
const volverButton = document.getElementById("backbutton")
const volverButton2 = document.getElementById("backbutton2")
const volverButton3 = document.getElementById("backbutton3")
const confirmationmsg = document.getElementById("confirmacion")
const finalmsg = document.getElementById("mensajeFinal")
const buttonSI = document.getElementById("buttonSi")
const buttonNO = document.getElementById("buttonNo")
const volverButton4 = document.getElementById("buttonPortada")
const sector = document.getElementById("sector");

// getter ocultables
const formulario = document.getElementById('formulario');
const tablalistado = document.getElementById("tablagrande")
const estadisticas = document.getElementById("estadisticas")
const botonesportada = document.getElementById("botonesportada")
const tablaportada = document.getElementById("tablaportada")
const bienvenidamsg = document.getElementById("bienvenidamsg")

// getters input
const emailInput = document.getElementById("email");
const nombreInput = document.getElementById("nombre")
const numInput = document.getElementById("num")
const cantidadInput = document.getElementById("cantidad")
const edadInput = document.getElementById("edad")
const fotoInput = document.getElementById("foto") 

//Getters avisos detallados
const detalle1 = document.getElementById("detalleAdopcion1")
const detalle2 = document.getElementById("detalleAdopcion2")
const detalle3 = document.getElementById("detalleAdopcion3")
const detalle4 = document.getElementById("detalleAdopcion4")
const detalle5 = document.getElementById("detalleAdopcion5")

//Getters imagenes zoom
const fotomut1 = document.getElementById("fotomut1")
const fotomut12 = document.getElementById("fotomut12")
const fotomut13 = document.getElementById("fotomut13")
const fotomut2 = document.getElementById("fotomut2")
const fotomut3 = document.getElementById("fotomut3")
const fotomut4 = document.getElementById("fotomut4")
const fotomut5 = document.getElementById("fotomut5")
const fotomut52 = document.getElementById("fotomut52")

//Botones volver del zoom
const volverzoom1 = document.getElementById("resetzoom1")
const volverzoom2 = document.getElementById("resetzoom2")
const volverzoom3 = document.getElementById("resetzoom3")
const volverzoom4 = document.getElementById("resetzoom4")
const volverzoom5 = document.getElementById("resetzoom5")

// get stats
const stat1 = document.getElementById("stat1")

const redes = document.getElementById("red")
const redesinput = document.getElementById("inputred")

const fecha = document.getElementById("fechaentrega")
const fechaactual = new Date()
fechaactual.setHours(fechaactual.getHours()-1)
const FechaInput = fechaactual.toISOString().slice(0,16)
fecha.value = FechaInput

//Función Ocultar
const mostrar = (id) => { id.classList.remove("ocultable") }
const ocultar = (id) => { id.classList.add("ocultable") }

//Función Transformar
const agrandar = (id) => { 
    id.classList.remove("fotobase")
    id.classList.add("fotozoom")
}

//Función volver del zoom
const volver = (id) => {
    id.classList.remove("fotozoom")
    id.classList.add("fotobase")
}

document.addEventListener('DOMContentLoaded', () => mostrar(bienvenidamsg))
document.addEventListener('DOMContentLoaded', () =>mostrar(botonesportada))
document.addEventListener('DOMContentLoaded', () =>mostrar(tablaportada))

adoptButton.addEventListener("click",() => {mostrar(formulario);ocultar(tablaportada);ocultar(botonesportada); ocultar(bienvenidamsg)})
listButton.addEventListener("click",() => {mostrar(tablalistado);ocultar(tablaportada);ocultar(botonesportada); ocultar(bienvenidamsg)})
statsButton.addEventListener("click",() => {mostrar(estadisticas);ocultar(tablaportada);ocultar(botonesportada); ocultar(bienvenidamsg)})
volverButton.addEventListener("click",() => {mostrar(tablaportada);mostrar(botonesportada);mostrar(bienvenidamsg);ocultar(formulario);ocultar(tablalistado);ocultar(estadisticas)})
volverButton2.addEventListener("click",() => {mostrar(tablaportada);mostrar(botonesportada);mostrar(bienvenidamsg);ocultar(formulario);ocultar(tablalistado);ocultar(estadisticas)})
volverButton3.addEventListener("click",() => {mostrar(tablaportada);mostrar(botonesportada);mostrar(bienvenidamsg);ocultar(formulario);ocultar(tablalistado);ocultar(estadisticas)})
volverButton4.addEventListener("click",() => {mostrar(tablaportada);mostrar(botonesportada);mostrar(bienvenidamsg);ocultar(formulario);ocultar(tablalistado);ocultar(estadisticas);ocultar(finalmsg)})
buttonSI.addEventListener("click",() => {mostrar(finalmsg);ocultar(formulario);ocultar(confirmationmsg)})
buttonNO.addEventListener("click",() => {mostrar(formulario);ocultar(confirmationmsg)})

// === BLOQUE: Fotos dinámicas ===
const MAX_FOTOS = 5;
const contenedorFotos = fotoInput.parentElement;
const btnAgregarFoto = document.createElement("button");
btnAgregarFoto.type = "button";
btnAgregarFoto.textContent = "Agregar otra foto";
contenedorFotos.appendChild(btnAgregarFoto);

const mensajeFotos = document.createElement("p");
mensajeFotos.style.color = "red";
contenedorFotos.appendChild(mensajeFotos);

btnAgregarFoto.addEventListener("click", () => {
    const inputsActuales = contenedorFotos.querySelectorAll('input[type="file"]').length;

    if(inputsActuales >= MAX_FOTOS){
        mensajeFotos.textContent = "No puedes agregar más de 5 fotos.";
        return;
    }

    const nuevoInput = document.createElement("input");
    nuevoInput.type = "file";
    nuevoInput.name = "foto";
    nuevoInput.accept = "image/*";
    contenedorFotos.appendChild(nuevoInput);
    mensajeFotos.textContent = "";
});

//Validadores
const validarlargo = (imp) => imp.length < 100;
const validadorMail = (mail) => mail && mail.includes("@") && mail.length<100;
const validadornombre = (nombre) => nombre && nombre.length > 4 && nombre.length < 200;
const validadorNum = (tel) => /^\+\d{3}\.\d{8}$/.test(tel);
const validadoredad = (edad) => edadInput.value > 1;
const validadorcantidad = (cantidad) => Number.isInteger(Number(cantidad))&& cantidad > 0

// Validación de fotos: revisa todos los inputs dentro del contenedor
const validarfotos = (contenedor) => {
    const inputs = contenedor.querySelectorAll('input[type="file"]');
    let cantidad = 0;
    inputs.forEach(i => { if(i.files.length > 0) cantidad++; });
    return cantidad >= 1 && cantidad <= MAX_FOTOS;
}

// === BLOQUE: Redes dinámicas ===
const MAX_REDES = 5;
const contenedorRedes = redesinput;

// Función que crea un input solo si hay espacio
function crearInputRed() {
    const inputsActuales = contenedorRedes.querySelectorAll('input[type="text"]').length;
    if(inputsActuales >= MAX_REDES){
        alert("No puedes agregar más de 5 redes.");
        return;
    }
    const nuevoInput = document.createElement("input");
    nuevoInput.type = "text";
    nuevoInput.placeholder = "ID o URL";
    contenedorRedes.appendChild(nuevoInput);
}

// Cada vez que se selecciona una red
redes.addEventListener("change", () => {
    if(redes.value !== ""){
        crearInputRed();
        redes.value = ""; // opcional: vuelve a la opción vacía
    }
});

// Validación de todos los inputs de redes antes de enviar
function validarRedes() {
    let isValid = true;
    const inputs = contenedorRedes.querySelectorAll('input[type="text"]');
    inputs.forEach(i => {
        if(i.value.length < 4 || i.value.length > 50){
            isValid = false;
            i.style.borderColor = "red";
        } else {
            i.style.borderColor = "";
        }
    });
    return isValid;
}

//Función Principal
function validarForm() {
    let isValid = false;
    let msg = "";

    if (!validadorMail(emailInput.value)) {
        msg += "Mail malo!\n";
        emailInput.style.borderColor = "red";
    } else {
        emailInput.style.borderColor = "";
    }

    if (!validarlargo(sector.value)) {
        msg += "largo máximo 100 caracteres\n";
    }

    if (!validadornombre(nombreInput.value)) {
        msg += "Nombre malo!\n";
        nombreInput.style.borderColor = "red";
    } else {
        nombreInput.style.borderColor = "";
    }

    if (numInput.value != "") {
        if (!validadorNum(numInput.value)) {
            msg += "Error en el formato\n";
        }
    }

    if (!validadorcantidad(cantidadInput.value)) {
        msg += "Error en la cantidad\n";
    }

    if (!validadoredad(edadInput.value)) {
        msg += "Error en la edad\n";
    }

    if(!validarfotos(contenedorFotos)){
        msg += "Añade entre 1 y 5 imágenes\n";
        contenedorFotos.querySelectorAll('input[type="file"]').forEach(i => i.style.borderColor = "red");
    } else {
        contenedorFotos.querySelectorAll('input[type="file"]').forEach(i => i.style.borderColor = "");
    }

    if(!validarRedes()){
        msg += "Los ID/URL de las redes deben tener entre 4 y 50 caracteres\n";
    }

    if (msg === "") {
        ocultar(formulario)
        mostrar(confirmationmsg)
    } else {
        alert(msg);
    }
}

let submit = document.getElementById("enviar");
submit.addEventListener("click", validarForm);
