<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="com.jx.library.Pagination" %>

<%@include file="../layout/header.jsp" %>

<!-- Page Header 
================================================== -->
<div class="row">
  <div class="col-md-12">
    <h1 class="page-header">Articulos</h1>
  </div>
</div>

<!-- Page Grind 
================================================== -->
<div class="row">
  <div class="col-sm-8" style="margin-bottom: 1rem">
    <ul class="list-inline">
      <li><a class="btn btn-sm btn-primary" href="articulos?action=add">Nuevo articulo</a></li>
      <li><a class="btn btn-sm btn-default" href="#">Otra cosa</a></li>
    </ul>
  </div>

  <div class="col-sm-4" style="margin-bottom: 1rem">
    <form method="get" action="articulos" class="input-group">
      <input type="text" name="search" class="form-control" placeholder="Buscar">
      <span class="input-group-btn">
        <button type="submit" class="btn btn-success"><i class="glyphicon glyphicon-search"></i></button>
      </span>
    </form>
  </div>
</div>

<!-- Page Table 
================================================== -->
<div class="row">
  <div class="col-md-12">
    <div class="table-responsive">
      <table class="table table-hover">
        <thead>
          <tr>
            <th>CODIGO</th>
            <th>NOMBRE</th>
            <th>DESCRPICION</th>
            <th>EXISTENCIA</th>
            <th>PRECIO</th>
            <th colspan=2>ACCIONES</th>
          </tr>
        </thead>
        <tbody style="cursor: pointer">
          <c:forEach var="articulo" items="${list}">
            <tr>
              <td>${articulo.codigo}</td>
              <td>${articulo.nombre}</td>
              <td>${articulo.descripcion}</td>
              <td>${articulo.existencia}</td>
              <td>${articulo.precio}</td>
              <td><a href="articulos?action=edit&id=${articulo.id}" class="btn btn-xs btn-primary">Editar</a></td>
              <td><a href="articulos?action=delete&id=${articulo.id}" class="btn btn-xs btn-danger">Eliminar</a> </td>				
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div> 
  </div>
</div>

<!-- Pagination Table 
================================================== -->
<div class="row">
  <div class="col-md-12 text-center">
    ${pagination.createLinks()}
  </div>
</div>

<%@include file="../layout/footer.jsp" %>
