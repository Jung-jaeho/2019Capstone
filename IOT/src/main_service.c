#include"main_service.h"


unsigned int number=0;

struct bt_table table[7];
struct properties_value *pro;
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
void* read_connection(void* ar)
{
	struct sockaddr_rc addr= {0};
	thread_argv *m_ar = (thread_argv*)ar;
	char* argv = m_ar->addr;
	char port = m_ar->port;
	int p_count = m_ar->count;
	int s,status=-1,i;
	int connect_count = 0;
RE_CONNECTION:
	s = socket(AF_BLUETOOTH,SOCK_STREAM,BTPROTO_RFCOMM);
	printf("%c\n",port);
	if(s <0 )
	{
		printf("port %c: Socket open fail:",port);
		goto SOCK_ERROR;
	}
	printf("port %c : addr %s\n",port,argv);
	while(status < 0)
	{ 	
		printf("Try connection... %s\n",argv);
		addr.rc_family = AF_BLUETOOTH;
		addr.rc_channel = (uint8_t)1;
		str2ba(argv,&addr.rc_bdaddr);
		status = connect(s,(struct sockaddr *)&addr,sizeof(addr));
		usleep(1000*1000);
		connect_count++;
		if(connect_count >10)
		{
			goto STATUS_ERROR;
		}
	}
	table[i].tf=true;
	if(status == 0)
	{
		int j;
		char send;
		char s_buf[256];
		write(s,&port,1);
		printf("port %c : connect: %s\n",port,argv);
		while(table[i].tf)
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
		goto RE_CONNECTION;
	}
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
	memset(table,0,sizeof(table));
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

					argv->count = i;
					argv->port = pro->arduino_mac[j][0];
					table[i].addr = *(addr_set+i);
					table[i].connect_number = argv->port-'A'+1;
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
