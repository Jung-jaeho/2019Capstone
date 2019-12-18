#include"sig_service.h"
pid_t pid=0;
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
	number += 1;
	pid = fork();
	if(pid == 0)
	{
		if(group_csv_file(number-1) < 0)
		{
			exit(3);
		}
		char *arg[]= {"java","-jar","/airbeat/Sender.jar",SEND_FILE_NAME,pro->send_server,pro->sirial_number,(char*)NULL};
		status = execvp("java",arg);
		if(status<0)
		{
			perror("status error");
			exit(0);
		}
	}
	printf("pid get: %d\n",pid);
}
void sig_server(int signo)
{
	int status;
	pid = fork();
	if(pid == 0)
	{
		if(group_csv_file(number-1) < 0)
		{
			exit(3);
		}
		char *arg[]= {"java","-jar","/airbeat/Sender.jar",SEND_FILE_NAME,pro->send_server,pro->sirial_number,(char*)NULL};
		status = execvp("java",arg);
		if(status<0)
		{
			perror("status error");
			exit(0);
		}
	}
}
void sig_child(int signo)
{
	int status;
	waitpid(pid,&status,0);
	int value = status >> 8 ;
	if(value == 3)
	{
		int signo_num[2] = {SIGALRM,SIGCHLD};
		void (*signal_function[2])(int) = {sig_time,sig_child};
		printf("GROUP ERROR\n");
		number -= 1;
		set_signal_setting(2,signo_num,signal_function);
		remove(SEND_FILE_NAME);
		alarm(5);
	}
	else if(value == 4)
	{
		int signo_num[2] = {SIGALRM,SIGCHLD};
		void (*signal_function[2])(int) = {sig_server,sig_child};
		printf("Server TimeOut\n");
		set_signal_setting(2,signo_num,signal_function);
		remove(SEND_FILE_NAME);
		alarm(120);
	}
	else
	{
		int signo_num[2] = {SIGALRM,SIGCHLD};
		void (*signal_function[2])(int) = {sig_time,sig_child};
		set_signal_setting(2,signo_num,signal_function);
		remove(SEND_FILE_NAME);
		printf("OK \n");
		printf("pid close %d\n",pid);	
		alarm(7);
	}
}
