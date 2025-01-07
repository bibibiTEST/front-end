from flask import Flask, request, jsonify
import os
import uuid
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 允许跨域请求，方便前端调用

# 存储路径设置
UPLOAD_FOLDER = 'uploads/images'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# 存储消息记录的列表（可以换成数据库）
messages = []

# 获取消息记录
@app.route('/api/messages', methods=['GET'])
def get_messages():
    return jsonify(messages)

# 发送消息（文字或图片）
@app.route('/api/messages', methods=['POST'])
def send_message():
    data = request.json
    content = data.get('content')
    is_sent_by_user = data.get('isSentByUser')
    is_image = data.get('isImage')

    # 将消息添加到记录中
    new_message = {
        'content': content,
        'isSentByUser': is_sent_by_user,
        'isImage': is_image
    }
    messages.append(new_message)
    return jsonify(new_message), 201

# 图片上传接口
@app.route('/api/upload', methods=['POST'])
def upload_image():
    if 'file' not in request.files:
        return jsonify({'error': 'No file part'}), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    # 生成唯一的文件名
    file_filename = str(uuid.uuid4()) + "_" + file.filename
    file_path = os.path.join(UPLOAD_FOLDER, file_filename)

    # 保存文件
    file.save(file_path)

    # 返回文件路径
    return jsonify({'imagePath': f'/uploads/images/{file_filename}'}), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
