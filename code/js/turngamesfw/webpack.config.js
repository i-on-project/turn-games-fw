function delay(ms) {
    return new Promise((resolve, reject) => {
        setTimeout(resolve, ms)
    })
}

module.exports = {
    mode: "development",
    resolve: {
        extensions: [".js", ".ts", ".tsx", ".css"]
    },
    devServer: {
        port: 8000,
        historyApiFallback: true,
        compress: false, 
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                // introducing an API delay to make testing easier
                pathRewrite: async function (path, req) {
                    await delay(1000)
                    return path
                }
            }
        },
        allowedHosts: "all"
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.css$/i,                                                                                                                                                             
                use: ["style-loader", "css-loader"],                                                                                                                          
            },  
        ]
    }
}