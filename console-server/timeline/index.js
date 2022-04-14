const crawlers = [ require('./crawlers/quasarzone'), ]

function onDataFetched(data) {
    console.log('onDataFetched: ' + data.map(function(it) {
        return it.title;
    }));
}

crawlers.forEach(craw => {
    craw.getData(onDataFetched);
});