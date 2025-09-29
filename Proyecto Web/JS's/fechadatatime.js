function setFechaEntrega() {
  const fechaInput = document.getElementById("fechaEntrega");
  if (!fechaInput) return; // seguridad

  // Fecha actual + 3 horas
  const ahora = new Date();
  ahora.setHours(ahora.getHours() + 3);

  const pad = n => n.toString().padStart(2, '0');
  const prellenado = `${ahora.getFullYear()}-${pad(ahora.getMonth() + 1)}-${pad(ahora.getDate())}T${pad(ahora.getHours())}:${pad(ahora.getMinutes())}`;

  // Prellenar y restringir
  fechaInput.value = prellenado;
  fechaInput.min = prellenado;
}

// Ejecutar al cargar
window.addEventListener("DOMContentLoaded", setFechaEntrega);