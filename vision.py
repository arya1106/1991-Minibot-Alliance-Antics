# this is losely based on 
# https://docs.wpilib.org/en/stable/docs/software/vision-processing/wpilibpi/using-cameraserver.html
# refer to it if you need more details, or feel free to ping me and ask questions
# a lot of my opencv questions were on stack overflow, so google was a huge help too

import numpy as np
import cv2 as cv
import time
from cscore import CameraServer as cs
from networktables import NetworkTables, NetworkTablesInstance

# open the camera and set/get it's resolution
# we had some trouble with WPILib's way of getting the camrea feed
# so we used openCV to capture the frames directly
cap = cv.VideoCapture(0)
cap.set(cv.CAP_PROP_FRAME_WIDTH, 192)
cap.set(cv.CAP_PROP_FRAME_HEIGHT, 144)
print("Got video")
x_res = cap.get(cv.CAP_PROP_FRAME_WIDTH)
y_res = cap.get(cv.CAP_PROP_FRAME_HEIGHT)

# network tables setup
ntinst = NetworkTablesInstance.getDefault()
# these two lines are important if you're using the romi, I couldn't get it to work otherwise
ntinst.startClientTeam(0) 
ntinst.startDSClient()

nt = NetworkTables.getTable('vision')

# setup the three feeds that we can watch from smart dashboard
output_blue = cs.getInstance().putVideo('Blue', 192, 144)
output_green = cs.getInstance().putVideo('Green', 192, 144)
output_pink = cs.getInstance().putVideo('Pink', 192, 144)
output_clear = cs.getInstance().putVideo('Clear', 192, 144)

# wait for network tables to start
time.sleep(0.5)

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()
    
    # we do all our processing here
    in_frame = cv.cvtColor(frame, cv.COLOR_BGR2HSV )
    
    # these return binary images which we can use to find contours
    outBlue = cv.inRange(in_frame, (100, 25, 40), (130, 255, 255))
    outGreen = cv.inRange(in_frame, (70, 40, 20), (100, 255, 255))
    outPink = cv.inRange(in_frame, (140, 20, 100), (180, 255, 255))

    # clean up the image with morphological operations
    kernel = np.ones((3,3), np.uint16)
    outBlue = cv.morphologyEx(outBlue, cv.MORPH_OPEN, kernel=kernel, iterations=1)
    outGreen = cv.morphologyEx(outGreen, cv.MORPH_OPEN, kernel=kernel, iterations=1)
    outPink = cv.morphologyEx(outPink, cv.MORPH_OPEN, kernel=kernel, iterations=1)

    # find the countours we need, the first variable here isn't used for what we need and the third one isn't either
    # but they can be useful in some other contsants
    _Blue, blueContours, blueHeirarchy = cv.findContours(outBlue, cv.RETR_LIST, cv.CHAIN_APPROX_NONE)
    _Green, greenContours, greenHeirarchy = cv.findContours(outGreen, cv.RETR_LIST, cv.CHAIN_APPROX_NONE)
    _Pink, pinkContours, pinkHeirarchy = cv.findContours(outPink, cv.RETR_LIST, cv.CHAIN_APPROX_NONE)
    
    center_x_steer = 0
    greenArea = 0
    pinkArea = 0
    capture = False
    stop = False

    # only do this if any contours are actually found
    if len(blueContours) > 0:

        # iterate over the contours to find the largest one
        largestBlue = blueContours[0]
        for contour in blueContours:
            if cv.contourArea(contour) > cv.contourArea(largestBlue):
                largestBlue = contour
        x, y, w, h = cv.boundingRect(largestBlue)
        outBlue = cv.rectangle(outBlue, (x,y), (x+w, y+h), (255,255,255), 1)
        outBlue = cv.circle(outBlue, (int(x+w/2),int(y+h/2)), radius=1, color=(0,0,255), thickness=-1)

        # find how off the center of the bounding rectangle is
        # this outputs a value between -1 and 1 which we put into arcade drive
        center_x_steer = ((x+w/2)-(x_res/2))/(x_res/2) 
    
    if len(greenContours) > 0:
        largestGreeen = greenContours[0]
        for contour in greenContours:
            if cv.contourArea(contour) > cv.contourArea(largestGreeen):
                largestGreeen = contour
        x, y, w, h = cv.boundingRect(largestGreeen)
        outGreen = cv.rectangle(outGreen, (x,y), (x+w, y+h), (255,255,255), 1)
        greenArea = cv.contourArea(largestGreeen)

        # this is probably a really janky way of doing this, but worked reliably for us and
        # this is how we check for the green circle at the end
        # we check for the green area so that we can avoid the small green dots on the field 
        # we check for the y value to make sure it is a little over half the height of the camera
        # since that's where the bounding rectangle was for our camera setup
        # both constants here will need to be adjusted based on your resolution and individual situation
        if(greenArea>575):
            stop = True
    # update the values in network tables
    nt.putNumber('center_x', center_x_steer)
    nt.putNumber('green_area', greenArea)
    nt.putNumber('y', y+h/y_res)
    nt.putBoolean('stop', stop)
    
    # output the frames you need
    output_blue.putFrame(outBlue)
    output_green.putFrame(outGreen)
    output_pink.putFrame(outPink)
    output_clear.putFrame(frame)
    
    # wait a little bit so you don't eat up all the bandwidth
    time.sleep(0.01)
