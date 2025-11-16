// Polyfill to disable caching for Compose Resources in Extension environment
if (window.caches) { // TODO extension don't have access to Cache API, but compose use it.
    const originalOpen = window.caches.open;
    window.caches.open = function (cacheName) {
        if (cacheName === 'compose_web_resources_cache') {
            console.log("Intercepted Compose cache request in extension environment.");
            return Promise.resolve({
                match: () => Promise.resolve(undefined),
                put: () => Promise.resolve(),
                add: () => Promise.resolve(),
                addAll: () => Promise.resolve(),
                delete: () => Promise.resolve(true),
                keys: () => Promise.resolve([])
            });
        }
        return originalOpen.apply(this, arguments);
    };
}

// Polyfill to suppress WebGL debug info warnings
(function () {
    const UNMASKED_VENDOR_WEBGL = 0x9245;
    const UNMASKED_RENDERER_WEBGL = 0x9246;

    function installProxy(proto) {
        if (!proto) return;
        const originalGetParameter = proto.getParameter;
        proto.getParameter = function (parameter) {
            if (parameter === UNMASKED_VENDOR_WEBGL) return 'OpenStore Vendor';
            if (parameter === UNMASKED_RENDERER_WEBGL) return 'OpenStore Renderer';
            return originalGetParameter.apply(this, arguments);
        };
    }

    if (window.WebGLRenderingContext) installProxy(window.WebGLRenderingContext.prototype);
    if (window.WebGL2RenderingContext) installProxy(window.WebGL2RenderingContext.prototype);
})();
