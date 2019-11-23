CC = gcc
LIBS = -lbluetooth -lpthread -lcsv -lproperties -lblue
INC = -Lcsv_writer -Lproperties_pack -Lbluetooth_pack
TARGET = ../airbeat.exe 
OBJECT = main_service.o 
all : $(TARGET)
$(TARGET) : $(OBJECT)
	$(CC) -o $@ $^ $(INC) $(LIBS) 


