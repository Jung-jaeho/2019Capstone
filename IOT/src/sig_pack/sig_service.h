#ifndef _GLOBAL_H
#include"../header.h"
#include"../main_shared.h"
#endif
#ifndef _MAIN_SERVICE_H
#include"../main_service.h"
#endif

void set_signal_setting(int sig_count,int *signo,void (**signal_function)(int));
void sig_time(int signo);
void sig_child(int signo);
void sig_server(int signo);
