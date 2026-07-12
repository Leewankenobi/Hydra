from http.server import HTTPServer, BaseHTTPRequestHandler
import json

class MyHandler(BaseHTTPRequestHandler):
  def do_POST(self):
    # Read the incoming data
    content_length = int(self.headers['Content-Length'])
    post_data = self.rfile.read(content_length)
    
    # Parse JSON
    payload = json.loads(post_data.decode('utf-8'))
    print(f"Received: {payload}")
    
    # Send response back
    self.send_response(200)
    self.send_header('Content-type', 'application/json')
    self.end_headers()
    self.wfile.write(json.dumps({"status": "success"}).encode())

# Start server on port 8000
server = HTTPServer(('0.0.0.0', 8000), MyHandler)
print("Server running on port 8000...")
server.serve_forever()