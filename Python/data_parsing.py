import os
import numpy as np 
import csv
import math
from scipy.signal import butter, lfilter, freqz
import matplotlib.pyplot as plt

SAMPLE_PATH = "/home/pierre/Desktop/Huawei Challenge/Data/TrainingData/SHLDataset_preview_v1/User1/270617"
WINDOW_SIZE = 4
SAMPLE_FREQUENCY = 100 # Hz
SAVING_PATH = "./training_dataset.csv"

test = []

def butter_lowpass(cutoff, fs, order=5):
    nyq = 0.5 * fs
    normal_cutoff = cutoff / nyq
    b, a = butter(order, normal_cutoff, btype='low', analog=False)
    return b, a

def butter_lowpass_filter(data, cutoff, fs, order):
    b, a = butter_lowpass(cutoff, fs, order=order)
    y = lfilter(b, a, data)
    return y

def get_frequency_with_fft(array):
    fft = np.fft.fft(array)
    frequencies = np.linspace(0, SAMPLE_FREQUENCY, SAMPLE_FREQUENCY * WINDOW_SIZE,  endpoint=False)
    final_f = frequencies[np.argmax(abs(fft)[:15])]
    return final_f

def compute_metrics(batch):
    # batch [Linear acceleration X, Linear acceleration Y, Linear acceleration Z, Gyroscope X, Gyroscope Y, Gyroscope Z]
    # [m(ax + ay + az), ax + ay + az, m(gx + gy + gz), gx + gy + gz]
    print("Computing metrics")
    batch_np = np.array(batch)
    batch_np[:, 1] =  butter_lowpass_filter(batch_np[:, 1], 10, 100, order=2)
    batch_np[:, 2] =  butter_lowpass_filter(batch_np[:, 2], 10, 100, order=2)
    fft = np.fft.fft(batch_np[:, 1])
    f = np.linspace(0, SAMPLE_FREQUENCY, 200,  endpoint=False)
    print(f.shape)
    if np.mean(batch_np[:, -1]) == 2:
        # plt.plot(f[:15], abs(fft)[:15])
        # plt.title('Magnitude spectrum of the signal, {}'.format(np.mean(batch_np[:, -1])))
        # plt.xlabel('Frequency (Hz)')
        # test.append(np.mean(batch_np[:, -1]))
        print(get_frequency_with_fft(batch_np[:, 1]))
        plt.plot(batch_np[:, 1])
        plt.show()
    a_frequency = get_frequency_with_fft(batch_np[:, 1])
    print(a_frequency, np.mean(batch_np[:, -1]))
    g_frequency = get_frequency_with_fft(batch_np[:, 3])
    result = [np.mean(batch_np[:, 0]),  a_frequency, ]


def parse_data_from_one_body_part_training(user_data_folder, body_part):
    body_parts = ["bag", "hand", "hips", "torso"]

    if body_part not in body_parts:
        raise ValueError("Body part {} is not valid.".format(body_part))

    raw_data_file_path = os.path.join(SAMPLE_PATH, "{}_Motion.txt".format(body_part.title()))
    labels_file_path = os.path.join(SAMPLE_PATH, "Label.txt".format(body_part.title()))
    print("Path : " + raw_data_file_path)

    with open(raw_data_file_path, "r") as raw_file:
        with open(labels_file_path, "r") as label_file:
            with open(SAVING_PATH, "a") as saving_file:
                batch = []
                csv_writer = csv.writer(saving_file, delimiter=',')
                headers = ["a_magnitude", "a_frequency", "a_mean", "a_sddeviation", "g_magnitude", "g_frequency", "g_mean", "g_sddeviation", "label"]
                #csv_writer.writerow(headers)
                count = 0
                while(True):
                    label = int(label_file.readline().split()[2])
                    line = raw_file.readline().split()
                    if len([x for x in line if x == "NaN"]) > 10 or label == 0 or label > 4:
                        continue
                    input_line = [float(i) if i != 'NaN' else 0 for i in line]
                    input_line = [np.linalg.norm([input_line[17], input_line[18], input_line[19]]), 
                                  input_line[17] + input_line[18] + input_line[19],
                                np.linalg.norm([input_line[4], input_line[5], input_line[6]]),
                                input_line[4] + input_line[5] + input_line[6]]
                    input_line.append(label)
                    batch.append(input_line)
                    if len(batch) == SAMPLE_FREQUENCY * WINDOW_SIZE:
                        metrics = compute_metrics(batch)
                        batch = []
                    count += 1
                    if count >= 100000:
                        break



if __name__ == "__main__":
    parse_data_from_one_body_part_training(SAMPLE_PATH, "torso")
    print("Done")