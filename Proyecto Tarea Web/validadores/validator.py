import re
from datetime import datetime

def validar_aviso(data):
    errores = []
    
    nombre = data.get('nombre', '').strip()
    if not nombre or len(nombre) < 3 or len(nombre) > 200:
        errores.append("Nombre")
    
    
    email = data.get('email', '').strip()
    if not email or len(email) >= 100:
        errores.append("Email")
    else:
        email_regex = r'^[\w.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$'
        if not re.match(email_regex, email):
            errores.append("Email")
    
    numero = data.get('numero', '').strip()
    numero_regex = r'^\+\d{3}\.\d{8}$'
    if not numero or not re.match(numero_regex, numero):
        errores.append("Número")
    

    tipo = data.get('select-tipo')
    if not tipo:
        errores.append("Tipo")
    
    
    cantidad = data.get('cantidad')
    if not cantidad or int(cantidad) < 1:
        errores.append("Cantidad")
    
    
    edad = data.get('edad')
    if not edad or int(edad) < 1:
        errores.append("Edad")
    

    medida = data.get('select-medida')
    if not medida:
        errores.append("Tipo") 
    
    
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
    
    
    region = data.get('select-region')
    if not region:
        errores.append("Región")
    
    
    comuna = data.get('select-comuna')
    if not comuna:
        errores.append("Comuna")
    

    return errores

def validar_comentario(data):
    errores = []
    
    nombre = data.get('nombre', '').strip()
    if not nombre or len(nombre) < 3 or len(nombre) > 80:
        errores.append("El nombre es obligatorio (entre 3 y 80 caracteres).")
    
    texto = data.get('texto', '').strip()
    if not texto or len(texto) < 5:
        errores.append("El comentario es obligatorio (mínimo 5 caracteres).")
        
    return errores