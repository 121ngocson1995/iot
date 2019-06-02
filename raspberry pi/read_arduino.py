import serial

ser = serial.Serial('/dev/ttyUSB0',9600)

while 1:
  #ser.flushInput()
  read_serial=ser.readline().strip().decode()
  print(read_serial)

ser.close()
