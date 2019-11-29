CC = gcc
CC2 = ar
TARGET = libblue.a 

$(TARGET) : bluetooth_service.o 
	$(CC2) rscv libblue.a bluetooth_service.o

bluetooth_service.o : 
	$(CC) -c bluetooth_service.c

clean:
	rm *.o *.a 
