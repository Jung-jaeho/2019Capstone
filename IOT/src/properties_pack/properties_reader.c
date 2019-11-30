#include"read_properties.h"
properties_value* read_properties()
{
	int fd,i,j,k,p=0,length,count,ar_count;
	char *property[4] = {SIRIAL_NUMBER,SEND_SERVER,ARDUINO_COUNT,ARDUINO_MAC};
	char value[1024];
	properties_value *pv;
	pv = (properties_value*)malloc(sizeof(properties_value));
	fd = open(FILE_PATH,O_RDONLY);
	if(fd <0)
	{
		exit(0);
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
				pv->sirial_number =(char*)malloc(strlen(object[i]+j+1));
				strcpy(pv->sirial_number,object[i]+j+1);
				printf("Raspberry Pi : %s\n",pv->sirial_number);
				break;
			case 1:
				pv->send_server =(char*)malloc(strlen(object[i]+j+1));
				strcpy(pv->send_server,object[i]+j+1);
				printf("Server : %s\n",pv->send_server);
				break;
			case 2:
				ar_count = atoi(object[i]+j+1);
				printf("arduino Count : %d\n",ar_count);
				pv->arduino_mac = (char**)malloc(sizeof(char*)*ar_count);
				pv->arduino_count = ar_count;
				break;
			case 3:
				pv->arduino_mac[p] = (char*)malloc(sizeof(char)*22);
				sprintf(pv->arduino_mac[p],"%c,%s",'A'+p,(object[i]+j+1));
				printf("Arduino_: %s\n",pv->arduino_mac[p]);
				p+=1;
			default :
				break;
		}
	}
	return pv;
}
void write_properties(properties_value* object)
{
	char *property[4] = {SIRIAL_NUMBER,SEND_SERVER,ARDUINO_COUNT,ARDUINO_MAC};
	int fd = open(FILE_PATH,O_CREAT|O_WRONLY|O_TRUNC,0777);
	int i;
	char **ar_buf;
	
	ar_buf = (char**)malloc(sizeof(char*) * ((object->arduino_count)+3));
	for(i = 0 ; i<(object->arduino_count)+3;i++)
	{
		ar_buf[i] = (char*)malloc(200);
	}
	sprintf(ar_buf[0],"%s=%s\n",property[0],object->sirial_number);
	sprintf(ar_buf[1],"%s=%s\n",property[1],object->send_server);
	sprintf(ar_buf[2],"%s=%d\n",property[2],object->arduino_count);
	for(i = 3; i <(object->arduino_count)+3 ;i++)
	{
		sprintf(ar_buf[i],"%s=%s\n",property[3],object->arduino_mac[i]);
	}
	for(i = 0 ; i< (object->arduino_count)+3 ;i++)
	{
		write(fd,ar_buf[i],strlen(ar_buf[i]));
		printf("%s\n",ar_buf[i]);
	}
}
