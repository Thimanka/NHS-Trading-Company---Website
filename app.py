from flask import Flask, request, jsonify, render_template, send_from_directory
import os

app = Flask(__name__, static_folder='assets', template_folder='.')

# Serve the static HTML files
@app.route('/')
def index():
    return send_from_directory('.', 'index.html')

@app.route('/<path:filename>')
def serve_html(filename):
    if filename.endswith('.html'):
        return send_from_directory('.', filename)
    return send_from_directory('.', filename + '.html')

# API endpoint for the contact form
@app.route('/submit-form', methods=['POST'])
def submit_form():
    name = request.form.get('name')
    email = request.form.get('email')
    interest = request.form.get('interest')
    message = request.form.get('message')
    
    # Here you would typically save to a database or send an email.
    print(f"Received inquiry from: {name} ({email}) regarding {interest}")
    print(f"Message: {message}")
    
    # Return a success response
    return """
    <html>
    <head><title>Success</title><style>body{font-family:sans-serif; text-align:center; padding:50px;}</style></head>
    <body>
        <h1 style="color: #635BFF;">Thank you for contacting NHS Trading!</h1>
        <p>We have received your inquiry and will get back to you shortly.</p>
        <a href="/" style="display:inline-block; margin-top:20px; padding:10px 20px; background:#0A2540; color:white; text-decoration:none; border-radius:5px;">Return Home</a>
    </body>
    </html>
    """

if __name__ == '__main__':
    print("Starting Python Flask server for NHS Trading Company...")
    print("Visit http://localhost:5000")
    app.run(debug=True, port=5000)
