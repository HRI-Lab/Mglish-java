# Mglish
Eclipse, Java, Watson SDK

## How to Add Testcases

![howto](images/howto1.png)

- put audio file in the resource folder.

![howto](images/howto2.png)

- put some code in the initTestCases() which located in DashboardApp.java

## Output

```
1월 25, 2018 12:53:21 오전 okhttp3.internal.platform.Platform log
정보: --> POST https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?timestamps=true http/1.1 (355270-byte body)
1월 25, 2018 12:53:27 오전 okhttp3.internal.platform.Platform log
정보: <-- 200 OK https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?timestamps=true (5873ms, unknown-length body)
0
I want to send this Ranma do you have a box yeah I found this one to put the photo albums in five it's a bit small
dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it
Wrong Word : 5 : Ranma 
Wrong Word : 12 : I 
Wrong Word : 13 : found 
Wrong Word : 18 : the 
Wrong Word : 22 : five 
Wrong Words Index : [5, 12, 13, 18, 22]

0 : dad
Time : 0.0 ~ 0.75
1 : I
Time : 0.75 ~ 0.86
2 : want
Time : 0.86 ~ 1.19
3 : to
Time : 1.19 ~ 1.35
4 : send
Time : 1.35 ~ 1.67
5 : this
Time : 1.67 ~ 1.83
6 : book
Time : 1.83 ~ 2.316666666666667
7 : to
Time : 2.316666666666667 ~ 2.8033333333333337
8 : grandma
Time : 2.8033333333333337 ~ 3.29
9 : do
Time : 3.29 ~ 3.44
10 : you
Time : 3.44 ~ 3.67
11 : have
Time : 3.67 ~ 3.95
12 : a
Time : 3.95 ~ 4.05
13 : box
Time : 4.05 ~ 4.72
14 : yeah
Time : 5.24 ~ 5.7
15 : I've
Time : 5.7 ~ 6.155
16 : got
Time : 6.155 ~ 6.61
17 : this
Time : 6.61 ~ 6.87
18 : one
Time : 6.87 ~ 7.15
19 : to
Time : 7.15 ~ 7.26
20 : put
Time : 7.26 ~ 7.48
21 : photo
Time : 7.54 ~ 7.84
22 : albums
Time : 7.84 ~ 8.28
23 : in
Time : 8.28 ~ 8.54
24 : but
Time : 8.54 ~ 9.28
25 : it's
Time : 9.28 ~ 9.47
26 : a
Time : 9.47 ~ 9.57
27 : bit
Time : 9.57 ~ 9.81
28 : small
Time : 9.81 ~ 10.32

Results from Watson : 
I want to send this Ranma do you have a box yeah I found this one to put the photo albums in five it's a bit small 
Complemented Results : 
dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small 
Answer : 
dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it
```
