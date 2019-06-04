import RPi.GPIO as GPIO
from time import sleep
import time

alarm = False

def updateAlarmStatus(response):
  global alarm
  print("response" + response)
  if (response == "false"):
    alarm = False

def enableAlarm(buzzer, led, firebase):
  global alarm
  alarm = True

  while alarm == True:
    print("alarm" + str(alarm))
    GPIO.output(buzzer,GPIO.HIGH)
    GPIO.output(led,GPIO.HIGH)
    print ("Beep")
    sleep(0.5)
    GPIO.output(buzzer,GPIO.LOW)
    GPIO.output(led,GPIO.LOW)
    print ("No Beep")
    firebase.get_async('/ringingstatus', None, callback=updateAlarmStatus)
    #alarm = firebase.get('/ringingstatus', None)
    sleep(0.5)
