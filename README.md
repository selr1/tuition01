
# Tuition01 System - Iman 2428591

## Assignment 2 (tuition01/gui/)

Please note this report is only for assignment 2 which is the GUI part, only within the /gui/ folder.

## Reflections

I succesfully made the login ui for our system and for now i only put Tuition01 as the name, also only students login is accepted and the main screen ui
is the timetable. What i find dificult the most is the styling , thats why i also uploaded the no styling version on my github repo.
I also uses only 2 .json files as simple database for login credentials and timetable details, so users can edit the files themselves and view the changes directly and easily. 



---

## Screenshots

### Login
![Screenshot 1](2026-01-04-230758_hyprshot.png)

### Timetable
![Screenshot 2](2026-01-04-230808_hyprshot.png)

## .json

[users](gui/users.json)
```
[
  {
    "username": "student",
    "password": "123",
    "role": "student"
  },
  {
    "username": "tutor",
    "password": "123",
    "role": "tutor"
  }
]
```

[timetable](gui/timetable.json)
```
[
  { "day": 1, "startHour": 9, "duration": 2, "subject": "Object Oriented Programming", "tutor": "Dr. Iman" },
  { "day": 3, "startHour": 14, "duration": 2, "subject": "Discrete Structures", "tutor": "Prof. Iman" },
  { "day": 5, "startHour": 10, "duration": 3, "subject": "System Analysis and Design", "tutor": "Mr. Iman" },
  { "day": 2, "startHour": 11, "duration": 1, "subject": "UNGS", "tutor": "Ms. Nur Iman" }
]
```
