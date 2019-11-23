#include"sig_service.h"
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
void sig_time(int signo)
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
void sig_child(int signo)
{
	int signo_num = SIGCHLD;
	void (*signal_function)(int) = sig_child;
	set_signal_setting(1,&signo_num,&signal_function);
	remove(SEND_FILE_NAME);	
}
