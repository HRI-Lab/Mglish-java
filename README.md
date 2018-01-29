# Mglish
Eclipse, Java, Watson SDK

## How to Add Testcases

![howto](images/howto1.png)

- put audio file in the resource folder.

![howto](images/howto2.png)

- put some code in the initTestCases() which located in DashboardApp.java

## Results

잡음이 섞인 음성파
```
Speaker 1 : dad		 [0.00 ~ 0.22]
Speaker 1 : I		 [0.22 ~ 0.43]
Speaker 1 : want		 [0.86 ~ 1.19]
Speaker 1 : to		 [1.19 ~ 1.35]
Speaker 1 : send		 [1.35 ~ 1.67]
Speaker 1 : this		 [1.67 ~ 1.83]
Speaker 1 : book		 [1.83 ~ 1.94]
Speaker 1 : to		 [1.94 ~ 2.05]
Speaker 1 : grandma	 [2.05 ~ 2.17]
Speaker 1 : do		 [3.29 ~ 3.44]
Speaker 1 : you		 [3.44 ~ 3.67]
Speaker 1 : have		 [3.67 ~ 3.95]
Speaker 1 : a		 [3.95 ~ 4.05]
Speaker 1 : box		 [4.05 ~ 4.72]
Speaker 2 : yeah		 [5.24 ~ 5.70]
Speaker 2 : I've		 [5.70 ~ 5.83]
Speaker 2 : got		 [5.83 ~ 5.96]
Speaker 2 : this		 [6.61 ~ 6.87]
Speaker 2 : one		 [6.87 ~ 7.15]
Speaker 2 : to		 [7.15 ~ 7.26]
Speaker 2 : put		 [7.26 ~ 7.48]
Speaker 2 : photo	 [7.54 ~ 7.84]
Speaker 2 : albums	 [7.84 ~ 8.28]
Speaker 2 : in		 [8.28 ~ 8.54]
Speaker 2 : but		 [8.54 ~ 8.79]
Speaker 2 : it's		 [9.28 ~ 9.47]
Speaker 2 : a		 [9.47 ~ 9.57]
Speaker 2 : bit		 [9.57 ~ 9.81]
Speaker 2 : small	 [9.81 ~ 10.32]
Speaker 0 : the		 [10.32 ~ 10.50]
Speaker 0 : box		 [10.50 ~ 10.68]
Speaker 1 : looks	 [11.41 ~ 11.70]
Speaker 1 : big		 [11.70 ~ 11.93]
Speaker 1 : enough	 [11.93 ~ 12.26]
Speaker 1 : for		 [12.26 ~ 12.48]
Speaker 1 : the		 [12.48 ~ 12.60]
Speaker 1 : book		 [12.60 ~ 13.03]
Speaker 1 : can		 [13.52 ~ 13.71]
Speaker 1 : I		 [13.71 ~ 13.81]
Speaker 1 : use		 [13.81 ~ 14.11]
Speaker 1 : it		 [14.11 ~ 14.30]

Speaker 1 : dad I want to send this book to grandma do you have a box 
Speaker 2 : yeah I've got this one to put photo albums in but it's a bit small 
Speaker 0 : the box 
Speaker 1 : looks big enough for the book can I use it 
```

원래 음성파일

```
Speaker 1 : dad		 [0.03 ~ 0.54]
Speaker 1 : I		 [0.71 ~ 0.87]
Speaker 1 : want		 [0.87 ~ 1.20]
Speaker 1 : to		 [1.20 ~ 1.36]
Speaker 1 : send		 [1.36 ~ 1.66]
Speaker 1 : this		 [1.66 ~ 1.82]
Speaker 1 : book		 [1.82 ~ 2.10]
Speaker 1 : to		 [2.10 ~ 2.25]
Speaker 1 : grandma	 [2.25 ~ 2.89]
Speaker 1 : do		 [3.22 ~ 3.46]
Speaker 1 : you		 [3.46 ~ 3.67]
Speaker 1 : have		 [3.67 ~ 3.95]
Speaker 1 : a		 [3.95 ~ 4.05]
Speaker 1 : box		 [4.05 ~ 4.75]
Speaker 2 : yeah		 [5.23 ~ 5.74]
Speaker 2 : I've		 [6.03 ~ 6.29]
Speaker 2 : got		 [6.29 ~ 6.60]
Speaker 2 : this		 [6.60 ~ 6.88]
Speaker 2 : one		 [6.88 ~ 7.14]
Speaker 2 : to		 [7.14 ~ 7.27]
Speaker 2 : put		 [7.27 ~ 7.48]
Speaker 2 : photo	 [7.48 ~ 7.84]
Speaker 2 : albums	 [7.84 ~ 8.28]
Speaker 2 : in		 [8.28 ~ 8.59]
Speaker 2 : but		 [8.70 ~ 9.10]
Speaker 2 : it's		 [9.27 ~ 9.47]
Speaker 2 : a		 [9.47 ~ 9.58]
Speaker 2 : bit		 [9.58 ~ 9.81]
Speaker 2 : small	 [9.81 ~ 10.51]
Speaker 1 : the		 [10.83 ~ 10.99]
Speaker 1 : box		 [10.99 ~ 11.42]
Speaker 1 : looks	 [11.42 ~ 11.68]
Speaker 1 : big		 [11.68 ~ 11.95]
Speaker 1 : enough	 [11.95 ~ 12.26]
Speaker 1 : for		 [12.26 ~ 12.48]
Speaker 1 : the		 [12.48 ~ 12.60]
Speaker 1 : book		 [12.60 ~ 13.17]
Speaker 1 : can		 [13.46 ~ 13.71]
Speaker 1 : I		 [13.71 ~ 13.81]
Speaker 1 : use		 [13.81 ~ 14.12]
Speaker 1 : it		 [14.12 ~ 14.45]

Speaker 1 : dad I want to send this book to grandma do you have a box 
Speaker 2 : yeah I've got this one to put photo albums in but it's a bit small 
Speaker 1 : the box looks big enough for the book can I use it 
```
