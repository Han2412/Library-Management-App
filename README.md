
# Summary

App Library Management use Android Studio & JavaScript WebSocket API & MySQL Workbench

# Getting Started

Clone the project repository: `https://github.com/Han2412/Library-Management-App`

## Database

1. Import `DB.sql` to MySQL Workbench
2. Change the Database Information in Server `database.js`
   
```javascript
// Function to establish a MySQL connection
async function connectToDatabase() {
    try {
      if (!connection) {
        connection = await mysql.createConnection({
          host: 'localhost',
          user: 'root', // change
          password: '123456', // change
          database: 'quanlithuvien', // change
        });
        console.log('Connected to MySQL database');
      }
      return connection;
    } catch (error) {
      console.error('Error connecting to MySQL database:', error.message);
      throw error;
    }
}
```

## Server

1. Navigate to the project directory: `cd 'Server'`
2. Install dependencies: `npm install`
3. Run the application: `npm start`
   
--> The Server will run on port 3500.

## Client

1. Import `Project` to Android Studio
2. Build & Run App

# Testing on Mobile Device

We will testing on Mobile Device using same wifi with Server

1. Run Server
2. Get IPv4 Address of Server device
3. Replace `SERVER_URL` with IPv4 Address of Server in [Constans.java](Project/app/src/main/java/com/example/project/utils/Constants.java)
```java
// main ip
public static final String SERVER_URL = "ws://192.168.1.18:3500"; // Replace with your server IP or hostname
```
4. Run the Device Test
