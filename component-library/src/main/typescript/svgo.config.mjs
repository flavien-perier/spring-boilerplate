export default {
    multipass: true,
    js2svg: {
        pretty: false,
    },
    plugins: [
        'preset-default',
        'removeDimensions',
        'removeScripts',
        'removeTitle',
    ],
};
