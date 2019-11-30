CC=gcc
CC2=ar
TARGET=libsig.a 

$(TARGET) : sig_service.o 
	$(CC2) rscv $(TARGET) sig_service.o

sig_service.o : 
	$(CC) -c sig_service.c

clean:
	rm *.o *.a 
