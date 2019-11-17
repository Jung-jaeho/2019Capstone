#include<stdio.h>
#include<string.h>
#include<unistd.h>
#include<stdlib.h>
#include<fcntl.h>


#define FILE_PATH "/home/song/airbeat/airbeat.properties"
#define SIRIAL_NUMBER "Sirial_Number"
#define SEND_SERVER "Send_Server"
#define ARDUINO_MAC "Arduino_MAC"
#define ARDUINO_COUNT "Arduino_Count"
typedef struct properties_value{
	char *sirial_number;
	char *send_server;
	char **arduino_mac;
}properties_value;
properties_value pro;
int read_properties(char* argv);
