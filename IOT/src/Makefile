CC = gcc
LIBS = -lbluetooth -lpthread -lcsv
INC = -Lcsv_writer
TARGET = airbeat.exe 
OBJECT = main_service.o 
all : $(TARGET)
$(TARGET) : $(OBJECT)
	$(CC) -o $@ $^ $(INC) $(LIBS) 


