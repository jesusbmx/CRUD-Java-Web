$(document).ready(function () {
    initValidateForm();
    initDatePicker();
    initUploadImg();
});
// Inicia el caledario : bootstrap.
function initDatePicker() {    
    $('.date-picker').datetimepicker({
        language: 'es',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0
    });
}
// Inicia la validación de formularios : jquery-validator.
function initValidateForm() {
    $("#form").submit(function () {
        return $(this).validate();
    });
}
// Inicia el lector de imagenes.
function initUploadImg() {
    $('#upload-file').change(function (e) {
        var file = e.target.files[0];

        if (! file.type.match(/image.*/i) ) {
            alert('seleccione una imagen');
            return false;
        } 
        
        var lector = new FileReader();
        lector.onload = function (e) {
            $('#upload-img').attr('src', e.target.result);
        };
        lector.readAsDataURL(file);
        
        return true;
    });
}