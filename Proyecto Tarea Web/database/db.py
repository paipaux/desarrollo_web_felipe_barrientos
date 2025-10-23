import pymysql
import json
import os
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime, Enum, Text
from sqlalchemy.orm import sessionmaker, declarative_base, relationship
from datetime import datetime
from werkzeug.utils import secure_filename

DB_NAME = "tarea2"
DB_USERNAME = "root"
DB_PASSWORD = "felipe2004"
DB_HOST = "localhost"
DB_PORT = 3306
DB_CHARSET = "utf8"

UPLOAD_FOLDER = 'static/uploads'

with open('database/querys.json', 'r', encoding='utf-8') as querys:
    QUERY_DICT = json.load(querys)

def get_conn():
    conn = pymysql.connect(
        db=DB_NAME,
        user=DB_USERNAME,
        passwd=DB_PASSWORD,
        host=DB_HOST,
        port=DB_PORT,
        charset=DB_CHARSET
    )
    return conn

DATABASE_URL = f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
engine = create_engine(DATABASE_URL, echo=False, future=True)
SessionLocal = sessionmaker(bind=engine)
Base = declarative_base()

class Region(Base):
    __tablename__ = 'region'
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)
    comunas = relationship("Comuna", back_populates="region")

class Comuna(Base):
    __tablename__ = 'comuna'
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)
    region_id = Column(Integer, ForeignKey('region.id'), nullable=False)
    region = relationship("Region", back_populates="comunas")
    avisos = relationship("AvisoAdopcion", back_populates="comuna")

class AvisoAdopcion(Base):
    __tablename__ = 'aviso_adopcion'
    id = Column(Integer, primary_key=True, autoincrement=True)
    fecha_ingreso = Column(DateTime, nullable=False, default=datetime.utcnow)
    comuna_id = Column(Integer, ForeignKey('comuna.id'), nullable=False)
    sector = Column(String(100))
    nombre = Column(String(200), nullable=False)
    email = Column(String(100), nullable=False)
    celular = Column(String(15))
    tipo = Column(Enum('gato', 'perro'), nullable=False)
    cantidad = Column(Integer, nullable=False)
    edad = Column(Integer, nullable=False)
    unidad_medida = Column(Enum('a', 'm'), nullable=False)
    fecha_entrega = Column(DateTime, nullable=False)
    descripcion = Column(Text(500))
    
    comuna = relationship("Comuna", back_populates="avisos")
    fotos = relationship("Foto", back_populates="aviso")
    contactos = relationship("ContactarPor", back_populates="aviso")
    comentarios = relationship("Comentario", back_populates="aviso")

class Foto(Base):
    __tablename__ = 'foto'
    id = Column(Integer, primary_key=True, autoincrement=True)
    ruta_archivo = Column(String(300), nullable=False)
    nombre_archivo = Column(String(300), nullable=False)
    aviso_id = Column(Integer, ForeignKey('aviso_adopcion.id'), nullable=False)
    
    aviso = relationship("AvisoAdopcion", back_populates="fotos")

class ContactarPor(Base):
    __tablename__ = 'contactar_por'
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(Enum('whatsapp', 'telegram', 'X', 'instagram', 'tiktok', 'otra'), nullable=False)
    identificador = Column(String(150), nullable=False)
    aviso_id = Column(Integer, ForeignKey('aviso_adopcion.id'), nullable=False)
    
    aviso = relationship("AvisoAdopcion", back_populates="contactos")
    
class Comentario(Base):
    __tablename__ = 'comentario'
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(80), nullable=False)
    texto = Column(String(300), nullable=False)
    fecha = Column(DateTime, nullable=False, default=datetime.utcnow)
    aviso_id = Column(Integer, ForeignKey('aviso_adopcion.id'), nullable=False)
    
    aviso = relationship("AvisoAdopcion", back_populates="comentarios")

def get_ultimos_avisos(limit=5):
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = """
    SELECT aa.*, c.nombre as comuna_nombre, r.nombre as region_nombre,
           (SELECT f.ruta_archivo FROM foto f WHERE f.aviso_id = aa.id LIMIT 1) as foto_ruta
    FROM aviso_adopcion aa
    JOIN comuna c ON aa.comuna_id = c.id
    JOIN region r ON c.region_id = r.id
    ORDER BY aa.fecha_ingreso DESC
    LIMIT %s
    """
    cursor.execute(query, (limit,))
    avisos = cursor.fetchall()
    conn.close()
    return avisos

def get_all_regiones():
    session = SessionLocal()
    regiones = session.query(Region).order_by(Region.nombre).all()
    session.close()
    return regiones

def get_comunas_by_region(region_id):
    session = SessionLocal()
    comunas = session.query(Comuna).filter_by(region_id=region_id).order_by(Comuna.nombre).all()
    session.close()
    return comunas

