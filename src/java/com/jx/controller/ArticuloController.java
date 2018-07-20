package com.jx.controller;

import com.jx.dao.ArticuloDao;
import com.jx.library.Pagination;
import com.jx.model.Articulo;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jesus
 */
@WebServlet(name = "ArticuloController", urlPatterns = {"/articulos"})
public class ArticuloController extends HttpServlet {

  /** Numero de registros por listado. */
  static final int MAX_RECORDS = 10;
  
  /** Origen de datos del recurso articulo. */
  private final ArticuloDao articuloDao;

  public ArticuloController() {
    articuloDao = new ArticuloDao();
  }

  /**
   * Handles the HTTP
   * <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
    String action = request.getParameter("action");
    action = (action == null) ? "" : action;
    
    try {
      // Validamos que accion debemos realizar.
      if (action.equals("add")) {
        add(request, response);
      } else if (action.equals("edit")) {
        edit(request, response);
      } else if (action.equals("delete")) {
        delete(request, response);
      } else { // default
        list(request, response);
      }
      
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
       articuloDao.close();
    }
  }

  /**
   * Handles the HTTP
   * <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
    String action = request.getParameter("action");
    action = (action == null) ? "" : action;
    
    try {
      // Validamos que accion debemos realizar.
      if (action.equals("save")) {
        save(request, response);
      } else {
        list(request, response);
      }
      
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      articuloDao.close();
    }
  }
  
  /**
   * Muestra un listado de registros.
   */ 
  private void list(HttpServletRequest request, HttpServletResponse response) 
  throws Exception {
    String search = request.getParameter("search");
    int index = getInt(request, "index", 0);
    
    Pagination p = new Pagination();
    p.setBaseUrl((search == null) 
            ? ("articulos?index=") : ("articulos?search=" + search + "&index="));
    p.setTotalRows((int) articuloDao.count(search));
    p.setLimit(MAX_RECORDS);
    p.setIndex(index);
    
    // Buscamos los registros
    List<Articulo> list = articuloDao.search(search, index, MAX_RECORDS);

    // Mostramos la vista y le mandamos la lista.
    RequestDispatcher dispatcher = request.getRequestDispatcher("articulo/list.jsp");
    request.setAttribute("list", list);
    request.setAttribute("pagination", p);
    dispatcher.forward(request, response);
  }

  /**
   * Muestra el formulario para agregar un nuevo registro.
   */
  private void add(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    Articulo articulo = new Articulo();
    // Mostramos la vista y le mandamos un modelo vacio.
    RequestDispatcher dispatcher = request.getRequestDispatcher("articulo/form.jsp");
    request.setAttribute("articulo", articulo);
    dispatcher.forward(request, response);
  }
  
  /**
   * Muestra el formulario para editar un registro.
   */
  private void edit(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    int id = getInt(request, "id", -1);
    
    // Recuperamos el registro por su id para editarlo.
    Articulo articulo = articuloDao.findById(id);
    if (articulo != null) {
      // Mostramos la vista y la mandamos el modelo para editarlo.
      RequestDispatcher dispatcher = request.getRequestDispatcher("articulo/form.jsp");
      request.setAttribute("articulo", articulo);
      dispatcher.forward(request, response);
      
    } else {
      add(request, response);
    }
  }
  
  /**
   * Elimina un registro por su id.
   */
  private void delete(HttpServletRequest request, HttpServletResponse response) 
  throws Exception {
    // Eliminamos el registro por el id que llego.
    int id = getInt(request, "id", -1);
    articuloDao.deleteById(id);
    // Mostramos la vista
    response.sendRedirect("articulos");
  }
 
  /**
   * Guarda un registro [insert,update]
   */
  private void save(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    int id = getInt(request, "id", -1);
    
    // Si nos manda un id modificamos, si no insertamos.
    Articulo articulo = new Articulo();
    articulo.setId(id);
    articulo.setCodigo(request.getParameter("codigo"));
    articulo.setNombre(request.getParameter("nombre"));
    articulo.setDescripcion(request.getParameter("descripcion"));
    articulo.setExistencia(Double.parseDouble(request.getParameter("cantidad")));
    articulo.setPrecio(Double.parseDouble(request.getParameter("precio")));
    articuloDao.save(articulo);
    // Mostramos la lista de registros
    response.sendRedirect("articulos");
  } 

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }
  
  static int getInt(HttpServletRequest request, String name, int defaultVal) {
    String parameter = request.getParameter(name);
    if (parameter == null) {
      return defaultVal;
    }
    try {
      return Integer.parseInt(parameter);
    } catch(NumberFormatException e) {
      return defaultVal;
    }
  }
}