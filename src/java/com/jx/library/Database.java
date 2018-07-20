package com.jx.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Jesus
 */
public class Database implements AutoCloseable {

// Variables
  
  /**
   * Driver de coneccion. 
   * MySQL      : com.mysql.jdbc.Driver 
   * Oracle     : oracle.jdbc.driver.OracleDriver 
   * PostgreSQL : org.postgresql.Driver
   * SQLServer  : com.microsoft.sqlserver.jdbc.SQLServerDriver
   */
  private String driverClassName = "com.mysql.jdbc.Driver";

  private String url;
  private String username;
  private String password;
  private boolean debug;

  private Connection con;

// Costructor
  
  public Database() {
  }

// Funciones  
  
  /**
   * Establece la coneccion con la base de datos.
   *
   * @return la coneccion
   *
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    if (isClosed()) {
      try {
        Class.forName(driverClassName);
      } catch (ClassNotFoundException ex) {
        throw new SQLException(ex);
      }
      con = DriverManager.getConnection(url, username, password);
      if (debug)
        System.out.println("com.mysql.jdbc.JDBC4Connection: OPEN " + url);
    }
    return con;
  }

  /**
   * Prepara sentencias sql.
   *
   * @param sql instruccion a preparar
   * @param i [opcional] flags de configuracion
   *
   * @return PreparedStatement setencia preparada
   *
   * @throws SQLException
   */
  public PreparedStatement prepareStatement(String sql, int... i) 
  throws SQLException {
    if (i == null || i.length == 0)
      return getConnection().prepareStatement(sql);
    else
      return getConnection().prepareStatement(sql, i);
  }
  
  /**
   * Ejecuta consultas a la base de datos.
   *
   * @param sql query a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return ResultSet con el resultado obtenido
   *
   * @throws SQLException
   */
  public ResultSet query(String sql, Object... params) throws SQLException {
    PreparedStatement ps = prepareStatement(sql);
    try {
      if (params != null) {
        for (int i = 0; i < params.length; i++) {
          ps.setObject(i + 1, params[i]);
        }
      }
      if (debug) System.out.println(ps);
      return ps.executeQuery();
    } finally {
      //ps.closeOnCompletion();
    }
  }
  
  public static Map<String, Object> mapper(ResultSet rs) throws SQLException {
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    Map<String, Object> map = new LinkedHashMap<String, Object>(columnCount);
    for (int i = 1; i <= columnCount; i++) {
      map.put(metaData.getColumnLabel(i), rs.getObject(i));
    }
    return map;
  }

  /**
   * Ejecuta sentencias a la base de datos.
   *
   * @param sql sentencia a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return @true resultado obtenido
   *
   * @throws SQLException
   */
  public boolean execute(String sql, Object... params) throws SQLException {
    return executeUpdate(sql, params) == 1;
  }
  
  /**
   * Ejecuta sentencias insert y obtiene el id del registro insertado.
   * 
   * @param sql sentencia insert
   * @param params [opcional] parametros de la sentencia
   * 
   * @return el ID de la fila recién insertada, o -1 si se produjo un error
   * 
   * @throws SQLException 
   */
  public long executeInsert(String sql, Object... params) throws SQLException {
    PreparedStatement ps = null;
    try {
      ps = prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      if (params != null) {
        for (int i = 0; i < params.length; i++) {
          ps.setObject(i + 1, params[i]);
        }
      }
      if (debug) System.out.println(ps);
      if (ps.executeUpdate() == 1) {
        ResultSet rs = null;
        try {
          //obtengo las ultimas llaves generadas
          rs = ps.getGeneratedKeys();
          return rs.next() ? rs.getLong(1) : 0;
        } finally {
          close(rs);
        }
      } else {
        return -1;
      }
    } finally {
      close(ps);
    }
  }

  public int executeUpdate(String sql, Object... params) throws SQLException {
    PreparedStatement ps = null;
    try {
      ps = prepareStatement(sql);
      if (params != null) {
        for (int i = 0; i < params.length; i++) {
          ps.setObject(i + 1, params[i]);
        }
      }
      if (debug) System.out.println(ps);
      return ps.executeUpdate();
    } finally {
      close(ps);
    }
  }

  /**
   * Obtiene el numero de filas.
   *
   * @param tabla donde se buscaran las existencias
   * @param whereClause condicion
   * @param whereArgs [opcional] parametros del whereClause
   *
   * @return numero de existencia
   *
   * @throws SQLException
   */
  public long count(String tabla, String whereClause, Object... whereArgs) 
  throws SQLException {
    String sql = "SELECT COUNT(*) AS COUNT FROM " + tabla;
    if (whereClause != null && !whereClause.isEmpty()) {
      sql += " WHERE " + whereClause;
    }
    ResultSet rs = null;
    try {
      rs = query(sql, whereArgs);
      return rs.next() ? rs.getLong("COUNT") : -1;
    } finally {
      close(rs);
    }
  }
  
