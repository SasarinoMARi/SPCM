const axios = require("axios");
const cheerio = require("cheerio");

module.exports = {
    getHtml: async function () {
        try {
            return await axios.get(`https://quasarzone.com/bbs/qb_saleinfo`);
        } catch (error) {
            console.error(error);
        }
    },
    getData: function (callback) {
        this.getHtml()
            .then(html => {
                let column = [];
                const $ = cheerio.load(html.data);
                const list = $("div.market-type-list table tbody tr");
                console.log('length:' + list.length);
                list.each(function (i, elem) {
                    var content_head = $(this).find('td:nth-child(2) div div:nth-child(2)');
                    column[i] = {
                        state: content_head.find('p span.label').text().trim(),
                        title: content_head.find('p a span:nth-child(1)').text().trim(),
                        url: content_head.find('p a.subject-link').attr('href')
                    }
                })
                const data = column.filter(n => n.title);
                return data;
            })
            .then(data => {
                //console.log(data);
                callback(data);
            });
    }
}