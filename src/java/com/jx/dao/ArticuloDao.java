
package com.jx.dao;

import com.jx.config.Config;
import com.jx.library.database.SQLDatabase;
import com.jx.model.Articulo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que administra el origen de la informacion.
 * 
 * @author jesus
 */
public class ArticuloDao implements AutoCloseable {

  /** Base de datos */
  private final SQLDatabase db;
  
  public ArticuloDao() {
    db = Config.getDataBaseMySQL();
  }
  
  /**
   * AL leer un registro de la base de datos.
   * 
   * @param rs resultado obtenido del query
   * 
   * @return modelo mapeado con el resultado obtenido
   * 
   * @throws SQLException 
   */
  protected Articulo onRead(ResultSet rs) throws SQLException {
    Articulo p = new Articulo();
    p.setId(rs.getInt("id"));
    p.setCodigo(rs.getString("codigo"));
    p.setNombre(rs.getString("nombre"));
    p.setDescripcion(rs.getString("descripcion"));
    p.setExistencia(rs.getDouble("existencia"));
    p.setPrecio(rs.getDouble("precio"));
    return p;
  }

  /**
   * Al guardar un registro en la base de datos.
   * 
   * @param m modelo donde estan los datos a guardar
   * 
   * @return mapa que contiene los valores de las columnas para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   */
  protected Map<String, Object> onWrite(Articulo m) {
    Map<String, Object> values = new HashMap<String, Object>(2);
    values.put("codigo", m.getCodigo());
    values.put("nombre", m.getNombre());
    values.put("descripcion", m.getDescripcion());
    values.put("existencia", m.getExistencia());
    values.put("precio", m.getPrecio());
    return values;
  }

  /**
   * Si el registro es editable: (true) ? Update : insert.
   * 
   * @param m modelo.
   * 
   * @return @true si es editable.
   * 
   * @throws java.sql.SQLException 
   */
  protected boolean isUpdate(Articulo m) throws SQLException {
    return m.getId() > 0;
  }
  
  /**
   * Al isertar el registro obtiene el id del registro.
   * 
   * @param m modelo insertado.
   * @param id insertado.
   */
  protected void insertId(Articulo m, long id) {
    if (id > 0) {
      m.setId((int)id);
    }
  }
  
  /**
   * Guarda el registro. Si el registro es editable hace un update, 
   * de lo contrario hace un insert.
   * 
   * @param m modelo a guardar.
   * 
   * @return @true si se guardaron los datos.
   * 
   * @throws SQLException 
   */
  public boolean save(Articulo m) throws SQLException {
    return isUpdate(m) ? update(m) : insert(m);
  }
  
  /**
   * Inserta el registro.
   * 
   * @param m modelo a guardar.
   * 
   * @return @true si se guardaron los datos.
   * 
   * @throws SQLException 
   */
  public boolean insert(Articulo m) throws SQLException {
    Map<String, Object> values = onWrite(m);
    long id = db.insert("articulos", values);
    insertId(m, id);
    return id != -1;
  }
  
  /**
   * Actualiza el registro.
   * 
   * @param m modelo a guardar.
   * 
   * @return @true si se guardaron los datos.
   * 
   * @throws SQLException 
   */
  public boolean update(Articulo m) throws SQLException {
    Map<String, Object> values = onWrite(m);
    return db.update("articulos", values, "id = ?", m.getId()) == 1;
  }
  
  /**
   * Elimina un registro por su identificador.
   * 
   * @param id identificador.
   * 
   * @return @true si se eliminaron los datos.
   * 
   * @throws SQLException 
   */
  public boolean deleteById(int id) throws SQLException {
    return db.delete("articulos", "id = ?", id) == 1;
  }

  /**
   * Busca un registro por su identificador.
   * 
   * @param id identificador.
   * 
   * @return un modelo.
   * 
   * @throws SQLException 
   */
  public Articulo findById(int id) throws SQLException {
    String sql = "SELECT * FROM articulos WHERE id = ?";
    ResultSet rs = null;
    try {   
      rs = db.query(sql, id);
      return rs.next() ? onRead(rs) : null;
    } finally {
      SQLDatabase.closeQuietly(rs);
    }
  }
  
  /**
   * Consulta registros en la base de datos.
   * 
   * @param search criterio de busqueda.
   * @param index indice actual.
   * @param limite de registros por consulta.
   * 
   * @return una lista de registros.
   * 
   * @throws SQLException 
   */
  public List<Articulo> search(String search, int index, int limite) throws SQLException {
    search = search == null ? ("%%") : ("%" + search + "%");
    
    final String whereClause = "codigo LIKE ? OR nombre LIKE ? OR descripcion LIKE ?";
    final Object[] whereArgs = {search, search, search};
    
    ResultSet rs = null;
    try {   
      rs = db.select(false, "articulos", null, whereClause, whereArgs, 
              null, null, null, (index + ", " + limite));
      
      int len = rs.last() ? rs.getRow() : 0;
      List<Articulo> list = new ArrayList<Articulo>(len);
      
      rs.beforeFirst();
      while (rs.next()) {
        list.add(onRead(rs));
      }
      return list;
      
    } finally {
      SQLDatabase.closeQuietly(rs);
    }
  }
  
  /**
   * Obtiene el numero de filas.
   * 
   * @param search criterio de busqueda.
   * 
   * @return numero de filas.
   * 
   * @throws SQLException 
   */
  public long count(String search) throws SQLException {
    search = search == null ? ("%%") : ("%" + search + "%");
    
    final String whereClause = "codigo LIKE ? OR nombre LIKE ? OR descripcion LIKE ?";
    final Object[] whereArgs = {search, search, search};
    
    return db.count("articulos", whereClause, whereArgs);
  }

  @Override
  public void close() {
    db.close();
  }
}