#include"bluetooth_service.h"
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
	if(e_bit != 'E') return 0;
	return rd_length;
}
int read_wait(int fd)
{
	char buf;
	char length[2];
	int count=0;
	int error;
	if((error = read(fd,&buf,1))<0)
	{
		printf("%d\n",error);
		return -2;
	}
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
		int j;
		ba2str(&(info+i)->bdaddr,addr);
		for(j=0;j<pro->arduino_count;j++)
		{
			if(strcmp(addr,pro->arduino_mac[j]+2) == 0)
			{
				addrset[n] = (char*)malloc(sizeof(char)*18);
				strcpy(addrset[n++],addr);
			}
			printf("%s %s\n",addr,name);
		}
	}
	goto DONE;
ERROR_EXIT:
	bt_free(info);
	hci_close_dev(dd);
	return -1;
DONE:
	bt_free(info);
	hci_close_dev(dd);
	return n;
}
int scan_bluetooth_reconnect(char **addrset)
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
		if(strcmp(addr,*(addrset)) == 0)
		{
			*(addrset) = addr;
			return 1;
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
