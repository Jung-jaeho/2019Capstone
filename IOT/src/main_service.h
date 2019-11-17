#include"header.h"
#include<bluetooth/bluetooth.h>
#include<bluetooth/hci.h>
#include<bluetooth/hci_lib.h>
#include<bluetooth/rfcomm.h>
#include<bluetooth/l2cap.h>
#include<pthread.h>
typedef struct thread_argv{
	char* addr;
	int port;
}thread_argv;
struct s_time {
	int hour;
	int min;
	int sec;
};
int scan_bluetooth_addr(char **addrset);
struct s_time *getTime();
void* read_connection(void* ar);
int read_bltooth(int fd, char* object,int size);
int read_wait(int fd);
static void sig_time(int signo);
static void sig_child(int signo);
void set_signal_setting(int sig_count,int *signo,void (**signal_function)(int));
