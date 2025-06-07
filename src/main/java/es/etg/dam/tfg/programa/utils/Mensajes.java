package es.etg.dam.tfg.programa.utils;

public class Mensajes {

    public static final String ERROR_CARGA = "Error al cargar los datos.";
    public static final String JUEGO_ELIMINADO = "Juego eliminado correctamente.";
    public static final String CONFIRMAR_ELIMINACION = "¿Estás seguro de que quieres eliminar este juego?";
    public static final String PLACEHOLDER_IMAGEN = "/placeholder.png";
    public static final String USUARIO_NO_LOGUEADO = "No se ha iniciado sesión.";
    public static final String JUEGO_AGREGADO = "¡Juego agregado a tu biblioteca!";
    public static final String SIN_RESULTADOS = "No se encontraron juegos con los filtros aplicados.";
    public static final String YA_EN_BIBLIOTECA = "El juego ya está en tu biblioteca.";
    public static final String AGREGADO_BIBLIOTECA = "¡Juego agregado a tu biblioteca!";
    public static final String ERROR_AGREGAR = "Error al agregar el juego: ";
    public static final String CONSOLA_NO_ENCONTRADA = "No se encontró la consola seleccionada.";
    public static final String SELECCION_CANCELADA = "Selección cancelada.";

    // Mensajes Biblioteca
    public static final String BIBLIOTECA_VACIA = "La biblioteca está vacía.";
    public static final String ERROR_EXPORTAR_PDF = "Error al exportar el PDF.";
    public static final String GUARDAR_BIBLIOTECA_PDF = "Guardar Biblioteca como PDF";

    // Mensajes Wishlist
    public static final String GUARDAR_WISHLIST_PDF = "Guardar Wishlist como PDF";
    public static final String WISHLIST_EXPORTADA = "Wishlist exportada correctamente.";
    public static final String ERROR_EXPORTAR_WISHLIST = "Error al exportar la wishlist: ";
    public static final String WISHLIST_VACIA = "No hay juegos en la wishlist para exportar.";
    public static final String ERROR_COMPANIA = "No se pudo completar la compañía: ";
    public static final String ERROR_TIENDA = "No se pudo cargar tiendas.";

    // Mensajes Búsqueda
    public static final String ERROR_CARGAR_PLATAFORMAS = "Error al cargar plataformas.";
    public static final String ERROR_CARGAR_GENEROS = "Error al cargar géneros.";
    public static final String SIN_RESULTADOS_API = "No se encontraron juegos con los filtros aplicados.";
    public static final String ERROR_CARGAR_RESULTADOS = "Error al cargar resultados: ";
    public static final String JUEGO_YA_EN_BIBLIOTECA = "El juego ya está en tu biblioteca.";
    public static final String JUEGO_MOVIDO_BIBLIOTECA = "Juego movido de wishlist a tu biblioteca.";
    public static final String JUEGO_AGREGADO_BIBLIOTECA = "Juego agregado a tu biblioteca"; // Se usa en
                                                                                             // WishlistControlador
                                                                                             // también
    public static final String ERROR_AGREGAR_JUEGO = "Error al agregar el juego: "; // Se usa en WishlistControlador
                                                                                    // también
    public static final String JUEGO_YA_EN_WISHLIST = "El juego ya está en tu wishlist.";
    public static final String JUEGO_AGREGADO_WISHLIST = "¡Juego agregado a tu wishlist!";
    public static final String ERROR_AGREGAR_WISHLIST = "Error al agregar a wishlist: ";
    public static final String ERROR_INFORMACION_DETALLADA = "Error al obtener información detallada del juego: ";

    // Mensajes Log In
    public static final String LOGIN_CAMPOS_VACIOS = "Debes introducir usuario y contraseña.";
    public static final String LOGIN_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";
    public static final String LOGIN_ERROR_GENERAL = "Error al iniciar sesión: ";

    // Mensajes Registro
    public static final String REGISTRO_ERROR_GENERAL = "Error al registrar usuario: ";

    // Mensajes Estadísticas
    public static final String GENERO_MAS_FRECUENTE = "Género más frecuente: %s";
    public static final String CONSOLA_MAS_FRECUENTE = "Consola más frecuente: %s";
    public static final String JUEGOS_FISICOS = "Juegos físicos: %d";
    public static final String JUEGOS_DIGITALES = "Juegos digitales: %d";
    public static final String SIN_DATOS = "(sin datos)";

