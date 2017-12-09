import numpy as np
import pickle
import data_parsing

model = pickle.load(open("./models/xgboost_model_2s.p","rb"))

def predict(input_vector):
    input_vector = input_vector[np.newaxis, :]
    output = model.predict(input_vector)
    output = np.squeeze(output)
    return output

def compute_metrics(batch):
    # [m(ax + ay + az), ax + ay + az, m(gx + gy + gz), gx + gy + gz, timestamp]
    batch_np = np.array(batch)
    batch_np[:, 1] =  data_parsing.butter_lowpass_filter(batch_np[:, 1], 2, 100, order=2)
    batch_np[:, 2] =  data_parsing.butter_lowpass_filter(batch_np[:, 2], 2, 100, order=2)
    a_frequency = data_parsing.get_frequency_with_fft(batch_np[:, 1])
    g_frequency = data_parsing.get_frequency_with_fft(batch_np[:, 3])
    result = [np.mean(batch_np[:, 0]),  a_frequency, np.mean(batch_np[:, 1]), np.std(batch_np[:, 1]),
            np.mean(batch_np[:, 2]),  g_frequency, np.mean(batch_np[:, 3]), np.std(batch_np[:, 3])]
    return result

def process_batch(input_path, output_path):
     with open(input_path, "r") as raw_file:
        with open(output_path, "w") as saving_file:
            batch = []
            timestamp_list = []
            headers = ["a_magnitude", "a_frequency", "a_mean", "a_sddeviation", "g_magnitude", "g_frequency", "g_mean", "g_sddeviation", "timestamp_list"]
            count = -1

            while True:
                count += 1
                try:
                    line = raw_file.readline().split()
                    timestamp = line[0]
                except IndexError as e:
                    print("End of file")
                    subbatch_process(batch, timestamp_list)
                    batch = []
                    timestamp_list = []
                    break
                if len([x for x in line if x == "NaN"]) > 10:
                    timestamp_list.append(line[0])
                    continue
                input_line = [float(i) if i != 'NaN' else 0 for i in line]
                input_line = [np.linalg.norm([input_line[17], input_line[18], input_line[19]]),
                              input_line[17] + input_line[18] + input_line[19],
                            np.linalg.norm([input_line[4], input_line[5], input_line[6]]),
                            input_line[4] + input_line[5] + input_line[6]]
                timestamp_list.append(timestamp)
                batch.append(input_line)
                def subbatch_process(batch, timestamp_list):
                    metrics = compute_metrics(batch)
                    label = predict(np.array(metrics))
                    for timestamp in timestamp_list:
                        saving_file.write(" ".join([str(int(float(timestamp))), str(label)]) + "\n")
                if len(batch) == data_parsing.SAMPLE_FREQUENCY * data_parsing.WINDOW_SIZE:
                    subbatch_process(batch, timestamp_list)
                    batch = []
                    timestamp_list = []
                if count % 100000 == 0:
                    print(count)


def process_trip_batches(trip_path, output_root_path, user):
    if not os.isdir(os.join(output_root_path, user)):
        os.makedir(os.join(output_root_path, user))

    output_trip_path = os.path.join(output_root_path, user, os.basename(trip_path))
    if not os.isdir(output_trip_path):
        os.makedir(output_trip_path)

    motions_file_name = [x for x in os.listdir(trip_path) if "Motion" in x]

    for motion_file_name in motions_file_name:
        process_batch(os.path.join(trip_path, motion_file_name), os.path.join(output_trip_path, "Label_{}.txt".format(motion_file_name.split(" ")[0])))

def create_global_output_folder(data_root_path, output_root_path):
    users = [user for user in os.listdir(data_root_path) if "User" in user]
    users = [os.path.join(data_root_path, user) for user in users]

    for user in users:
        trips = [os.path.join(user, x) for x in os.listdir(user) if os.path.isdir(os.path.join(user, x))]
        for trip in trips:
            print("Parsing trip {} for user {}...".format(os.path.basename(trip), os.path.basename(user)))
            process_trip_batches(trip, output_root_path, os.basename(user))

if __name__ == "__main__":
    data_root_path = "/home/ubuntu/HackATon/Data/EvalData/SHLDataset_preview_v1/"
    output_root_path = "/home/ubuntu/HackATon/Output"
    create_global_output_folder(data_root_path, output_root_path)
