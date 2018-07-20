<%@include file="../layout/header.jsp" %>

<!-- Page Header 
================================================== -->
<div id="page-wrapper">
<div class="row">
  <div class="col-sm-6">
    <h3>
      ${ (articulo.id == 0) ? "Nuevo Registro" : articulo.nombre }
    </h3>
    <p class="lead">Formulario</p>
  </div>

  <div class="col-sm-6">
    <p class="text-right">
      <a onclick="history.go(-1)" class="btn btn-default">
        <i class="glyphicon glyphicon-chevron-left"></i> Regresar
      </a>
    </p>
  </div>
</div>
  

<!-- Page Well
================================================== -->
<div class="row">
  <div class="col-md-12">  
    <div class="panel panel-primary"> 
      <!--Heading-->
      <div class="panel-heading">Datos del Articulo</div>
      <!--/Heading-->

      <!--Body-->
      <div class="panel-body">
        <!-- Formulario para el recurso -->
        <form id="form" method="post" action="articulos?action=save">
          <fieldset>
            <div class="row">
              <div class="col-sm-7">
                
                <input type="hidden" name="id" value="${articulo.id}" />
                
                <div class="form-group">
                  <label>Código</label>
                  <input type="text" name="codigo" value="${articulo.codigo}" class="form-control" placeholder="Ingrese el codigo" />
                </div>

                <div class="form-group">
                  <label>Nombre</label>
                  <input type="text" name="nombre" value="${articulo.nombre}" class="form-control" placeholder="Ingrese el nombre" />
                </div>

                <div class="form-group">
                  <label>Descripción</label>
                  <input type="text" name="descripcion" value="${articulo.descripcion}" class="form-control" placeholder="Ingrese la descripcion" />
                </div>
                
                <div class="form-group">
                  <label>Existencia</label>
                  <input type="text" name="cantidad" value="${articulo.existencia}" class="form-control" placeholder="Ingrese la existencia" />
                </div>
                
                <div class="form-group">
                  <label>Precio</label>
                  <input type="text" name="precio" value="${articulo.precio}" class="form-control" placeholder="Ingrese su correo electrónico" data-validacion-tipo="requerido|email" />
                </div>

            </div>
          </div>

          <hr class="star-primary">

          <div class="text-right">
            <button type="button" class="btn btn-default" onclick="history.go(-1)">
              Cancelar
            </button>
            <button type="submit" class="btn btn-success">
              <i class="glyphicon glyphicon-floppy-disk"></i> Guardar
            </button>
          </div>
        </fieldset>
      </form>
      <!--/Form-->
    </div>
    <!--/Body-->
  </div>
</div>
</div>
<%@include file="../layout/footer.jsp" %>
