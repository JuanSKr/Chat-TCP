# Chat-TCP

## Introduction
Chat-TCP is a Java application designed for real-time communication over a TCP connection. This project implements a graphical user interface (GUI) using Swing, allowing users to register, log in, and exchange messages seamlessly.

## Features
- **Graphical User Interface**: The application features an intuitive GUI built with Swing, providing a user-friendly experience.
- **Secure User Authentication**: Users can securely register and log in using unique usernames and passwords.
- **MySQL Database Integration**: Chat-TCP utilizes a MySQL database to store user information and messages efficiently.
- **Message Exchange**: Users can send and receive messages in real-time, facilitating seamless communication.

## Installation
To run Chat-TCP locally, follow these steps:
1. Clone the repository to your local machine.
2. Ensure you have MySQL installed, and import the provided database file located in the `db` folder.
3. Compile the Java files using any Java compiler.
4. Run the application and start chatting!

## Database Schema
The MySQL database consists of two tables: `user` and `message`.

### Table `user`
- **id**: An auto-incremented integer serving as a unique identifier for each user.
- **username**: A VARCHAR field storing the unique username for each user.
- **password**: A VARCHAR field storing the hashed password for user authentication.

### Table `message`
- **id**: An auto-incremented integer serving as a unique identifier for each message.
- **sender_id**: An integer referencing the id of the user who sent the message, establishing a Foreign Key relationship with the `id` column of the `user` table.
- **content**: A VARCHAR field storing the message content.
- **date**: A DATE field storing the timestamp when the message was sent.

## About This Project
Chat-TCP is a project developed for the Programming of Services and Processes course. It aims to provide a practical implementation of TCP communication while integrating various programming concepts.
