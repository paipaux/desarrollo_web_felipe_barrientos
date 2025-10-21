from flask import Flask, render_template, request, redirect, url_for, jsonify, flash
from database.db import get_ultimos_avisos, get_all_regiones, get_comunas_by_region, create_aviso_sqlalchemy, create_foto_sqlalchemy, allowed_file, get_avisos_paginados, get_total_avisos, get_aviso_by_id, get_fotos_by_aviso_id, UPLOAD_FOLDER, create_contacto_sqlalchemy, get_stats_avisos_por_dia, get_stats_avisos_por_tipo, get_stats_avisos_por_mes
from validadores.validator import validar_aviso
from datetime import datetime
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)
app.secret_key = 'clave_secreta'

@app.route('/')
def portada():
    avisos = get_ultimos_avisos(5)
    return render_template('Portada.html', avisos=avisos)

@app.route('/listado')
@app.route('/listado/<int:page>')
def listado(page=1):
    per_page = 5
    avisos = get_avisos_paginados(page, per_page)
    total_avisos = get_total_avisos()
    total_pages = (total_avisos + per_page - 1) // per_page
    
    return render_template('Listado.html', 
                          avisos=avisos, 
                          page=page, 
                          total_pages=total_pages)

@app.route('/estadisticas')
def estadisticas():
    return render_template('Estadísticas.html')

@app.route('/formulario')
def formulario():
    regiones = get_all_regiones()
    return render_template('Formulario.html', regiones=regiones)

@app.route('/api/comunas/<int:region_id>')
def api_comunas(region_id):
    comunas = get_comunas_by_region(region_id)
    comunas_json = [{'id': c.id, 'nombre': c.nombre} for c in comunas]
    return jsonify(comunas_json)

@app.route('/crear-aviso', methods=['POST'])
def crear_aviso():
    try:
        print("INICIANDO PROCESAMIENTO DEL FORMULARIO")
        
        errores = validar_aviso(request.form)
        
        if errores:
            flash("Los siguientes campos son inválidos: " + ", ".join(errores), 'error')
            return redirect(url_for('formulario'))
        
        tipo_corregido = request.form.get('select-tipo').lower()
        
        unidad_medida_form = request.form.get('select-medida')
        if unidad_medida_form == 'Años':
            unidad_medida_corregida = 'a'
        elif unidad_medida_form == 'Meses':
            unidad_medida_corregida = 'm'
        else:
            unidad_medida_corregida = unidad_medida_form

        aviso_data = {
            'comuna_id': request.form.get('select-comuna'),
            'sector': request.form.get('sector', ''),
            'nombre': request.form.get('nombre'),
            'email': request.form.get('email'),
            'celular': request.form.get('numero'),
            'tipo': tipo_corregido,
            'cantidad': int(request.form.get('cantidad')),
            'edad': int(request.form.get('edad')),
            'unidad_medida': unidad_medida_corregida,
            'fecha_entrega': datetime.strptime(request.form.get('fechaEntrega'), '%Y-%m-%dT%H:%M'),
            'descripcion': request.form.get('desc', '')
        }
        
        print("DATOS PARA BD (AVISO):")
        for key, value in aviso_data.items():
            print(f"   {key}: {value}")

        aviso_id = create_aviso_sqlalchemy(aviso_data)
        print(f"AVISO CREADO CON ID: {aviso_id}")

        fotos = request.files.getlist('fotos')
        print(f"PROCESANDO {len(fotos)} FOTOS...")
        
        fotos_guardadas = 0
        for i, foto in enumerate(fotos):
            if foto and foto.filename and allowed_file(foto.filename):
                filename = secure_filename(foto.filename)
                os.makedirs(UPLOAD_FOLDER, exist_ok=True)
                timestamp = datetime.now().strftime("%Y%m%d_%H%M%S_%f")
                unique_filename = f"{timestamp}_{filename}"
                filepath = os.path.join(UPLOAD_FOLDER, unique_filename).replace('\\', '/')
                
                print(f"Guardando foto en: {filepath}")
                foto.save(filepath)
                
                create_foto_sqlalchemy(aviso_id, filepath, unique_filename)
                fotos_guardadas += 1
        print(f"FOTOS GUARDADAS: {fotos_guardadas}")

        print("PROCESANDO CONTACTOS...")
        redes = request.form.getlist('contact_network')
        identificadores = request.form.getlist('contact_identifier')

        contactos_guardados = 0
        for red, ident in zip(redes, identificadores):
            if red and ident:
                if red == 'x': 
                    red = 'X'
                print(f"Guardando contacto: {red} - {ident}")
                create_contacto_sqlalchemy(aviso_id, red, ident)
                contactos_guardados += 1
        print(f"CONTACTOS GUARDADOS: {contactos_guardados}")
        
        flash("Aviso creado exitosamente", 'success')
        return redirect(url_for('portada'))
        
    except Exception as e:
        print(f"ERROR: {str(e)}")
        import traceback
        print(f"TRACEBACK: {traceback.format_exc()}")
        flash(f"Error al crear el aviso: {str(e)}", 'error')
        return redirect(url_for('formulario'))

@app.route('/aviso/<int:aviso_id>')
def ver_aviso(aviso_id):
    aviso = get_aviso_by_id(aviso_id)
    if not aviso:
        flash("Aviso no encontrado", 'error')
        return redirect(url_for('listado'))
    
    fotos = get_fotos_by_aviso_id(aviso_id)
    return render_template('aviso_detalle.html', aviso=aviso, fotos=fotos)

@app.route('/api/stats/avisos-por-dia')
def api_stats_avisos_por_dia():
    data = get_stats_avisos_por_dia()
    categorias = [item['dia'] for item in data]
    valores = [item['total'] for item in data]
    
    response_data = {
        'categorias': categorias,
        'valores': valores
    }
    return jsonify(response_data)

@app.route('/api/stats/avisos-por-tipo')
def api_stats_avisos_por_tipo():
    data = get_stats_avisos_por_tipo()
    response_data = []
    for item in data:
        response_data.append({
            'name': item['tipo'].capitalize(),
            'y': item['total']
        })
    return jsonify(response_data)

@app.route('/api/stats/avisos-por-mes')
def api_stats_avisos_por_mes():
    data = get_stats_avisos_por_mes()
    
    meses = sorted(list(set([item['mes'] for item in data])))
    datos_gatos = {mes: 0 for mes in meses}
    datos_perros = {mes: 0 for mes in meses}
    
    for item in data:
        if item['tipo'] == 'gato':
            datos_gatos[item['mes']] = item['total']
        elif item['tipo'] == 'perro':
            datos_perros[item['mes']] = item['total']
            
    response_data = {
        'categorias': meses,
        'series': [
            {
                'name': 'Gato',
                'data': [datos_gatos[mes] for mes in meses]
            },
            {
                'name': 'Perro',
                'data': [datos_perros[mes] for mes in meses]
            }
        ]
    }
    return jsonify(response_data)

if __name__ == '__main__':
    app.run(debug=True)