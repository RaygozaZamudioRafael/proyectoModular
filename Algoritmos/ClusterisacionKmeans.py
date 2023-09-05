# -*- coding: utf-8 -*-
"""
Created on Mon Aug 28 17:18:37 2023

@author: chida
"""

import numpy as np
import cv2

nombreImg = 'b433dcf3-c57d-4f5e-9116-5aaeecbaef01___GCREC_Bact.Sp 3715'
img = cv2.imread('Z:/ImagenesIA/Bacterial_spot/'+nombreImg+'.JPG')

img2 = img.reshape((-1,3))

img2 = np.float32(img2)

criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10,1.0)

#Clusters
k = 8
attemps = 10

ret,label,center=cv2.kmeans(img2,k,None,criteria,attemps,cv2.KMEANS_PP_CENTERS)

center = np.uint8(center)

res = center[label.flatten()]
res2 = res.reshape((img.shape))
cv2.imwrite('0segmented.jpg',res2)