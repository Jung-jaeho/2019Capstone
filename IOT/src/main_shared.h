#define _GLOBAL_H
enum TF{
	false=0,
	true
};
typedef struct send_msg{
	char *value;
	int len;
}send_msg;
typedef struct bt_table{
	char* addr;
	int connect_number;
	enum TF tf;
}bt_table;
typedef struct properties_value{
	char *sirial_number;
	char *send_server;
	int arduino_count;
	char **arduino_mac;
}properties_value;
extern struct bt_table table[7];
extern struct properties_value *pro;
extern unsigned int number;

