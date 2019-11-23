#include<fcntl.h>
#ifndef _GLOBAL_H
#include"../header.h"
#include"../main_shared.h"
#endif

#define FILE_PATH "/airbeat/system.properties"
#define SIRIAL_NUMBER "Sirial_Number"
#define SEND_SERVER "Send_Server"
#define ARDUINO_MAC "Arduino_MAC"
#define ARDUINO_COUNT "Arduino_Count"
void write_properties(properties_value* object);
properties_value* read_properties();
