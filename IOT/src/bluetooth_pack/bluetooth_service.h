
#ifndef _GLOBAL_H

#include"../header.h"
#include"../main_shared.h"

#endif

#include<bluetooth/bluetooth.h>
#include<bluetooth/hci.h>
#include<bluetooth/hci_lib.h>
#include<bluetooth/rfcomm.h>
#include<bluetooth/l2cap.h>
int scan_bluetooth_addr(char **addrset);
int read_bltooth(int fd, char* object,int size);
int read_wait(int fd);