def create_aviso_sqlalchemy(aviso_data):
    session = SessionLocal()
    nuevo_aviso = AvisoAdopcion(**aviso_data)
    session.add(nuevo_aviso)
    session.commit()
    aviso_id = nuevo_aviso.id
    session.close()
    return aviso_id

def create_foto_sqlalchemy(aviso_id, ruta_archivo, nombre_archivo):
    try:
        session = SessionLocal()
        nueva_foto = Foto(
            ruta_archivo=ruta_archivo,
            nombre_archivo=nombre_archivo,
            aviso_id=aviso_id
        )
        session.add(nueva_foto)
        session.commit()
        print(f"Foto insertada en BD: {nombre_archivo}")
        session.close()
    except Exception as e:
        print(f"Error insertando foto: {e}")
        session.rollback()
        session.close()

def create_contacto_sqlalchemy(aviso_id, nombre_red, identificador_red):
    try:
        session = SessionLocal()
        nuevo_contacto = ContactarPor(
            aviso_id=aviso_id,
            nombre=nombre_red,
            identificador=identificador_red
        )
        session.add(nuevo_contacto)
        session.commit()
        print(f"Contacto insertado en BD: {nombre_red}")
        session.close()
    except Exception as e:
        print(f"Error insertando contacto: {e}")
        session.rollback() 
        session.close()


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in {'png', 'jpg', 'jpeg', 'gif'}

def get_avisos_paginados(page=1, per_page=5):
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    offset = (page - 1) * per_page
    query = """
    SELECT aa.*, c.nombre as comuna_nombre, r.nombre as region_nombre,
           (SELECT COUNT(*) FROM foto f WHERE f.aviso_id = aa.id) as total_fotos
    FROM aviso_adopcion aa
    JOIN comuna c ON aa.comuna_id = c.id
    JOIN region r ON c.region_id = r.id
    ORDER BY aa.fecha_ingreso DESC
    LIMIT %s OFFSET %s
    """
    cursor.execute(query, (per_page, offset))
    avisos = cursor.fetchall()
    conn.close()
    return avisos

def get_total_avisos():
    conn = get_conn()
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM aviso_adopcion")
    total = cursor.fetchone()[0]
    conn.close()
    return total

def get_aviso_by_id(aviso_id):
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = """
    SELECT aa.*, c.nombre as comuna_nombre, r.nombre as region_nombre
    FROM aviso_adopcion aa
    JOIN comuna c ON aa.comuna_id = c.id
    JOIN region r ON c.region_id = r.id
    WHERE aa.id = %s
    """
    cursor.execute(query, (aviso_id,))
    aviso = cursor.fetchone()
    conn.close()
    return aviso

def get_fotos_by_aviso_id(aviso_id):
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    cursor.execute("SELECT * FROM foto WHERE aviso_id = %s", (aviso_id,))
    fotos = cursor.fetchall()
    conn.close()
    return fotos

def get_stats_avisos_por_dia():
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = """
        SELECT DATE_FORMAT(fecha_ingreso, '%Y-%m-%d') as dia, 
               COUNT(*) as total
        FROM aviso_adopcion
        GROUP BY dia
        ORDER BY dia ASC;
    """
    cursor.execute(query)
    data = cursor.fetchall()
    conn.close()
    return data

def get_stats_avisos_por_tipo():
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = """
        SELECT tipo, COUNT(*) as total
        FROM aviso_adopcion
        GROUP BY tipo;
    """
    cursor.execute(query)
    data = cursor.fetchall()
    conn.close()
    return data

def get_stats_avisos_por_mes():
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = """
        SELECT 
            DATE_FORMAT(fecha_ingreso, '%Y-%m') as mes,
            tipo,
            COUNT(*) as total
        FROM aviso_adopcion
        GROUP BY mes, tipo
        ORDER BY mes ASC, tipo;
    """
    cursor.execute(query)
    data = cursor.fetchall()
    conn.close()
    return data

def get_comentarios_by_aviso_id(aviso_id):
    conn = get_conn()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    query = "SELECT * FROM comentario WHERE aviso_id = %s ORDER BY fecha DESC"
    cursor.execute(query, (aviso_id,))
    comentarios = cursor.fetchall()
    conn.close()
    return comentarios

def create_comentario_sqlalchemy(aviso_id, nombre, texto):
    try:
        session = SessionLocal()
        nuevo_comentario = Comentario(
            aviso_id=aviso_id,
            nombre=nombre,
            texto=texto
        )
        session.add(nuevo_comentario)
        session.commit()
        
        session.refresh(nuevo_comentario)
        comentario_creado = {
            'id': nuevo_comentario.id,
            'nombre': nuevo_comentario.nombre,
            'texto': nuevo_comentario.texto,
            'fecha': nuevo_comentario.fecha.isoformat(),
            'aviso_id': nuevo_comentario.aviso_id
        }
        
        print(f"Comentario insertado en BD por: {nombre}")
        session.close()
        return comentario_creado
    except Exception as e:
        print(f"Error insertando comentario: {e}")
        session.rollback() 
        session.close()
        return None