  public ResultSet select(boolean distinct, String table, String[] columns,
            String whereClause, Object[] whereArgs, String groupBy,
            String having, String orderBy, String limit) throws SQLException {
    if (isEmpty(groupBy) && !isEmpty(having)) {
      throw new IllegalArgumentException(
              "HAVING clauses are only permitted when using a groupBy clause");
    }

    StringBuilder query = new StringBuilder(120);

    query.append("SELECT ");
    if (distinct) {
      query.append("DISTINCT ");
    }
    if (columns != null && columns.length != 0) {
      appendColumns(query, columns);
    } else {
      query.append("* ");
    }
    query.append("FROM ");
    query.append(table);
    appendClause(query, " WHERE ", whereClause);
    appendClause(query, " GROUP BY ", groupBy);
    appendClause(query, " HAVING ", having);
    appendClause(query, " ORDER BY ", orderBy);
    appendClause(query, " LIMIT ", limit);

    return query(query.toString(), whereArgs);
  }
  
  private static boolean isEmpty(CharSequence str) {
    return (str == null || str.length() == 0)
            ? Boolean.TRUE
            : Boolean.FALSE;
  }
  
  private static void appendClause(StringBuilder s, String name, String clause) {
    if (!isEmpty(clause)) {
      s.append(name);
      s.append(clause);
    }
  }
  
  /**
   * Add the names that are non-null in columns to s, separating them with
   * commas.
   */
  public static void appendColumns(StringBuilder s, String[] columns) {
    for (int i = 0; i < columns.length; i++) {
      String column = columns[i];
      if (column != null) {
        if (i > 0) {
          s.append(", ");
        }
        s.append(column);
      }
    }
    s.append(' ');
  }
  
  /**
   * Inserta un registro en la base de datos.
   *
   * @param tabla donde se va a insertar la fila
   * @param datos mapa contiene los valores de columna iniciales para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   *
   * @return el ID de la fila recién insertada, o -1 si se produjo un error
   *
   * @throws SQLException
   */
  public long insert(String tabla, Map<String, Object> datos) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO ");
    sql.append(tabla);
    sql.append('(');

    int size = (datos != null && datos.size() > 0) ? datos.size() : 0;
    Object[] bindArgs = new Object[size];
    int i = 0;
    for (String colName : datos.keySet()) {
      sql.append((i > 0) ? "," : "");
      sql.append(colName);
      bindArgs[i++] = datos.get(colName);
    }

    sql.append(')');
    sql.append(" VALUES (");
    for (i = 0; i < size; i++) {
      sql.append((i > 0) ? ",?" : "?");
    }
    sql.append(')');

    return executeInsert(sql.toString(), bindArgs);
  }

  /**
   * Actualiza una registro en la base de datos.
   *
   * @param tabla donde se va a actualizar la fila.
   * @param datos mapa contiene los valores de columna iniciales para la fila. 
   *      Las claves deben ser los nombres de las columnas y los valores valores 
   *      de la columna.
   * @param whereClause [opcional] cláusula WHERE para aplicar al actualizar.
   *      Pasar null actualizará todas las filas.
   * @param whereArgs [opcional] Puede incluirse en la cláusula WHERE, que
   *      será reemplazado por los valores de whereArgs. Los valores
   *      se enlazará como cadenas.
   *
   * @return el número de filas afectadas.
   *
   * @throws SQLException
   */
  public int update(String tabla, Map<String, Object> datos, String whereClause, Object... whereArgs)
  throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("UPDATE ");
    sql.append(tabla);
    sql.append(" SET ");

    int setValuesSize = datos.size();
    int bindArgsSize = (whereArgs == null) ? setValuesSize : (setValuesSize + whereArgs.length);
    Object[] bindArgs = new Object[bindArgsSize];
    int i = 0;
    for (String colName : datos.keySet()) {
      sql.append((i > 0) ? "," : "");
      sql.append(colName);
      bindArgs[i++] = datos.get(colName);
      sql.append("=?");
    }

    if (whereArgs != null) {
      for (i = setValuesSize; i < bindArgsSize; i++) {
        bindArgs[i] = whereArgs[i - setValuesSize];
      }
    }
    if (whereClause != null && !whereClause.isEmpty()) {
      sql.append(" WHERE ");
      sql.append(whereClause);
    }

    return executeUpdate(sql.toString(), bindArgs);
  }
  
  /**
   * Elimina un registro de la base de datos.
   * 
   * @param tabla donde se eliminara
   * @param whereClause [opcional] cláusula WHERE para aplicar la eliminación.
   *      Pasar null elimina todas las filas.
   * @param whereArgs [opcional] Puede incluirse en la cláusula WHERE, que
   *      será reemplazado por los valores de whereArgs. Los valores
   *      se enlazará como cadenas.
   * 
   * @return el número de filas afectadas.
   * 
   * @throws SQLException 
   */
  public int delete(String tabla, String whereClause, Object... whereArgs) 
  throws SQLException {
    String sql = "DELETE FROM " + tabla;
    if (whereClause != null && !whereClause.isEmpty()) {
      sql += " WHERE " + whereClause;
    }
    return executeUpdate(sql, whereArgs);
  }

  /**
   * @return @true si la base de datos esta cerrada.
   *
   * @throws SQLException
   */
  public boolean isClosed() throws SQLException {
    return con == null || con.isClosed();
  }
  
  public static void close(AutoCloseable ac) {
    try {
      if (ac != null) ac.close();
    } catch (Exception ignore) {
      // empty
    }
  }
  
  @Override public void close() {
    close(con);
    if (debug)
      System.out.println("com.mysql.jdbc.JDBC4Connection: CLOSE " + url);
  }
  
  @Override protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }
  
  public String getDriverClassName() {
    return driverClassName;
  }
  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isDebug() {
    return debug;
  }
  public void setDebug(boolean debug) {
    this.debug = debug;
  }
}
