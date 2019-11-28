CC = gcc
LIBS = -lbluetooth -lpthread -lcsv -lproperties -lblue -lsig
INC = -Lcsv_writer -Lproperties_pack -Lbluetooth_pack -Lsig_pack
TARGET = ../program/airbeat.exe 
OBJECT = main_service.o 
all : $(TARGET)
$(TARGET) : $(OBJECT)
	$(CC) -o $@ $^ $(INC) $(LIBS) 


clean:
	rm *.o ../airbeat.exe 
