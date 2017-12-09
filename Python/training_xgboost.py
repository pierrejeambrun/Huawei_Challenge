import xgboost as xgb
import pandas as pd
from sklearn.metrics import accuracy_score, explained_variance_score, classification_report
from sklearn.grid_search import GridSearchCV
import numpy as np
import pickle

input_dataset = pd.read_csv("training_dataset.csv", header=0)
print(input_dataset)
big_X_inputed = input_dataset.as_matrix()
np.random.shuffle(big_X_inputed)
trainval_index = int(big_X_inputed.shape[0] * 0.8)
train_Y = big_X_inputed[:, -1]

big_X_inputed = big_X_inputed[:, :-1]
train_X = big_X_inputed[0: trainval_index]
val_X = big_X_inputed[trainval_index:]


gbm = xgb.XGBClassifier()
param_search = {
    "max_depth": [_ for _ in range(1, 4)],
    "n_estimators": [50 * _ for _ in range(1, 5)],
    "learning_rate": [0.1 * _ for _ in range(1, 5)]
}
print("Training...")
search = GridSearchCV(estimator= gbm, param_grid=param_search)
search.fit(train_X, train_Y[0: trainval_index])

gbm = search
print(search.best_params_)

print('---------------------------------')
validation_prediction = gbm.predict(val_X)
val_Y = train_Y[trainval_index:]
accuracy = accuracy_score(val_Y, validation_prediction)
print("Final accuracy is {}".format(accuracy * 100))
pickle.dump(gbm, open("xgboost_model.p", "wb"))