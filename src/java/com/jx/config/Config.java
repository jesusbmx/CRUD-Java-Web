package com.jx.config;

import com.jx.library.Database;


/**
 *
 * @author Jesus
 */
public final class Config {

  private static Database mysql;

  private Config() {
  }

  public static Database getDataBaseMySQL() {
    if (mysql == null) {
      mysql = new Database();
      // Ejemplo con base de datos MySQL
      mysql.setDriverClassName("com.mysql.jdbc.Driver");
      mysql.setUrl("jdbc:mysql://localhost:3306/javaweb_crud");
      mysql.setUsername(/*"usuario"*/"root");
      mysql.setPassword(/*"password"*/"");
      mysql.setDebug(Boolean.TRUE);
    }
    return mysql;
  }
}
