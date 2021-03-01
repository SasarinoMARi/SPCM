const request = require('request')

module.exports = async (value) => 
    new Promise((resolve, reject) => {
        request.get(value, (error, response, data) => {
            if(error) reject(error)
            else resolve(data)
        })
    })