CC=gcc
CC2=ar
TARGET=libairbeat.a 

$(TARGET) : sig_service.o bluetooth_service.o csv_writer.o properties_reader.o
	$(CC2) rscv $(TARGET) sig_service.o bluetooth_service.o csv_writer.o properties_reader.o

sig_service.o : 
	$(CC) -c ../sig_pack/sig_service.c
bluetooth_service.o:
	$(CC) -c ../bluetooth_pack/bluetooth_service.c
csv_writer.o :
	$(CC) -c ../csv_writer/csv_writer.c
properties_reader.o:
	$(CC) -c ../properties_pack/properties_reader.c
clean:
	rm *.o *.a 
