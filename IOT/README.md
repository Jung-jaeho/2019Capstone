SRC SOURCE
=====
##1. C Language
- HTTP Protocol Connect from fork System Call.
- Bluetooth Serivce use BlueZ Library.
-- <3.0Ver> RFCOMM Connection.
-- <Up to 3.0Ver> Gatt protocol use BlueZ Library.
-- properties-pack : Scan properties.
-- csv-writer-pack : CSV-File make & group file.
-- main-service.c : main & thread & bluetooth connection file.
-- main-service.h : main-service-header.
-- main-shared.h : all programs are use var collection.
-- header.h : header file.
-- bluetooth-pack : bluetooth scan & bluetoothservice file.
-- sig-pack : signal action file & fork & execvp java program.

##2. Java Language
- HTTP Protocol Connect use Apache HTTPClient Library.
- JSON Object send to post.
##3. Arduino System
- Arduino has Temperature,CO,CH4 Sensor, also that has alarm Buzzer, Bluetooth.
- Arduino Programming by Sketch.
##4. file description
-- Sender.jar : send server json that json maed by read line from send csv file.
-- airbeat.service : service file -> copy to etc/systemd/system/ and systemctl enable airbeat.service
-- run.sh : service run file -> copy to /airbeat/
-- airbeat.exe : run file
-- example.properties : example for airbeat program use to properties.
##4. run System
-- mkdir /airbeat/
-- mkdir /airbeat/sensor_csv folder
-- cp example.properties -> change name and copy to /airbeat/system.properties & write information
-- cp airbeat.service -> copy to /etc/systemd/system/
-- cp run.sh -> copy to /airbeat/
-- cp Sender.jar -> copy to /airbeat/
