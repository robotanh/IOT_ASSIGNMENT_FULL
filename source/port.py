import cv2
import serial.tools.list_ports   
    
def list_ports():
    """
    Test the ports and returns a tuple with the available ports and the ones that are working.
    """
    non_working_ports = []
    dev_port = 0
    working_ports = []
    available_ports = []
    while len(non_working_ports) < 6: # if there are more than 5 non working ports stop the testing. 
        camera = cv2.VideoCapture(dev_port)
        if not camera.isOpened():
            non_working_ports.append(dev_port)
            print("Port %s is not working." %dev_port)
        else:
            is_reading, img = camera.read()
            w = camera.get(3)
            h = camera.get(4)
            if is_reading:
                print("Port %s is working and reads images (%s x %s)" %(dev_port,h,w))
                working_ports.append(dev_port)
            else:
                print("Port %s for camera ( %s x %s) is present but does not reads." %(dev_port,h,w))
                available_ports.append(dev_port)
        dev_port +=1
    return available_ports,working_ports,non_working_ports

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB Serial Device" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return "COM5"
# def getPort():
#     ports = serial.tools.list_ports.comports()
#     for port in ports:
#         if "COM" in port.description:
#             return port.device
#     return "None"

if getPort()!= "None":
    ser = serial.Serial( port=getPort(), baudrate=115200)
    print(ser)

def processData(client,data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    # splitData = data
    if splitData[1] == "T":
        client.publish("cambien2", splitData[2])
        print("cambien2 = " + splitData[2])
    elif splitData[1] == "H":
        client.publish("cambien3", splitData[2])
        print("cambien3 = " + splitData[2])


mess = ""
def readSerial(client):
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(client, mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]

def writeData(data):
    ser.write(str(data).encode('utf-8'))