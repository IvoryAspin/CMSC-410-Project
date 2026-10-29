# CMSC-319-Project
or the semester project i would like to make a calendar with the same look as a physical calendar but instead of having one photo per month have one photo of space ever day with the use of the NASA API and tie it into google calendar with the use of google's calendar API

Ten requirements

- able to create events and set time
- show the daily space photo for each day for up to a year in advance by look at a date you will be shown the space photo from a year ago on that date to show 
- be able to connect to google calendar
- when connected to google calendar it should be able to create and read events
- show be able to see holiday
- login to keep your calendar your own
- on your birthday it will show the picture from your birth date and year 
- be able to set custom backgrounds
- two factor authentication
- have a light and dark mode

An architectural draft telling me how you are going to build it with diagrams
how i plan to build the project is first to get the current date and showing any events that are happening that day that taking up half of the screen and for the other half have the photo of the day from NASA

first have the user enter a date that they want to see by getting the input then if the date is greater then the current date then -1 to the year and show the photo from that date 

this will be done with the NASA apod api to enter a chosen date and get the daily photo for that date

for the calendar part i will be use google calendar api to inferface with it by use the get command to see what is on it and https://developers.google.com/workspace/calendar/api/v3/reference/events these command to create events 
apod_2026-10-22.jpg|700

Pasted image 20261022110736.png
^ a example
