CREATE DATABASE IF NOT EXISTS javaweb_crud;
USE javaweb_crud;
 
--
-- Estructura de tabla para la tabla `articulos`
--
 
CREATE TABLE IF NOT EXISTS `articulos` (
  `id` int(10) NOT NULL,
  `codigo` varchar(30) COLLATE utf8_spanish2_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8_spanish2_ci NOT NULL,
  `descripcion` varchar(50) COLLATE utf8_spanish2_ci NOT NULL,
  `existencia` double NOT NULL,
  `precio` double NOT NULL
) ;
 
--
-- Volcado de datos para la tabla `articulos`
--
 
INSERT INTO `articulos` (`id`, `codigo`, `nombre`, `descripcion`, `existencia`, `precio`) VALUES
(5, 'EC001', 'ESFERO ROJO', 'ESFERO BORRABLE', 40, 0.65),
(4, 'EC002', 'ESFERO NEGRO', 'ESFERO BORRABLE', 30, 0.65),
(6, 'FA001', 'FOLDER ARCHIVADOR AZ OFICIO', 'FOLDER CARTÓN', 10, 2.79),
(7, 'SM001', 'SOBRE MANILA ', 'SOBRE MANILA OFICIO', 15, 0.1);
 
--
-- Índices para tablas volcadas
--
 
--
-- Indices de la tabla `articulos`
--
ALTER TABLE `articulos`
  ADD PRIMARY KEY (`id`);
 
--
-- AUTO_INCREMENT de las tablas volcadas
--
 
--
-- AUTO_INCREMENT de la tabla `articulos`
--
ALTER TABLE `articulos`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
