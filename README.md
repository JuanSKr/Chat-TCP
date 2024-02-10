# Chat-TCP

## Interfaz Gráfica
La interfaz gráfica ha sido creada con Swing. La aplicación cuenta con dos ventanas: una para el registro y otra para el chat.

## Base de datos
La aplicación utiliza una base de datos MySQL para almacenar la información de los usuarios y los mensajes. La base de datos se compone de dos tablas: user y message.

## Tabla user
La tabla user almacena la información de los usuarios. Cada fila representa a un usuario y tiene las siguientes columnas:  
### id:
Un número entero que se autoincrementa y sirve como identificador único para cada usuario.
### username: 
Un VARCHAR que almacena el nombre de usuario. Este valor es único para cada usuario.
### password:
Un VARCHAR que almacena la contraseña del usuario.

## Tabla message
La tabla message almacena los mensajes enviados por los usuarios. Cada fila representa un mensaje y tiene las siguientes columnas:  
### id:
Un número entero que se autoincrementa y sirve como identificador único para cada mensaje.
### sender_id:
Un número entero que hace referencia al id del usuario que envió el mensaje. Esta columna establece una relación de Foreign Key con la columna id de la tabla user.
### content:
Un VARCHAR que almacena el contenido del mensaje.
### date:
Un campo de tipo DATE que almacena la fecha y hora en que se envió el mensaje.

## Juan Castaño Gomariz