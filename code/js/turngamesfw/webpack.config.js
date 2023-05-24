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
                pathRewrite: async function (path, req) {
                    path = path.slice(4) // Cut "/api" to make request to localhost:8080 without "/api"
                    return path
                }
            }
        },
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