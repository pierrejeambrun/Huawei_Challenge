import numpy as np
import pickle

model = pickle.load(open("xgboost_model.p","rb"))

def predict(input_vector):
    input_vector = input_vector[np.newaxis, :]
    print(input_vector)
    output = model.predict(input_vector)
    output = np.squeeze(output)
    return output

def process_batch(input_path, output_path):
    # Classify output format
    # Launch training and create a model with sliding window of 4 seconds.
    pass

if __name__ == "__main__":
    test = [1.17375118043,1.0,-0.128894988887,1.09504279337,0.540089569726,0.5,-0.09358088,0.568179152855]
    test = np.array(test)
    print(predict(test))
