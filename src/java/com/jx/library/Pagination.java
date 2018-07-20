package com.jx.library;

import java.io.Serializable;

/**
 *
 * @author Jesus
 */
public class Pagination implements Serializable {

// Variables  
  
  private String baseUrl = "#"; // site_url('pagina/');
  private int totalRows = 20; //$total;
  private int limit = 5; // $limite;
  private int index = 0;

  // Contenedor principal.
  private String fullTagOpen = "<ul class='pagination'>";
  private String fullTagClose = "</ul>";

  // Página o indice actual.
  private String curTagOpen = "<li class='active'><a>";
  private String curTagClose = "</a></li>";

  // Diseño para los numeros de la paguinación.
  private String numTagOpen = "<li>";
  private String numTagClose = "</li>";

// Costructor
  
  public Pagination() {
  }

// Funciones  
  
  public String createLinks() {
    // Si nuestro recuento de artículos o total por página es cero, no es necesario continuar.
    if (totalRows == 0 || limit == 0) {
      return "";
    }
    
    // Calcula el número total de páginas. Redondear fracciones hacia arriba
    int totalPaginas = (int) Math.ceil((double)totalRows / limit);

    // ¿Hay solo una página? Hm ... no hay nada más que hacer aquí entonces.
    if (totalPaginas == 1) {
      return "";
    }
    
    // Put together our base and first URLs.
    baseUrl = baseUrl.trim();
		
    // And here we go...
    StringBuilder output = new StringBuilder();
    
    appendln(output, fullTagOpen);

    for (int pagina = 1; pagina <= totalPaginas; pagina++) {
      int page = (pagina - 1) * limit;
      
      if (page == index) {
        append(output, curTagOpen);
        append(output, Integer.toString(pagina));
        appendln(output, curTagClose);
      } else {
        append(output, numTagOpen);
        append(output, "<a href='");
        append(output, baseUrl);
        append(output, Integer.toString(page));
        append(output, "'>");
        append(output, Integer.toString(pagina));
        append(output, "</a>");
        appendln(output, numTagClose);
      }
    }
    
    append(output, fullTagClose);
    
    return output.toString();            
  }
  
  public static void append(StringBuilder sb, String value) {
    sb.append(value);
  }
  
  public static void appendln(StringBuilder sb, String value) {
    sb.append(value).append("\n");
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getFullTagOpen() {
    return fullTagOpen;
  }

  public void setFullTagOpen(String fullTagOpen) {
    this.fullTagOpen = fullTagOpen;
  }

  public String getFullTagClose() {
    return fullTagClose;
  }

  public void setFullTagClose(String fullTagClose) {
    this.fullTagClose = fullTagClose;
  }

  public String getCurTagOpen() {
    return curTagOpen;
  }

  public void setCurTagOpen(String curTagOpen) {
    this.curTagOpen = curTagOpen;
  }

  public String getCurTagClose() {
    return curTagClose;
  }

  public void setCurTagClose(String curTagClose) {
    this.curTagClose = curTagClose;
  }

  public String getNumTagOpen() {
    return numTagOpen;
  }

  public void setNumTagOpen(String numTagOpen) {
    this.numTagOpen = numTagOpen;
  }

  public String getNumTagClose() {
    return numTagClose;
  }

  public void setNumTagClose(String numTagClose) {
    this.numTagClose = numTagClose;
  }

  
  public static void main(String[] args) {
    Pagination pagination = new Pagination();
    pagination.setBaseUrl("articulos?index=");
    pagination.setTotalRows(11);
    pagination.setLimit(10);
    pagination.setIndex(0);
    System.out.println(pagination.createLinks());
  }
}
