#include"main_service.h"
#include"csv_writer/csv_writer.h"
#include"properties_pack/read_properties.h"

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
		if( *(addr_set+i) != NULL )
		{
			argv = (thread_argv*)malloc(sizeof(thread_argv));
			argv->addr= *(addr_set+i);
			argv->port = i+1;
			pthread_create(&pid[i],NULL,read_connection,(void*)argv);
			count+= 1;
		}
	}
	alarm(10);
	int signo[2] = {SIGALRM,SIGCHLD};
	void (*signal_function[2])(int) = {sig_time,sig_child};
	set_signal_setting(2,signo,signal_function);
	for(i = 0 ; i <count;i++)
	{
		pthread_join(pid[i],NULL);
	}
	printf("Finish\n");
}
void set_init()
{
	if(mkdir("/airbeat",0777)>=0)
	{
		mkdir("/airbeat/sensor_csv",0777);
		int i;
		properties_value pv;
		/*
		pv.sirial_number = (char*)malloc(200);
		pv.send_server = (char*)malloc(200);
		pv.arduino_count = 0;

		printf("System_init\n");
		printf("Serial_number_In : ");
		scanf(" %s",pv.sirial_number);
		printf("Send-Server : ");
		scanf(" %s",pv.send_server);
		printf("Arduino-Count : ");
		scanf(" %d",&pv.arduino_count);
		pv.arduino_mac = (char**)malloc(sizeof(char*)*pv.arduino_count);
		for(i =0 ;i <pv.arduino_count;i++)
		{
			pv.arduino_mac[i]= (char*)malloc(200);
			printf("Arduino-MAC : ");
			scanf(" %s",pv.arduino_mac[i]);
		}
		write_properties(&pv);
		*/
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
			return -1;
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
	int port = m_ar->port;
	int s,status,i;
	s = socket(AF_BLUETOOTH,SOCK_STREAM,BTPROTO_RFCOMM);
	printf("%d\n",port);
	if(s <0 )
	{
		printf("port %d: Socket open fail:",port);
		goto SOCK_ERROR;
	}
	printf("port %d : addr %s\n",port,argv);
	addr.rc_family = AF_BLUETOOTH;
	addr.rc_channel = (uint8_t)1;
	str2ba(argv,&addr.rc_bdaddr);
	status = connect(s,(struct sockaddr *)&addr,sizeof(addr));
	if(status < 0)
	{
		printf("port %d : connect Error\n",port);
		goto STATUS_ERROR;
	}
	if(status == 0)
	{
		int j;
		char send;
		char s_buf[256];
		send = 'A'+(port-1);
		write(s,&send,1);
		printf("port %d : connect: %s\n",port,argv);
		for(j = 0 ; j < 50 ;j++)
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
				r_len = sprintf(s_buf,"%c,%02d:%02d:%02d,%s\n",send,t->hour,t->min,t->sec,buf);
				free(t);
				write_csv(s_buf,port,number);
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

int scan_bluetooth_addr(char **addrset)
{
	char addr[18],name[255];
	uint8_t lap[3] = {0x33,0x8b,0x9e};
	struct hci_dev_info di;
	inquiry_info *info = NULL;	
	int dd=0,dev_id,i,num_rsp=0,length=8,n=0,flags=0;
	dev_id = hci_get_route(NULL);
	if(dev_id < 0 )
	{
		perror("Device not Inside");
		goto ERROR_EXIT;
	}
	if(hci_devinfo(dev_id,&di)<0)
	{
		perror("can`t get device info");
		goto ERROR_EXIT;
	}
	num_rsp = hci_inquiry(dev_id,length,num_rsp,lap,&info,flags);
	if(num_rsp <0)
	{
		perror("inquiry error");
		goto ERROR_EXIT;
	}
	dd = hci_open_dev(dev_id);
	if(dd<0)
	{
		perror("HCI_device open filed");
		goto ERROR_EXIT;
	}
	memset(addrset,0,sizeof(char*)*7);
	for(i = 0 ; i < num_rsp;i++)
	{
		ba2str(&(info+i)->bdaddr,addr);
		if(hci_read_remote_name(dd,&(info+i)->bdaddr,sizeof(name),name,10000)<0)
		{
			strcpy(name,"N/A");
		}
		if(strcmp(name,"HC-06") == 0)
		{
			addrset[n] = (char*)malloc(sizeof(char)*18);
			strcpy(addrset[n],addr);
			n++;
		}
		printf("%s %s\n",addr,name);
	}
	goto DONE;
ERROR_EXIT:
	bt_free(info);
	hci_close_dev(dd);
	return -1;
DONE:
	bt_free(info);
	hci_close_dev(dd);
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
int read_bltooth(int fd, char* object,int size)
{
	int length,rd_length;
	rd_length = read(fd,object,size);
	if(rd_length < size)
	{
		return 0;
	}
	char e_bit;
	read(fd,&e_bit,1);
	if(e_bit != 'E')return 0;
	return rd_length;
}
int read_wait(int fd)
{
	char buf;
	char length[2];
	while(read(fd,&buf,1)<0);
	if(buf == 'S'){
		read(fd,length,2);
		int size;
		size = atoi(length);
		printf("%d\n",size);
		return size;
	}
	while(read(fd,&buf,1)>=0)
	{
		if(buf == 'E')
			break;
	}
	return 0;
}
