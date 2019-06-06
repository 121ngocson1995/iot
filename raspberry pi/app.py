import RPi.GPIO as GPIO
import serial
from time import sleep
from firebase import firebase
from alarm import enableAlarm

# Set up alarm components
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

buzzer = 23
led = 18
GPIO.setup(buzzer,GPIO.OUT)
GPIO.setup(led,GPIO.OUT)

# Set up inital values for alarm components
GPIO.output(led,GPIO.LOW)
GPIO.output(buzzer,GPIO.LOW)

# Set up firebase connection
ser = serial.Serial('/dev/ttyUSB0',9600)
firebase = firebase.FirebaseApplication('https://mqtt-e9aac.firebaseio.com/')
cloudData = firebase.get('/', None)
door_opened = cloudData["opened"]
door_locked = cloudData["locked"]

ser.flushInput()

while 1:
  ir_read=ser.readline().strip().decode()
  print(ir_read + "  " + door_opened)

  # if door has just been opened while status is closed
  if int(ir_read) > 900 and door_opened == "false":
    # fetch data again to make sure the offline status is up-to-date
    cloudData = firebase.get('/', None)

    # if the status is correct
    if door_opened == cloudData['opened']:
      # set door status on cloud as opened
      firebase.put_async('/', 'opened', "true")

      # if the door is locked, set alarm status on cloud as ringing
      if cloudData['locked'] == 'true':
        # Alarm goes off
        enableAlarm(buzzer, led, firebase)

    # update offline data
    door_opened = "true"

  # if door has just been closed while status is opened
  elif int(ir_read) <=900 and door_opened == "true":
    # set door status on cloud as opened
    firebase.put_async('/', "opened", "false")

    # update offline data
    door_opened = "false"
