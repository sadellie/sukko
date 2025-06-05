const http = require("http");
const fs = require("fs");
const path = require("path");
const url = require("url");

const PORT = 3000;
const CACHE_DURATION = 2 * 60 * 60; // 2 hours in seconds
const PUBLIC_DIR = "./public"; // Change this to your folder path

const server = http.createServer((req, res) => {
  // Parse the URL
  const parsedUrl = url.parse(req.url);
  let pathname = parsedUrl.pathname;

  // Default to index.html if root path
  if (pathname === "/") {
    pathname = "/index.html";
  }

  // Get the full file path
  const filePath = path.join(PUBLIC_DIR, pathname);

  // Check if file exists
  fs.stat(filePath, (err, stats) => {
    if (err) {
      if (err.code === "ENOENT") {
        res.writeHead(404, { "Content-Type": "text/plain" });
        res.end("File not found");
      } else {
        res.writeHead(500, { "Content-Type": "text/plain" });
        res.end("Server error");
      }
      return;
    }

    // Check if it's a file (not directory)
    if (!stats.isFile()) {
      res.writeHead(403, { "Content-Type": "text/plain" });
      res.end("Forbidden");
      return;
    }

    // Set cache control headers
    res.setHeader("Cache-Control", `public, max-age=${CACHE_DURATION}`);
    res.setHeader(
      "Expires",
      new Date(Date.now() + CACHE_DURATION * 1000).toUTCString()
    );

    // Get file extension for content type
    const ext = path.extname(filePath).toLowerCase();
    const contentTypes = {
      ".html": "text/html",
      ".css": "text/css",
      ".js": "application/javascript",
      ".json": "application/json",
      ".png": "image/png",
      ".jpg": "image/jpeg",
      ".jpeg": "image/jpeg",
      ".gif": "image/gif",
      ".svg": "image/svg+xml",
      ".ico": "image/x-icon",
      ".txt": "text/plain",
    };

    const contentType = contentTypes[ext] || "application/octet-stream";
    res.setHeader("Content-Type", contentType);

    // Stream the file
    const stream = fs.createReadStream(filePath);
    stream.pipe(res);

    stream.on("error", (error) => {
      res.writeHead(500, { "Content-Type": "text/plain" });
      res.end("Error reading file");
    });
  });
});

server.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
  console.log(`Serving files from: ${path.resolve(PUBLIC_DIR)}`);
  console.log(`Cache control set to 2 hours (${CACHE_DURATION} seconds)`);
});

// Handle server errors
server.on("error", (error) => {
  if (error.code === "EADDRINUSE") {
    console.error(`Port ${PORT} is already in use`);
  } else {
    console.error("Server error:", error);
  }
});
