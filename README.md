## Flight Booking System

A complete Spring Boot application for airline & flight booking management.

## Overview

This project lets users register airlines, add flight schedules, search flights, book tickets, cancel bookings, and view booking history.

━━ 📄 API Documentation

A full CSV file is included in the repo:
api-documentation.csv

Contains:
Endpoint
Request type
Request/Response body
HTTP codes
Error schema

🏗️ Architecture
controller → service → repository → database
        ↘ dto ↙         ↘ entity ↙

🗂️ Project Structure
src/
 └── main/
      └── java/com/flightapp
            ├── controller
            ├── service
            ├── repository
            ├── entity
            ├── dto
            ├── exceptions
            └── utils
            
## DataBase Schema
<img width="903" height="775" alt="image" src="https://github.com/user-attachments/assets/8716c7e5-5164-4056-8659-379caf4f2fcf" />
