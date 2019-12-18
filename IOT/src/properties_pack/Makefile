CC = gcc
CC2 = ar
TARGET = libproperties.a 

$(TARGET) : properties_reader.o 
	$(CC2) rscv $(TARGET) properties_reader.o
properties_reader.o: 
	$(CC) -c properties_reader.c
clean:
	rm *.o *.a
