const path = require("path");
const fs = require("fs");

function ResourcesMiddlewareFactory(config) {
  return function (request, response, next) {

  }
}

config.middleware = config.middleware || [];
config.middleware.push("resources");

config.plugins = config.plugins = config.plugins || [];
config.plugins.push(
  { "middleware:resources": ["factory", ResourcesMiddlewareFactory] },
);
