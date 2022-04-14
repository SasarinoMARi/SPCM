require("dotenv").config();
class SQLManager {
  constructor() {
    const mysql = require('mysql');
    this.connection = mysql.createConnection({
      host     : process.env.DB_HOST,
      user     : process.env.DB_USER,
      password : process.env.DB_PW,
      database : process.env.DB_NAME
    });
  }
}


module.exports = new SQLManager().connection;