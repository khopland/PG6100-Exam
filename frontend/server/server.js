const express = require('express');
const path = require('path');

const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

if(process.env.DEV){
    const proxiedServer = 'http://localhost:80'
    app.use("/api/*", createProxyMiddleware({ target: proxiedServer, changeOrigin: true }))
}

//needed to server static files, like HTML, CSS and JS.
app.use(express.static(path.resolve(__dirname, '..', 'dist')));

//handling 404
app.use((req, res, next) => {
    res.sendFile(path.resolve(__dirname, '..', 'dist', 'index.html'));
});

const port = process.env.PORT || 3000;

app.listen(port, () => {
    console.log('Started Frontend NodeJS server on port ' + port);
});