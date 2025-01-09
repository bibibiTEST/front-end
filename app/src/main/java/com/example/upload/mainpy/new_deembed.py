import numpy as np
import cv2 as cv
import numpy.linalg

import encode_rdh


def process_image_blocks(image, block_num,block_size=8):
    """
    将图像划分为 block_size x block_size 的块
    """
    height, width = image.shape

    # 确保图像尺寸是 block_size 的整数倍
    # if height % block_size != 0 or width % block_size != 0:
    #     raise ValueError(f"图像尺寸必须是 {block_size} 的整数倍！")

    blocks = []
    # for row in range(0, height, block_size):
    #     for col in range(0, width, block_size):
    #         block = image[row:row + block_size, col:col + block_size]
    #         blocks.append(block)

    blocks_per_row = width // block_size
    for i in range(block_num):
        block_row = i // blocks_per_row
        block_col = i % blocks_per_row

        start_x = block_col * block_size
        start_y = block_row * block_size
        blocks.append(image[start_y:start_y + block_size, start_x:start_x + block_size])
    return blocks

def merge_image_blocks(blocks, image_shape, block_size=8):
    """
    将图像块重新合并为原始图像
    """
    height, width = image_shape
    merged_image = np.zeros((height, width))
    print()
    index = 0
    for row in range(0, height, block_size):
        for col in range(0, width, block_size):
            merged_image[row:row + block_size, col:col + block_size] = blocks[index]
            index += 1

    return merged_image

def calculateF(H_img, block_size):
    d1 = 0
    d2 = 0
    d3 = 0
    H_img = H_img.astype(np.float32)

    for u in range(1, block_size-1):
        for v in range(1, block_size-1):
            avg_neighbors = (H_img[u-1][v] + H_img[u][v-1] + H_img[u+1][v] + H_img[u][v+1]) / 4
            d1 += abs(H_img[u][v] - avg_neighbors)
    for u in range(0,block_size):
        for v in range(block_size-1):
            d2 += abs(H_img[u][v] - H_img[u][v + 1])
    for u in range(0, block_size-1):
        for v in range(1,block_size):
            d2 += abs(H_img[u][v] - H_img[u+1][v])

    for u in range(1,block_size-1):
        d3 += abs(H_img[0][u] - (H_img[0][u-1]+H_img[0][u+1]+H_img[1][u])/3)
    for u in range(1,block_size-1):
        d3 += abs(H_img[block_size-1][u] - (H_img[0][u-1]+H_img[0][u+1]+H_img[1][u])/3)
    for u in range(1,block_size-1):
        d3 += abs(H_img[u][0] - (H_img[u-1][0]+H_img[u+1][0]+H_img[u][1])/3)
    for u in range(1,block_size-1):
        d3 += abs(H_img[u][block_size-1] - (H_img[u-1][block_size-1]+H_img[u+1][block_size-1]+H_img[u][block_size-2])/3)
    d3+=(abs(H_img[0][0]-(H_img[0][1]+H_img[1][0])/2) +
         abs(H_img[0][block_size-1]-(H_img[0][block_size-2]+H_img[1][block_size-1])/2))
    d3+=(abs(H_img[block_size-1][0]-(H_img[block_size-1][0]+H_img[block_size-1][1])/2) +
         abs(H_img[block_size-1][block_size-1]-(H_img[block_size-1][block_size-2]+H_img[block_size-2][block_size-1])/2))
    return d1 + d2 + d3

def extract_data(img, block_size, block_order, k):
    blocks = process_image_blocks(img,len(block_order),block_size)
    message = []
    groups = len(block_order) // k
    i = 0
    for idx in range(groups):
        f = []
        f2 = []
        a = []

        for block in range(k):
            current_block_idx = block_order[i]
            original_block = blocks[current_block_idx]

            # 计算 f 值
            temp_f = calculateF(original_block, block_size)
            f.append(temp_f)

            # 修改块并计算 f' 值
            modified_block = original_block.copy()
            for u in range(block_size):
                for v in range(block_size):
                    if (u + v) % 2 == 0:
                        modified_block[u][v] ^= 0b00001000

            temp_f2 = calculateF(modified_block, block_size)
            f2.append(temp_f2)

            # 计算 A 值
            temp_a = temp_f - temp_f2
            a.append(temp_a)
            # print("current:",current_block_idx,":",temp_a)
            i += 1
        # print("a group finish")
        # 找到被修改的块索引
        max_a_idx = np.argmax(a)
        message.append(max_a_idx)
        # 恢复被修改的块
        target_block_idx = block_order[idx * k + max_a_idx]
        target_block = blocks[target_block_idx].copy()
        for u1 in range(block_size):
            for v1 in range(block_size):
                if (u1 + v1) % 2 == 0:
                    target_block[u1][v1] ^= 0b00001000
        blocks[target_block_idx] = target_block

    recover = merge_image_blocks(blocks, img.shape)
    return recover, message


def getMessage(message, k):
    # #true message
    # message = [3,0,2,6,1,1,4,3,3,1,0,6,2,5,4,6,3,1,6,0,0]
    for i in range(len(message) - 1):
        if message[i] == 0 and message[i + 1] == 0:
            message = message[:i]
            break
    print(message)
    t = np.log2(k)
    t = int(t)
    t = t if t < 8 else 8  # t 最大为 8
    binary_message = ''.join([format(num, '08b')[8-t:8] for num in message])
    decoded_message = ""
    i = 0
    print(binary_message)
    while i + 8 <= len(binary_message):
        # 获取 8 位二进制
        char_binary = binary_message[i:i + 8]
        # 转换为字符
        decoded_message += chr(int(char_binary, 2))

        # 移动到下一个8位
        i += 8

    return decoded_message


if __name__ == '__main__':
    input_image_path = "embedded_image_0.png"  # 需要解码的图片
    image = cv.imread(input_image_path,0)
    if image is None:
        raise ValueError("无法加载图像！请检查路径。")
    # 定义文件路径
    # RC4 密钥
    key = [1, 2, 3]
    decodeImage = encode_rdh.encrypt_image(image,key)
    file_path = "order_0.txt"  # 替换为你的文件路径
    cv.imwrite("decodeImageWithInfo.png", decodeImage)
    # 初始化 block_order 列表
    block_order = []

    # 读取文件并提取数字
    with open(file_path, "r") as file:
        # 读取文件内容并分割为单个数字
        block_order = [int(num) for num in file.read().split()]


    recover ,message = extract_data(decodeImage,8,block_order,8)
    cv.imwrite("test.png", recover)
    decodedMessage = getMessage(message,8)
    print(decodedMessage)
    print("finish")
