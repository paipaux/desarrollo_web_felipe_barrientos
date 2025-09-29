const validateName = (nombre) => {
  if(!nombre) return false;
  let minlengthValid = nombre.trim().length >= 3;
  let maxlengthValid = nombre.trim().length <= 200;


  return minlengthValid && maxlengthValid;
}

const validateEmail = (email) => {
  if (!email) return false;
  let lengthValid = email.length < 100;

  let re = /^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
  let formatValid = re.test(email);

  return lengthValid && formatValid;
};

const validateNum = (num) => {
  if (!num) return false;
  let re = /^\+\d{3}\.\d{8}$/;
  return re.test(num);
};

const validateCant = (cant) => {
  if (!cant) return false;
  let mincantidad = cant >= 1;
  return mincantidad
}

const validateEdad = (edad) => {
  if (!edad) return false;
  let mincantidad = edad >= 1;
  return mincantidad
}

const validateFechaEntrega = (fechaStr) => {
  if (!fechaStr) return false;

  const input = document.getElementById("fechaEntrega");

  // convertir a Date local (construcción explícita)
  const toDate = (s) => {
    const [d, t] = s.split("T");
    const [Y, M, D] = d.split("-").map(Number);
    const [h, m] = t.split(":").map(Number);
    return new Date(Y, M - 1, D, h, m);
  };

  const fechaInput = toDate(fechaStr);
  const fechaMin = input.min ? toDate(input.min) : new Date(Date.now() + 3 * 3600 * 1000);

  return fechaInput >= fechaMin;
};

//Select de fotos
const validateFotos = () => {
  const fotosInput = document.getElementById("fotos");
  const archivos = fotosInput.files;
  
  if (archivos.length === 0) {
    return false;
  }
  
  if (archivos.length > 5) {
    return false;
  }
  
  for (let i = 0; i < archivos.length; i++) {
    const archivo = archivos[i];
    if (!archivo.type.startsWith('image/')) {
      return false;
    }
  }
  
  return true;
};

const validateRegion = (region) => {
    return !!region; 
};

const validateComuna = (comuna) => {
    return !!comuna; 
};


const mostrarAgradecimiento = () => {
  let validationBox = document.getElementById("val-box");
  let validationMessageElem = document.getElementById("val-msg");
  let validationListElem = document.getElementById("val-list");
  
  validationMessageElem.innerText = "Hemos recibido la información de adopción, muchas gracias y suerte!";
  validationListElem.textContent = "";
  
  validationBox.style.backgroundColor = "#ddffdd";
  validationBox.style.borderLeftColor = "#4CAF50";
  
  let portadaButton = document.createElement("button");
  portadaButton.innerText = "Volver a la Portada";
  portadaButton.addEventListener("click", () => {
    window.location.href="Portada.html"
    // Aquí deberías redirigir a la portada de tu sistema
    // Por ejemplo: window.location.href = "index.html";
    // En un caso real, descomenta la línea de arriba y pon la URL correcta
  });
  
  validationListElem.appendChild(portadaButton);
  validationBox.hidden = false;
};

// Validador Final

const validateForm = () => {
  let Form = document.forms["formulario"];
  let nombre = Form["nombre"].value;
  let email = Form["email"].value;
  let numero = Form["numero"].value;
  let tipo = Form["select-tipo"].value;
  let cantidad = Form["cantidad"].value;
  let edad = Form["edad"].value;
  let medida = Form["select-medida"].value;
  let fechaEntrega = Form["fechaEntrega"].value;
  let region = Form["select-region"].value;
  let comuna = Form["select-comuna"].value;
  let invalidInputs = [];
  let isValid = true;

  const setInvalidInput = (inputName) => {
    invalidInputs.push(inputName);
    isValid &&= false;
  };

  if (!validateName(nombre)) {
    setInvalidInput("Nombre");
  }
  if (!validateEmail(email)) {
    setInvalidInput("Email");
  }
  if (!validateNum(numero)) {
    setInvalidInput("Número");
  }

  if (!tipo) {              
  setInvalidInput("Tipo"); 
  }

  if (!validateCant(cantidad)) {
    setInvalidInput("Cantidad");
  }

  if (!validateEdad(edad)) {
    setInvalidInput("Edad");
  }

  if (!medida) {              
  setInvalidInput("Tipo"); 
  }
  
  if (!validateFechaEntrega(fechaEntrega)){
  setInvalidInput("Fecha de entrega");
  }

  if (!validateFotos()) {
    setInvalidInput("Fotos (mínimo 1, máximo 5)");
  }

  if (!validateRegion(region)) {
    setInvalidInput("Región");
  }



  if (!validateComuna(comuna)) {
    setInvalidInput("Comuna");
  }

  

  

  let validationBox = document.getElementById("val-box");
  let validationMessageElem = document.getElementById("val-msg");
  let validationListElem = document.getElementById("val-list");
  let formContainer = document.querySelector(".main-container");
    
  if (!isValid) {
    validationListElem.textContent = "";
    // agregar elementos inválidos al elemento val-list.
    for (input of invalidInputs) {
      let listElement = document.createElement("li");
      listElement.innerText = input;
      validationListElem.append(listElement);
    }
    // establecer val-msg
    validationMessageElem.innerText = "Los siguientes campos son inválidos:";

    // aplicar estilos de error
    validationBox.style.backgroundColor = "#ffdddd";
    validationBox.style.borderLeftColor = "#f44336";

    // hacer visible el mensaje de validación
    validationBox.hidden = false;
  } else {
    // Ocultar el formulario
    formulario.style.display = "none";
    
    // Mostrar mensaje de confirmación
    validationMessageElem.innerText = "¿Está seguro que desea agregar este aviso de adopción?";
    validationListElem.textContent = "";
    validationBox.style.backgroundColor = "#e8f4fd";
    validationBox.style.borderLeftColor = "#2196F3";

    // Crear botones de confirmación
    let siButton = document.createElement("button");
    siButton.innerText = "Sí, estoy seguro";
    siButton.style.marginRight = "10px";
    siButton.addEventListener("click", mostrarAgradecimiento);

    let noButton = document.createElement("button");
    noButton.innerText = "No, no estoy seguro, quiero volver al formulario";
    noButton.addEventListener("click", () => {
      // Mostrar el formulario nuevamente
      formulario.style.display = "block";
      validationBox.hidden = true;
    });

    validationListElem.appendChild(siButton);
    validationListElem.appendChild(noButton);
    validationBox.hidden = false;
  }
        
}


let submitBtn = document.getElementById("enviar-button");
submitBtn.addEventListener("click", validateForm);  
