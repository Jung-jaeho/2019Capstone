CC = gcc
CC2 = ar
TARGET = libcsv.a 

libcsv.a : csv_writer.o 
	ar rscv libcsv.a csv_writer.o

csv_writer.o : 
	gcc -c csv_writer.c


