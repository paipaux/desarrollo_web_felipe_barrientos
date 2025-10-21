import re
from datetime import datetime

def validar_aviso(data):
    errores = []
    
    nombre = data.get('nombre', '').strip()
    if not nombre or len(nombre) < 3 or len(nombre) > 200:
        errores.append("Nombre")
    
    # Validar email (igual que validateEmail)
    email = data.get('email', '').strip()
    if not email or len(email) >= 100:
        errores.append("Email")
    else:
        email_regex = r'^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$'
        if not re.match(email_regex, email):
            errores.append("Email")
    
    # Validar teléfono (igual que validateNum)
    numero = data.get('numero', '').strip()
    numero_regex = r'^\+\d{3}\.\d{8}$'
    if not numero or not re.match(numero_regex, numero):
        errores.append("Número")
    
    # Validar tipo
    tipo = data.get('select-tipo')
    if not tipo:
        errores.append("Tipo")
    
    # Validar cantidad (igual que validateCant)
    cantidad = data.get('cantidad')
    if not cantidad or int(cantidad) < 1:
        errores.append("Cantidad")
    
    # Validar edad (igual que validateEdad)
    edad = data.get('edad')
    if not edad or int(edad) < 1:
        errores.append("Edad")
    
    # Validar unidad de medida
    medida = data.get('select-medida')
    if not medida:
        errores.append("Tipo")  # Manteniendo tu mensaje "Tipo"
    
    # Validar fecha entrega (similar a validateFechaEntrega)
    fecha_entrega = data.get('fechaEntrega')
    if not fecha_entrega:
        errores.append("Fecha de entrega")
    else:
        try:
            fecha_input = datetime.strptime(fecha_entrega, '%Y-%m-%dT%H:%M')
            fecha_min = datetime.now()
            if fecha_input <= fecha_min:
                errores.append("Fecha de entrega")
        except:
            errores.append("Fecha de entrega")
    
    # Validar región (igual que validateRegion)
    region = data.get('select-region')
    if not region:
        errores.append("Región")
    
    # Validar comuna (igual que validateComuna)
    comuna = data.get('select-comuna')
    if not comuna:
        errores.append("Comuna")
    

    return errores