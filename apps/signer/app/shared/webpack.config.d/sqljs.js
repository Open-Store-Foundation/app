const CopyWebpackPlugin = require('copy-webpack-plugin');
//const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');

config.resolve = config.resolve || {};
config.resolve.fallback = config.resolve.fallback || {};
config.resolve.fallback.fs = false;
config.resolve.fallback.crypto = false;
config.resolve.fallback.path = false;

config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.wasm',
                to: 'sql-wasm.wasm'
            }
        ]
    }),
    //    new NodePolyfillPlugin({
    //        excludeAliases: ['console'],
    //    }),
);

// Fix for Chrome Extension: Filenames starting with "_" are reserved
config.output = config.output || {};
config.output.chunkFilename = 'chunk-[contenthash].js';
