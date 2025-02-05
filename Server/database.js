const mysql = require('mysql2/promise');
const fs = require('fs');
const { countBy } = require('lodash');
let connection;

// Function to establish a MySQL connection
async function connectToDatabase() {
    try {
        if (!connection) {
            connection = await mysql.createConnection({
                host: 'localhost',
                user: 'root',
                password: 'zed123456@',
                database: 'quanlithuvien',
            });
            console.log('Connected to MySQL database');
        }
        return connection;
    } catch (error) {
        console.error('Error connecting to MySQL database:', error.message);
        throw error;
    }
}

async function connectionToDataBase() {
    try {
        connection = await mysql.createConnection({
            host: 'localhost',
            user: 'root',
            password: '141705',
            database: 'quanlithuvien'
        })
        console.log('connection to database');
        return connection;

    } catch (error) {
        console.error('error connecting to MySql: ', error);
        throw error;
    }
}

async function login(username, password) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Query to check login credentials
        const query = 'SELECT * FROM quanlithuvien.taikhoan WHERE username = ? AND password = ?';

        // Execute the query with parameters
        const [rows, fields] = await connection.execute(query, [username, password]);

        // Check if any rows were returned
        const loginSuccessful = rows.length > 0;

        // Close the connection when done
        // await connection.end();
        // console.log('Connection closed');

        return loginSuccessful;
    } catch (error) {
        console.error('An error occurred:', error.message);
        return false; // Return false in case of an error
    }
}

async function getAllBooks() {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Query to get all rows from the 'sach' table
        const query = 'SELECT * FROM quanlithuvien.sach';

        // Execute the query
        const [rows, fields] = await connection.execute(query);

        // Get string value of image(BLOB) in mysql workbench
        rows.forEach(row => {
            row.image = Buffer.from(row.image, 'string').toString();
        });

        // Return the result
        return rows;
    } catch (error) {
        console.error('An error occurred while getting all books:', error.message);
        throw error;
    }
}

async function getAllReceipts() {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Query to get all rows from the 'sach' table
        const query = 'SELECT * FROM quanlithuvien.phieu';

        // Execute the query
        const [rows, fields] = await connection.execute(query);

        // Return the result
        return rows;
    } catch (error) {
        console.error('An error occurred while getting all receipt:', error.message);
        throw error;
    }
}

async function order(receipt) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        const query1 = `INSERT INTO quanlithuvien.phieu 
      (id, id_books, status, first_name, last_name, gender, email, phone, date_start, date_return) 
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;

        const query2 = `UPDATE quanlithuvien.sach 
    SET inventory_quantity = inventory_quantity - 1 
    WHERE id = ?`;

        const query3 = `INSERT INTO quanlithuvien.ctpm 
    (idpm,idsach,ngaymuon,trangthai,soluong) 
    VALUES (?, ?, ?, ?, ?)`;

        const { id, id_books, status, first_name, last_name, gender, email, phone, date_start, date_return } = receipt;

        // Execute the query with parameters
        await connection.execute(query1, [id, id_books, status, first_name, last_name, gender, email, phone, date_start, date_return]);

        let arrIds = receipt.id_books.split(",");
        // Loop through each receipt and execute the query
        for (const arrId of arrIds) {
            // Execute the update query with parameter
            await connection.execute(query2, [arrId]);
        }

        // Đếm số lượng sách mỗi loại
        const sach_dem = countBy(arrIds);

        for (const arrId of arrIds) {
            // Execute the update query with parameter
            await connection.execute(query3, [receipt.id, arrId, receipt.date_start, receipt.status, sach_dem[arrId]]);
        }

        // Return the result
        return true;
    } catch (error) {
        console.error('An error occurred while getting all receipt:', error.message);
        throw false;
    }
}

async function addUser(user) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        const query = `INSERT INTO quanlithuvien.taikhoan 
      (id, name, username, password, email) 
      VALUES (?, ?, ?, ?, ?)`;

        const { id, name, username, password, email } = user;

        // Execute the query with parameters
        await connection.execute(query, [id, name, username, password, email]);

        // Return the result
        return true;
    } catch (error) {
        console.error('An error occurred while adding user:', error.message);
        throw false;
    }
}

async function checkUsernameExists(username) {
    try {
        const connection = await connectToDatabase();

        // Query to check if username exists
        const query = 'SELECT * FROM quanlithuvien.taikhoan WHERE username = ?';

        // Execute the query with parameters
        const [rows, fields] = await connection.execute(query, [username]);

        // Check if any rows were returned
        if (rows.length > 0) {
            // Return user information if username exists
            return rows[0];
        } else {
            // Return null if username doesn't exist
            return null;
        }
    } catch (error) {
        console.error('An error occurred:', error.message);
        return null; // Return null in case of an error
    }
}

async function changePassword(username, newPassword) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Construct the SQL query to update the password
        const query = `UPDATE quanlithuvien.taikhoan 
                       SET password = ? 
                       WHERE username = ?`;

        // Execute the query with parameters
        await connection.execute(query, [newPassword, username]);

        // Return true if successful
        return true;
    } catch (error) {
        console.error('An error occurred while changing password:', error.message);
        throw false;
    }
}

async function addBook(receipt) {
    try {
        const connection = await connectToDatabase();

        const query = `INSERT INTO quanlithuvien.sach 
    (id, name, summary, name_author, inventory_quantity, image, category, date_add, price)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;

        const query2 = `INSERT INTO quanlithuvien.sachhai 
    (idsach, tensach, gia, tonkho)
    VALUES (?, ?, ?, ?)`;



        const { id, name, summary, name_author, inventory_quantity, image, category, date_add, price } = receipt;

        await connection.execute(query, [id, name, summary, name_author, inventory_quantity, image, category, date_add, price]);

        await connection.execute(query2, [id, name, price, inventory_quantity]);
        return true

    } catch (error) {
        console.error('An error occurred while add book:', error.message);
        throw false;
    }
}

