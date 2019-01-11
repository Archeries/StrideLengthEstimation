% The lack of benchmarking datasets for pedestrian stride length estimation makes it hard to pinpoint differences of published methods. Existing datasets either lack the ground-truth of each stride or are limited to small spaces with single scene or motion pattern. To fully evaluate the performance of proposed ASLE algorithm, we conducted benchmark dataset for natural pedestrian dead reckoning using smartphone sensors and FM-INS module. we leveraged the FM-INS module(X-IMU) to provide the ground-truth of each stride with motion distance errors in 0.3% of the entire travel distance. The datasets were obtained from a group of healthy adults with natural motion patterns (fast walking, normal walking, slow walking, running, jumping). The datasets contained more than 22 km, 10000 strides of gait measurements. The datasets cover both indoor and outdoor cases, including: stairs, escalators, elevators, office environments, shopping mall, streets and metro station. To maximize compatibility, all data is published in open and simple file formats. 
%
% Dataset created by Android smartphone and a foot-mounted IMU module (X-IMU from x-io Technologies).
% Date of creation: Oct 2, 2018 to Oct 28, 2018.
% Developed by Research Center for Ubiquitous Computing Systems, Institute of Computing Technology Chinese Academy of Sciences, Beijing, China.
% The sample program stores information from Smartphone internal sensors (Accelerometers, Gyroscopes, Magnetometers) and also from external devices (X-IMU).
%
% Phone used for this Dataset:
% Manufacturer: Huawei
% Model: Mate 9
% API Android version: 26
% Android version Release: 8.0
%
% Data format: 'flag; Acc_X(m/s^2); Acc_Y(m/s^2); Acc_Z(m/s^2); Gyr_X(rad/s); Gyr_Y(rad/s); Gyr_Z(rad/s); Mag_X(uT); Mag_Y(uT); Mag_Z(uT); SensorTimestamp(s); stride-length(m), stride number, and cumulative walking-distance(m)'
% Each line (a sample) contain 14 columns. The first column is a flag, the next nine columns are the nine degree-of-freedom sensor data from the smartphone embedded sensors. The last three columns represent the current stride-length, stride number, and cumulative walking-distance, respectively. Therefore, the last three columns of each stride (contains about 120 samples(lines) since the sampling rate was set to 100 Hz) are constant value.
%
% The sensor is sampled at 100 Hz.
% Throughout the datasets, the users hold the phone in their hand in front of their chest. 
% The foot-mounted module is attached to the instep of the right foot of the pedestrian.
%

