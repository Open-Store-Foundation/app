const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');

config.resolve = {
    fallback: {
        fs: false,
    },
}

config.plugins = [
    new NodePolyfillPlugin({
        excludeAliases: ['console'],
    }),
]
