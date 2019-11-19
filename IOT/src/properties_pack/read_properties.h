#include<stdio.h>
#include<string.h>
#include<unistd.h>
#include<stdlib.h>
#include<fcntl.h>


#define FILE_PATH "/airbeat/system.properties"
#define SIRIAL_NUMBER "Sirial_Number"
#define SEND_SERVER "Send_Server"
#define ARDUINO_MAC "Arduino_MAC"
#define ARDUINO_COUNT "Arduino_Count"
typedef struct properties_value{
	char *sirial_number;
	char *send_server;
	int arduino_count;
	char **arduino_mac;
}properties_value;
void write_properties(properties_value* object);
properties_value* read_properties();
