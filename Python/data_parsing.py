import os
import numpy as np 
import csv
import math
from scipy.signal import butter, lfilter, freqz
import matplotlib.pyplot as plt

WINDOW_SIZE = 2
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
    batch_np = np.array(batch)
    batch_np[:, 1] =  butter_lowpass_filter(batch_np[:, 1], 2, 100, order=2)
    batch_np[:, 2] =  butter_lowpass_filter(batch_np[:, 2], 2, 100, order=2)
    a_frequency = get_frequency_with_fft(batch_np[:, 1])
    g_frequency = get_frequency_with_fft(batch_np[:, 3])
    result = [np.mean(batch_np[:, 0]),  a_frequency, np.mean(batch_np[:, 1]), np.std(batch_np[:, 1]),
            np.mean(batch_np[:, 2]),  g_frequency, np.mean(batch_np[:, 3]), np.std(batch_np[:, 3]), int(round(np.mean(batch_np[:, -1])))]
    return result

def parse_data_from_one_body_part_training(trip_path, body_part):
    raw_data_file_path = os.path.join(trip_path, "{}_Motion.txt".format(body_part.title()))
    labels_file_path = os.path.join(trip_path, "Label.txt".format(body_part.title()))

    with open(raw_data_file_path, "r") as raw_file:
        with open(labels_file_path, "r") as label_file:
            with open(SAVING_PATH, "a") as saving_file:
                batch = []
                csv_writer = csv.writer(saving_file, delimiter=',')
                headers = ["a_magnitude", "a_frequency", "a_mean", "a_sddeviation", "g_magnitude", "g_frequency", "g_mean", "g_sddeviation", "label"]
                #csv_writer.writerow(headers)
                count = -1
                while(True):
                    count += 1
                    try:
                        label = int(label_file.readline().split()[1])
                    except IndexError as e:
                        print("End of file")
                        break
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
                        csv_writer.writerow(metrics)
                        batch = []
                    if count % 100000 == 0:
                        print(count)


def parse_trip(trip_folder_path):
    body_parts = ["bag", "hand", "hips", "torso"]
    for body_part in body_parts:
        parse_data_from_one_body_part_training(trip_folder_path, body_part)

if __name__ == "__main__":
    data_root_path = "/home/pierre/Desktop/Huawei Challenge/Data/TrainingData/SHLDataset_preview_v1/"
    users = [user for user in os.listdir(data_root_path) if "User" in user]
    users = [os.path.join(data_root_path, user) for user in users]

    for user in users:
        trips = [os.path.join(user, x) for x in os.listdir(user) if os.path.isdir(os.path.join(user, x))]
        for trip in trips:
            print("Parsing trip {} for user {}...".format(os.path.basename(trip), os.path.basename(user)))
            parse_trip(trip)
