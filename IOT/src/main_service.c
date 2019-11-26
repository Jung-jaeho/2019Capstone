#include"main_service.h"


unsigned int number=0;

struct bt_table table[7];
struct properties_value *pro;
void set_init()
{
	if(mkdir("/airbeat",0777)>=0)
	{
		mkdir("/airbeat/sensor_csv",0777);
		pro = read_properties();
	}
	else
	{
		remove("/airbeat/sensor_csv/*");
		pro = read_properties();
	}
	return;
}
void* read_connection(void* ar)
{
	struct sockaddr_rc *addr=(struct sockaddr_rc*)malloc(sizeof(struct sockaddr_rc));
	memset(addr,0,sizeof(struct sockaddr_rc));
	thread_argv *m_ar = (thread_argv*)ar;
	char* argv = m_ar->addr;
	char port = m_ar->port;
	int p_count = m_ar->count;
	int s,status=-1;
	int connect_count = 0;
	struct timeval tv;
	tv.tv_sec = 2;
	tv.tv_usec = 0;
RE_CONNECTION:
	while(status < 0)
	{ 	
		s = socket(AF_BLUETOOTH,SOCK_STREAM,BTPROTO_RFCOMM);
		setsockopt(s,SOL_SOCKET,SO_RCVTIMEO,(char*)&tv,sizeof(struct timeval));
		if(s <0 )
		{
			printf("port %c: Socket open fail:",port);
			goto SOCK_ERROR;
		}
		addr->rc_family = AF_BLUETOOTH;
		addr->rc_channel = (uint8_t)1;
		str2ba(argv,&addr->rc_bdaddr);
		status = connect(s,(struct sockaddr*)addr,sizeof(*addr));
		printf("Connect Addr : ... %s\n ",argv);
		if(status < 0)
		{
			close(s);
		}
		usleep(5000*1000);
	}
	table[p_count].tf=true;
	if(status == 0)
	{
		int j;
		char send;
		char s_buf[256];
		while(write(s,&port,1)<0);
		while(table[p_count].tf)
		{
			char buf[128];
			int r_len;
			r_len = read_wait(s);
			if(r_len == -2)
				table[p_count].tf = false;
			usleep(1000*150);
			if(r_len != 0)
			{
				r_len = read_bltooth(s,buf,r_len);
				if(r_len == -2)
				{
					printf("CONNECTION FAIL ADDR: %s %d\n",argv,p_count);
					printf("TRUE : table[%d].tf == %d\n",p_count,table[p_count].tf);
					table[p_count].tf = false;
					printf("FALSE: table[%d].tf == %d\n",p_count,table[p_count].tf);
				}
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
		close(s);
		status = -1;
		free(addr);
		addr = (struct sockaddr_rc*)malloc(sizeof(struct sockaddr_rc));
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
					table[i].connect_number = (argv->port-'A')+1;//파일 넘버 1~7까지(port-'A'+1)
					printf("table i = %d addr = %s connect_number = %d\n",i,table[i].addr,table[i].connect_number);
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
