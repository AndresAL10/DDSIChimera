package chimeraddsi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Scanner;

public class ChimeraDDSI {
    
    public static void mostrarMenu() {
        System.out.println("\n Seleccione una opcion del menu:   ");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println("|   1 - Borrar tablas existentes y creacion de tablas rellenando con 10 filas la tabla stock  |");
        System.out.println("|   2 - Dar de alta nuevo pedido                                                              |");
        System.out.println("|   3 - Mostrar contenido de las tablas                                                       |");
        System.out.println("|   4 - Salir y cerrar conexion                                                               |");
        System.out.println("-----------------------------------------------------------------------------------------------");
    }

    public static void mostrarMenuInterno() {
        System.out.println("\n ¿Que desea hacer?  ");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println("|   1 - Añadir detalle de producto                                                            |");
        System.out.println("|   2 - Eliminar todos los detalles de producto                                               |");
        System.out.println("|   3 - Cancelar pedido                                                                       |");
        System.out.println("|   4 - Finalizar pedido                                                                      |");
        System.out.println("-----------------------------------------------------------------------------------------------");
    }      

    public static void ejecutarQuery(Connection connection, String query) throws SQLException {
        Statement state = connection.createStatement();
        state.executeQuery(query);
        state.closeOnCompletion();
    }

