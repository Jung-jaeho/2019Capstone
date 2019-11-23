#include"main_service.h"
#include"csv_writer/csv_writer.h"
#include"properties_pack/read_properties.h"
#include"bluetooth_pack/bluetooth_service.h"

bt_table table[7];
properties_value *pro;
unsigned int number=0;
void set_init();
int main()
{
	set_init();
	float value[4];
	thread_argv *argv;
	pthread_t pid[5];
	char **addr_set = (char**)malloc(sizeof(char*)*7);
	int i,count=0;
	printf("Server : %s\n",pro->send_server);
	printf("Sirial_Number : %s\n",pro->sirial_number);
	if(scan_bluetooth_addr(addr_set) < 0 )
	{
		printf("error \n");
	}
	for(i = 0; i<7;i++)
	{
		int j;
		if( *(addr_set+i) != NULL )
		{
			argv = (thread_argv*)malloc(sizeof(thread_argv));
			argv->addr= *(addr_set+i);
			for(j = 0 ; j < pro->arduino_count;j ++)
			{
				if(strcmp(*(addr_set+i),pro->arduino_mac[j]+2)==0)
				{
					strcpy(table[i].str,*(addr_set+i));
					argv->port = pro->arduino_mac[j][0];
					printf("%c",pro->arduino_mac[j][0]);
					pthread_create(&pid[i],NULL,read_connection,(void*)argv);
					count+= 1;
					break;
				}
			}

		}		
	}
	alarm(20);
	int signo[2] = {SIGALRM,SIGCHLD};
	void (*signal_function[2])(int) = {sig_time,sig_child};
	set_signal_setting(2,signo,signal_function);
	for(i = 0 ; i <count;i++)
	{
		pthread_join(pid[i],NULL);
	}
	sig_time(SIGCHLD);
	sig_child(SIGCHLD);
	printf("Finish\n");
}
void set_init()
{
	if(mkdir("/airbeat",0777)>=0)
	{
		mkdir("/airbeat/sensor_csv",0777);
		int i;
		properties_value pv;
		pro = read_properties();
	}
	else
	{
		pro = read_properties();
	}
	return;
}
void set_signal_setting(int sig_count,int *signo,void (**signal_function)(int))
{
	int i;
	for(i = 0 ; i < sig_count;i++)
	{
		struct sigaction act;
		act.sa_handler = signal_function[i];
		sigaction(signo[i],&act,NULL);
	}
}
static void sig_time(int signo)
{
	printf("SigAlarm\n");
	int status;
	int signo_num = SIGALRM;
	void (*signal_function)(int) = sig_time;
	group_csv_file(number++);
	set_signal_setting(1,&signo_num,&signal_function);
	if(fork() == 0)
	{
		printf("EXECP\n");
		char *arg[]= {"java","-jar","./Sender.jar",SEND_FILE_NAME,pro->send_server,pro->sirial_number,(char*)NULL};
		status = execvp("java",arg);
		if(status<0)
		{
			perror("status error");
			return;
		}
	}
	alarm(10);
}
static void sig_child(int signo)
{
	int signo_num = SIGCHLD;
	void (*signal_function)(int) = sig_child;
	set_signal_setting(1,&signo_num,&signal_function);
	remove(SEND_FILE_NAME);	
}
void* read_connection(void* ar)
{
	struct sockaddr_rc addr= {0};
	thread_argv *m_ar = (thread_argv*)ar;
	char* argv = m_ar->addr;
	char port = m_ar->port;
	int s,status,i;
	s = socket(AF_BLUETOOTH,SOCK_STREAM,BTPROTO_RFCOMM);
	printf("%c\n",port);
	if(s <0 )
	{
		printf("port %c: Socket open fail:",port);
		goto SOCK_ERROR;
	}
	printf("port %c : addr %s\n",port,argv);
	addr.rc_family = AF_BLUETOOTH;
	addr.rc_channel = (uint8_t)1;
	str2ba(argv,&addr.rc_bdaddr);
	status = connect(s,(struct sockaddr *)&addr,sizeof(addr));

	if(status < 0)
	{
		printf("port %c : connect Error\n",port);
		goto STATUS_ERROR;
	}
	if(status == 0)
	{
		int j;
		char send;
		char s_buf[256];
		write(s,&port,1);
		printf("port %c : connect: %s\n",port,argv);
		while(1)
		{
			char buf[128];
			int r_len;
			r_len = read_wait(s);
			usleep(1000*150);
			if(r_len!=0)
			{
				r_len = read_bltooth(s,buf,r_len);
				if(r_len == 0)
					continue;
				printf("Size : %d %s\n",r_len,buf);
				buf[r_len-1] = '\0';
				struct s_time *t= getTime();
				r_len = sprintf(s_buf,"%c,%02d:%02d:%02d,%s\n",port,t->hour,t->min,t->sec,buf);
				free(t);
				write_csv(s_buf,(port-'A')+1,number);
			}
		}
	}
	goto DONE;
SOCK_ERROR:
	return (void*)-1;
STATUS_ERROR:
	close(s);
	return (void*)-1;
DONE:
	close(s);
	return (void*)0; 
}

struct s_time *getTime()
{
	struct s_time *rs = (struct s_time*)malloc(sizeof(struct s_time));
	time_t sec_time = time(NULL);
	struct tm *t = localtime(&sec_time);
	rs->hour = t->tm_hour;
	rs->min = t->tm_min;
	rs->sec = t->tm_sec;
	return rs;
}

