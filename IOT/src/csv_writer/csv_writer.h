#define TRUE 0
#define FALSE 1
#define FILE_NAME "/home/song/sensor_csv/arduino_sensor_"
#define SEND_FILE_NAME "/home/song/sensor_csv/send_file.csv"
#define FOLDER_NAME "/home/song/sensor_csv"

typedef struct send_msg{
	char *value;
	int len;
}send_msg;
int write_csv(char* value,int number,int count);
int write_csv_to_fd(int fd, char* value, int len);
int group_csv_file(int number);
send_msg* tok(char* value,send_msg *msg);
