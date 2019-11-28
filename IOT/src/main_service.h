#define _MAIN_SERVICE_H

#include<pthread.h>
#include"bluetooth_pack/bluetooth_service.h"
#include"properties_pack/read_properties.h"
#include"csv_writer/csv_writer.h"
#include"sig_pack/sig_service.h"

#ifndef _GLOBAL_H
#include"main_shared.h"
#include"header.h"
#endif

typedef struct thread_argv{
	char* addr;
	char port;
	int count;
}thread_argv;
struct s_time {
	int hour;
	int min;
	int sec;
};

struct s_time *getTime();
void* read_connection(void* ar);
void set_init();