    // Mensajes NuevaUbicacion
    public static final String LUGAR_OBLIGATORIO = "El lugar es obligatorio.";

    // PDF
    // BibliotecaControlador, WishlistControlador
    public static final String DESCRIPCION_PDF = "Archivos PDF (*.pdf)";
    public static final String EXTENSION_PDF = "*.pdf";

    // Títulos pantallas
    // BibliotecaControlador, ControladorBusqueda, Log In Controlador
    public static final String TITULO_WISHLIST = "Wishlist";
    public static final String TITULO_INICIO_SESION = "Iniciar sesión";
    public static final String TITULO_ESTADISTICAS = "Estadísticas";
    public static final String TITULO_AGREGAR_JUEGOS = "Agregar juegos";
    public static final String TITULO_BIBLIOTECA = "Biblioteca";
    public static final String TITULO_REGISTRO = "Registro de Usuario";

    // Texto de botones, labels, comboboxes...

    // BibliotecaControlador, WishlistControlador, ControladorBusqueda,
    // EstadisticasControlador
    public static final String ELIMINAR = "Eliminar";
    public static final String NOMBRE = "Nombre: ";
    public static final String CONSOLA = "Consolas: ";
    public static final String CONSOLA2 = "Consolas";
    public static final String GENERO = "Géneros: ";
    public static final String EMPRESA = "Empresa: ";
    public static final String VACIO = "N/A";
    public static final String ESTILO_FICHA = "-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9; -fx-padding: 10;";
    public static final String ESTILO_BUSQUEDA_SIN_RESULTADOS = "-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 10;";
    public static final String PAGINA = "Página ";
    public static final String DE = " de ";
    public static final String TODOS = "Todos";
    public static final String TODAS = "Todas";
    public static final String NINGUNO = "Ninguno";
    public static final String FILTRO_BIBLIOTECA_A_Z = "Nombre A-Z";
    public static final String FILTRO_BIBLIOTECA_Z_A = "Nombre Z-A";
    public static final String FILTRO_BIBLIOTECA_RECIENTE = "Fecha más reciente";
    public static final String FILTRO_BIBLIOTECA_ANTIGUA = "Fecha más antigua";
    // ControladorBusqueda
    public static final String AGREGAR_WISHLIST = "Agregar a wishlist";

    // Wishlist Controlador
    public static final String AGREGAR_BIBLIOTECA = "Agregar a Biblioteca";
    public static final String TIENDAS_DISPONIBLES = "Tiendas disponibles:";
    public static final String ESTILO_NOMBRE_WISHLIST = "-fx-font-size: 16px; -fx-font-weight: bold;";
    public static final String ESTILO_TARJETA = "-fx-border-color: #ccc; -fx-padding: 10;";

    // FichaJuego Controlador
    public static final String FECHA = "Fecha: ";
    public static final String FORMATO = "Formato: ";
    public static final String FORMATO_FISICO = "Físico";
    public static final String FORMATO_DIGITAL = "Digital";
    public static final String UBICACION = "Ubicación: ";

    // Api RAWG
    // WishlistControlador, ControladorBusqueda
    public static final String API_RESULTADOS = "results";
    public static final String API_ID = "id";
    public static final String API_HTTPS = "https://";
    public static final String API_STORE = "store";
    public static final String API_STORES = "stores";
    public static final String API_NOMBRE = "name";
    public static final String API_DOMINIO = "domain";

    // ControladorBusqueda
    public static final String API_GENEROS = "genres";
    public static final String API_DESARROLLADORES = "developers";
    public static final String API_LANZADO = "released";
    public static final String API_IMAGEN = "background_image";
    public static final String API_PLATAFORMA = "platform";
    public static final String API_PLATAFORMAS = "platforms";
    public static final String API_SIGUIENTE = "next";

    // Enlaces API
    public static final String ENLACE_JUEGOS = "https://api.rawg.io/api/games/";
    public static final String ENLACE_GENEROS = "https://api.rawg.io/api/genres";
    public static final String ENLACE_PLATAFORMAS = "https://api.rawg.io/api/platforms";
}
