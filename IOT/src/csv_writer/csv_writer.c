#include"csv_writer.h"
int write_csv(char* value,int number,int count)
{
	char file_name[150];
	int fd;
	sprintf(file_name,"%s%s%d_%d",FOLDER_NAME,FILE_NAME,number,count);
	fd = open(file_name,O_WRONLY|O_CREAT|O_APPEND,0666);
	write(fd,value,strlen(value));
	close(fd);
	return 1;
}
int write_csv_to_fd(int fd, char* value,int len)
{
	write(fd,value,len);
	return 1;
}
int group_csv_file(int number)
{
	int fd[7],sfd=-1,i,j,now_count,r_len,count=0;
	char file_name[7][150];
	char object[1024];
	memset(fd,-1,sizeof(fd));
	for(i = 0,j=0 ; i < pro->arduino_count;i++)
	{
		if(table[i].connect_number == 0 || table[i].tf == false)
		{
			printf("number : %d\n",table[i].connect_number);
			continue;
		}
		sprintf(file_name[i],"%s%s%d_%d",FOLDER_NAME,FILE_NAME,table[i].connect_number,number);
		printf("%s\n",file_name[i]);
		while(fd[j] <0)
		{
			if(table[i].connect_number == 0 || table[i].tf == false)
			{
				break;
			}
			fd[j] = open(file_name[i],O_RDONLY|O_EXCL,0666);
		}
		j++;
	}
	now_count = j;
	if(fd[0] < 0)
	{
		printf("fd none\n");
		return -1;
	}
	printf("now_count = %d\n",now_count);
	while(sfd<0)
	{
		sfd = open(SEND_FILE_NAME,O_CREAT|O_EXCL|O_WRONLY,0666);
		usleep(500*1000);
		if(count++ > 100){
			printf("File open Fail : Send_File\n");
			return -1;
		}
	}
	for(i=0;i<now_count;i++)
	{
		if(fd[i] != 0)
		{
			while((r_len= read(fd[i],object,1024))>0)
			{
				send_msg msg;
				msg.value = object;
				msg.len = r_len;
				write_csv_to_fd(sfd,msg.value,msg.len);
			}
			remove(file_name[i]);
		}
	}
	close(sfd);
}
