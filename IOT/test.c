#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/time.h>
#include<fcntl.h>
#include<pthread.h>
#include<sys/wait.h>
int main()
{
	int status;
	int number=780;
	printf("이것은 c언어 파일입니다. \n");
	pid_t c_pid = fork();
	if(c_pid == 0){
		char *csv_number = (char*)malloc(sizeof(char));
		sprintf(csv_number,"%d",number);
		printf("요놈은 java 파일입니다.\n");
		char *arg[]= {"java","-jar","/home/song/2019Capstone/IOT/Sender.jar",csv_number,(char*)NULL};
		status = execvp("java",arg);
		if(status<0)
		{
			perror("status error");
			return -1;
		}
	}
	printf("Java를 기다립니다.");
	waitpid(c_pid,&status,0);
	if(WIFEXITED(status))
	{
		printf("error : %d\n",WEXITSTATUS(status));
	}
	else
	{
		printf("Java File 이 끝났습니다.");
	}
	printf("System Finish");
}


