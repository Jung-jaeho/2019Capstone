#include "read_properties.h"
int read_properties(char* argv)
{
	int fd,i,j,k,p=0,length,count,ar_count;
	char *property[4] = {SIRIAL_NUMBER,SEND_SERVER,ARDUINO_COUNT,ARDUINO_MAC};
	char value[1024];
	fd = open(argv,O_RDONLY);
	if(fd <0)
	{
		return -1;
	}
	char* object[100];
	if(read(fd,value,1024)>0)
	{
		for(object[0] = strtok(value,"\n"),count=0;object[count] != NULL; object[++count] = strtok(NULL,"\n"));
	}
	for(i = 0 ; i <count;i++)
	{
		length=strlen(object[i]); 
		for(j = 0 ; j <length;j++)
		{
			if(strncmp(&object[i][j],"=",1) == 0)
			{
				break;
			}
		}
		for(k=0;k<4;k++)
		{
			if(strncmp(property[k],object[i],j-1) ==0)
			{
				break;
			}
		}
		switch(k)
		{
			case 0:
				pro.sirial_number =(char*)malloc(strlen(object[i]+j+1));
				strcpy(pro.sirial_number,object[i]+j+1);
				printf("Raspberry Pi : %s\n",pro.sirial_number);
				break;
			case 1:
				pro.send_server =(char*)malloc(strlen(object[i]+j+1));
				strcpy(pro.send_server,object[i]+j+1);
				printf("Server : %s\n",pro.send_server);
				break;
			case 2:
				ar_count = atoi(object[i]+j+1);
				printf("arduino Count : %d\n",ar_count);
				pro.arduino_mac = (char**)malloc(sizeof(char*)*ar_count);
				break;
			case 3:
				pro.arduino_mac[p] = (char*)malloc(sizeof(char)*22);
				sprintf(pro.arduino_mac[p],"%c,%s",'A'+p,(object[i]+j+1));
				printf("Arduino_: %s\n",pro.arduino_mac[p]);
				p+=1;
			default :
				break;
		}
	}
}
