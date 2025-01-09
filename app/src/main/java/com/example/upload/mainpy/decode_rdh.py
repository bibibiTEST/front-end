import cv2
import numpy as np
import numpy.linalg
import encode_rdh
from  RC4_exp import RC4,PRGA
from extract import extract

def binary_to_string(binary_str):
    # 每8位为一组转换为字符
    chars = [chr(int(binary_str[i:i+8], 2)) for i in range(0, len(binary_str), 8)]
    return ''.join(chars)


def message_key(key_path):
    with open(key_path, 'r') as f:
        # 读取文件内容，逐行处理
        lines = f.readlines()

        # 将每行字符串拆分并转换为数字，构建一个二维列表
        matrix = [list(map(int, line.split())) for line in lines]
        # 转换为NumPy数组
        matrix_np = np.array(matrix)
        return matrix_np
if __name__ == '__main__':
    input_image_path = "Marked_Image.png"  # 需要解码的图片
    image = cv2.imread(input_image_path)
    if image is None:
        raise ValueError("无法加载图像！请检查路径。")

    # RC4 密钥
    #key = 84612565  # 密钥可以自行修改
    set = message_key("lololo.txt")
    # decode_image = encode_rdh.encrypt_image(image, key)  # 异或解密

    enimg = RC4()
    rand_stream = PRGA(enimg.S, image.shape[0] * image.shape[1] * image.shape[2])
    decode_image = enimg.RC4_img(input_image_path,rand_stream) #将原始图像加密
    cv2.imwrite("decode.png", decode_image)

    extract = extract()
    recovered, recover_data = extract.extract_data(decode_image, 8,set)
    cv2.imwrite('Recovered_Image.png', recovered)
    flattened_string = ''.join(map(str, recover_data[0, :64].flatten()))
    result = binary_to_string(flattened_string)
    print('数据提取成功')
    print("提取数据:", result)
    print(result)

    # 保存加密图像

