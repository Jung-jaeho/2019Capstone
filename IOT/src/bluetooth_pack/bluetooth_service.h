#include"../header.h"

#include<bluetooth/bluetooth.h>
#include<bluetooth/hci.h>
#include<bluetooth/hci_lib.h>
#include<bluetooth/rfcomm.h>
#include<bluetooth/l2cap.h>
int scan_bluetooth_addr(char **addrset);
int read_bltooth(int fd, char* object,int size);
int read_wait(int fd);
enum TF{
	false=0,
	true
};
typedef struct bt_table{
	char* str;
	enum TF tf;
}bt_table;