async function addCategory(receipt) {
    try {
        const connection = await connectToDatabase();

        const query = `INSERT INTO quanlithuvien.theloai (name) VALUES (?)`;

        const { name } = receipt;

        const [result] = await connection.execute(query, [name]);

        if (result.affectedRows === 1) {
            console.log('Add category successfully');
            return true;
        } else {
            console.error('Failed to add category');
            return false;
        }

    } catch (error) {
        console.error('An error occurred while add category:', error.message);
        throw false;
    }
}

async function getAllCategories() {
    try {
        const connection = await connectToDatabase();

        const query = `SELECT * FROM quanlithuvien.theloai`;

        const [rows, fields] = await connection.execute(query);

        return rows;
    } catch (error) {
        console.error('An error occurred while get all categories:', error.message);
        throw false;
    }
}

async function updateReceiptStatus(Receipt) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Query to update the status of the receipt
        const query = 'UPDATE quanlithuvien.phieu SET status = ? WHERE id = ?';
        const { id, status } = Receipt;
        // Execute the query
        const [result] = await connection.execute(query, [status, id]);

        // Check if the query was successful
        if (result.affectedRows === 1) {
            console.log('Receipt status updated successfully');
            return true;
        } else {
            console.error('Failed to update receipt status');
            return false;
        }
    } catch (error) {
        console.error('An error occurred while updating receipt status:', error.message);
        throw error;
    }
}


async function updateReceipt(Receipt) {
    try {
        // Connect to the database
        const connection = await connectToDatabase();

        // Query to update the status of the receipt
        const query = 'UPDATE quanlithuvien.phieu SET first_name = ?, last_name = ?, gender = ?, email = ?, phone = ?  WHERE id = ?';
        const { id, first_name, last_name, gender, email, phone } = Receipt;
        // Execute the query
        const [result] = await connection.execute(query, [first_name, last_name, gender, email, phone, id]);

        // Check if the query was successful
        if (result.affectedRows === 1) {
            console.log('Receipt status updated successfully');
            return true;
        } else {
            console.error('Failed to update receipt status');
            return false;
        }
    } catch (error) {
        console.error('An error occurred while updating receipt status:', error.message);
        throw error;
    }
}

async function getDataStatistic(dayA, dayB) {
    const connection = await connectToDatabase();

    const query = `SELECT 
    s.tensach,
            SUM(CASE WHEN ctpm.trangthai = 'Borrowing' 
                THEN ctpm.soluong 
                    WHEN ctpm.trangthai = 'Return' 
                THEN ctpm.soluong ELSE 0 END) AS tongluotmuon,
            SUM(CASE WHEN ctpm.trangthai = 'Return' 
                THEN ctpm.soluong ELSE 0 END) AS tongluottra,
            SUM(ctpm.soluong * s.gia) AS doanhthu,
    s.tonkho
    FROM 
        quanlithuvien.ctpm AS ctpm
    JOIN 
        quanlithuvien.sachhai AS s ON ctpm.idsach = s.idsach
    WHERE 
        ctpm.ngaymuon BETWEEN ? AND ?
    GROUP BY 
        ctpm.idsach
    `
    const [row, filed] = await connection.execute(query, [dayA, dayB]);
    console.log('DataStatistic from database:', row);

    return row;
}

async function getDataBarChart(dayA, dayB) {
    const connection = await connectToDatabase();

    const query = `SELECT s.tensach, SUM(c.soluong) AS tongluotmuon
    FROM quanlithuvien.ctpm AS c
    JOIN quanlithuvien.sachhai AS s ON c.idsach = s.idsach
    WHERE c.ngaymuon BETWEEN ? AND ?
    AND c.trangthai IN ('Borrowing', 'Return')
    GROUP BY s.tensach
    ORDER BY tongluotmuon DESC
    LIMIT 3; `;


    const [row, field] = await connection.execute(query, [dayA, dayB]);
    console.log('Data from database:', row);

    return row;
}

// Export the connectToDatabase function
module.exports = {
    connectToDatabase, login, getAllBooks, getAllReceipts,
    order, addBook, addUser, checkUsernameExists, changePassword,
    updateReceiptStatus, updateReceipt, getDataBarChart, getDataStatistic,
    addCategory, getAllCategories
};