    public static void mostrarTablas(Connection conexion)throws SQLException {  
        try {
            Statement stmt = conexion.createStatement();            
            ResultSet res = stmt.executeQuery("SELECT *FROM Stock");
            
            System.out.println("\nTabla Stock: \n");         
            System.out.println("Cproducto \tCantidad");
            //Hacemos un While para recorrer toda la tabla y asi poder sacar todos los registros de la tabla Stcok
            while (res.next()) {
                /*Se muestra los datos que queremos sacar por consola indicandole:
                        El tipo de dato (int,String...) de cada campo
                        El nombre de los campos de la tabla entre comillas doble " "
                */
                System.out.println(res.getInt("Cproducto") + "\t\t" + res.getInt("cantidad") + "\n");
            }
            res.close();
            
            System.out.println("\nTabla Pedido: \n");
            res = stmt.executeQuery("SELECT *FROM Pedido");
            System.out.println("Cpedido \tCcliente \tFechaPedido");            
            //Hacemos un While para recorrer toda la tabla y asi poder sacar todos los registros de la tabla Pedido
            while (res.next()) {
                /*Se muestra los datos que queremos sacar por consola indicandole:
                        El tipo de dato (int,String...) de cada campo
                        El nombre de los campos de la tabla entre comillas doble " "
                */
                System.out.println(res.getInt("Cpedido") + "\t\t" + res.getInt("Ccliente") + "\t\t" + res.getDate("FechaPedido")+ "\n");
            }
            res.close();
            
            System.out.println("\nTabla DetallesPedido: \n");
            res = stmt.executeQuery("SELECT *FROM DetallesPedido");
            System.out.println("Cpedido \tCproducto \tCantidad");
            //Hacemos un While para recorrer toda la tabla y asi poder sacar todos los registros de la tabla Stcok
            while (res.next()) {
                /*Se muestra los datos que queremos sacar por consola indicandole:
                        El tipo de dato (int,String...) de cada campo
                        El nombre de los campos de la tabla entre comillas doble " "
                */
                System.out.println(res.getInt("Cpedido") + "\t\t" + res.getInt("Cproducto") + "\t\t" + res.getInt("cantidad") + "\n");
            }
            res.close();
            
        } catch (SQLException ex) {
           System.out.println(ex);
           System.out.println("Error al ejecutar el metodo MostrarTablas");
           System.err.format(" SQL State; %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        
    }

    public static void main(String ar[]) {
        //Datos necesarios para realizar la conexion a la base de datos
        System.out.println("Conectando a la base de datos...");
        String driver = "jdbc:oracle:thin:@";
        String server = "oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
        String user = "x6046787";
        String psw = "x6046787";

        Scanner reader = new Scanner(System.in);
        
        int opcion;
        int opcion2;
        int codStock;
        int cantidadIntroducida;

        try (Connection conn = DriverManager.getConnection(driver + server, user, psw)) {

            if (conn != null) {
                //Por defecto esta inicializado a true y para que sea posible hacer commit donde indiquemos, lo seteamos a false
                conn.setAutoCommit(false);
                
                System.out.println("Connected database");
                
                do {
                    //Desplegamos el menu con las 4 posibles opciones
                    mostrarMenu();
                    //Leemos el siguiente entero por teclado
                    opcion = reader.nextInt();
                    Statement estado2 = conn.createStatement();

                    switch (opcion) {
                        //En este caso realizamos el borrado y la creacion de las tablas y añadimos 10 tuplas a la tabla Stock
                        case 1:
                            System.out.println("\nBorrando tablas DetallePedido, Stock y Pedido...");

                            try {
                                ejecutarQuery(conn, "DELETE FROM DetallesPedido");
                                ejecutarQuery(conn, "DROP TABLE DetallesPedido");

                            } catch (Exception e) {
                                System.out.println("No existe la tabla DetallesPedido");
                            }

                            try {
                                ejecutarQuery(conn, "DELETE FROM Stock");
                                ejecutarQuery(conn, "DROP TABLE Stock");

                            } catch (Exception e) {
                                System.out.println("No existe la tabla Stock");
                            }

                            try {
                                ejecutarQuery(conn, "DELETE FROM Pedido");
                                ejecutarQuery(conn, "DROP TABLE Pedido");

                            } catch (Exception e) {
                                System.out.println("No existe la tabla Pedido");
                            }

                            System.out.println("\nCreando tablas DetallePedido, Stock y Pedido...");

                            ejecutarQuery(conn, "CREATE TABLE Pedido(Cpedido int PRIMARY KEY, Ccliente int UNIQUE, FechaPedido  date)");

                            ejecutarQuery(conn, "CREATE TABLE Stock(Cproducto int PRIMARY KEY,cantidad int)");

                            ejecutarQuery(conn, "CREATE TABLE DetallesPedido(Cpedido REFERENCES Pedido(Cpedido), Cproducto REFERENCES Stock(Cproducto), Cantidad int, PRIMARY KEY(Cpedido,Cproducto))");

                            System.out.println("\nInsertando 10 tuplas en Stock");

                            ejecutarQuery(conn, "INSERT INTO Stock VALUES( 1,20)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (2,10)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (3,15)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (4,54)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (5,45)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (6,2)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (7,28)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (8,42)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (9,23)");
                            ejecutarQuery(conn, "INSERT INTO Stock VALUES (10,45)");

                            //Una vez tenemos la todo inicializado es un buen momento para hacer un commit
                            conn.commit();

                            break;
                        case 2:                                                       
                            //En la tabla se han guardado los codigos tipo int
                            int cliente=0;
                            int producto=0;
                            
                            //Creamos un punto intermedio al que poder hacer rollback en caso de que cancelemos el pedido
                            Savepoint guardadoCancelarPedido = conn.setSavepoint();
                            try {
                                System.out.println("\nDar de alta nuevo pedido.");
                                System.out.println("Inserte codigo del cliente : ");
                                cliente = reader.nextInt();
                                System.out.println("Inserte codigo del producto : ");
                                producto = reader.nextInt();

                                //CURRENT_TIMESTAMP funcion para tiempo actual   
                                ejecutarQuery(conn, "INSERT INTO PEDIDO VALUES ( " + producto + " , " + cliente + " , CURRENT_TIMESTAMP )");
                                //La fecha se incluyo internamente, insertando asi la fecha actual del sistema con CURRENT_DATE()
                            } catch (Exception e) {
                                System.out.println("No ha introducido los datos correctamente:\n \"INSERT INTO PEDIDO VALUES ( " + producto + " , " + cliente + " , CURRENT_TIMESTAMP) \"");
                            }
                            
                            //Creamos un punto intermedio al que poder hacer rollback en caso de que eliminemos los detalles del pedido pero queramos seguir con el pedido
                            Savepoint guardadoEliminarDetallesPedido = conn.setSavepoint();                     
                            
                            do {
                                //Una vez creado el pedido mostramos el segundo menu y pedimos al usuario que seleccione una opcion
                                mostrarMenuInterno();
                                opcion2 = reader.nextInt();

                                switch (opcion2) {
                                    case 1:
                                        
                                        System.out.println("\nAÑADIR DETALLESPEDIDO.");
                                        System.out.println("Inserte codigo del stock : ");
                                        codStock = reader.nextInt();
                                        System.out.println("Inserte cantidad: ");
                                        cantidadIntroducida = reader.nextInt();                                       
                                        
                                        //Obtenemos la cantidad actual de stock que hay disponible para ese producto
                                        ResultSet valor = estado2.executeQuery("SELECT cantidad FROM Stock WHERE CPRODUCTO = " + codStock);
                                        int cantidadStock = 0;
                                        if (valor.next()) {
                                            cantidadStock = valor.getInt("cantidad");
                                        }
                                        valor.close();
                                        //Comprobamos que la cantidad solicitada es menor o igual que la cantidad disponible de stock
                                        if (cantidadIntroducida <= cantidadStock) {                                                                                       
                                            try {   
                                                //Ejecuto un delete donde lo que quiero eliminar esta identificado por producto que es cpedido y codStock que es cproducto   
                                                ejecutarQuery(conn, "INSERT INTO DETALLESpedido VALUES ( " + producto + " , " + codStock + " , " + cantidadIntroducida + "  ) ");
                                                
                                                //Calculamos la cantidad actualizada de stock disponible
                                                int nuevaCantidad = cantidadStock - cantidadIntroducida;                                                
                                                //Actualizamos la tabla de Stock con la nueva cantidad calculada
                                                ejecutarQuery(conn, "UPDATE Stock SET cantidad = ( "  + nuevaCantidad + " ) WHERE CPRODUCTO = " + codStock);
                                            } catch (Exception e) {
                                                System.out.println(e);
                                            }
                                        }else{
                                            //En caso de que la cantidad solicitada sea mayor que la de stock disponible, devolvemos un mensaje de error y no actualizamos la tabla Stock
                                            System.out.println("\nNo hay suficiente cantidad disponible en stock.");
                                        }
                                        mostrarTablas(conn);
                                        break;
                                    case 2:
                                        //Volvemos al estado justo despues de dar de alta el pedido
                                        conn.rollback(guardadoEliminarDetallesPedido);
                                        mostrarTablas(conn);
                                        System.out.println("\nDetalles del pedido eliminados");
                                        break;
                                    case 3:
                                        //Volvemos al estado justo de antes de dar de alta un nuevo pedido
                                        conn.rollback(guardadoCancelarPedido);
                                        mostrarTablas(conn);
                                        System.out.println("\nPedido cancelado");
                                        break;
                                    case 4:
                                        conn.commit();
                                        mostrarTablas(conn);
                                        break;
                                    default:
                                        System.out.println("\nOpcion no valida, vuelva a seleccionar una opcion:\n");
                                        

                                }
                            } while (opcion2 != 4 && opcion2 != 3);

                        case 3:
                            //Mostramos el contenido de todas las tablas
                            mostrarTablas(conn);
                               break;
                        case 4:
                            //Salimos y cerramos la conexion
                            System.out.println("\nSaliendo del sistema y cerrando la conexion...");
                            conn.close();
                    }

                } while (opcion != 4 );

            } else {

                System.out.println("Failed to make connection");

            }

        } catch (SQLException e) {
            System.out.println(e);
            System.err.format("1 SQL State; %s\n%s", e.getSQLState(), e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
