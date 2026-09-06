# Hotel Booking System

A simple hotel booking app written in plain Java. No frameworks, no Maven/Gradle,
no dependencies to download — it's just a handful of `.java` files and the JDK's
built-in HTTP server. You run it from the terminal, but you actually *use* it in
your browser.

I built this to cover the basics you'd want from something like this:

- Add and remove rooms (number, type, price per night)
- Book a room for a date range — it works out the total automatically
- Cancel a booking whenever you want
- Won't let you double-book a room for overlapping dates
- Saves everything to disk so you don't lose your data when you restart it
- A dashboard that doesn't look like it was made in 2003

## What you need

Just a JDK (11 or newer works, I used 21 while writing it). Not a JRE — you need
`javac`, so make sure it's the full JDK.

```
java -version
javac -version
```

If the second command says "not found," you've probably only got a JRE installed
and need to grab a proper JDK instead.

## Running it

Easiest way — there's a script for it:

```bash
./run.sh
```

(Windows folks, use `run.bat` instead.)

If you'd rather just do it by hand:

```bash
mkdir -p out
javac -d out src/com/hotel/*.java
java -cp out com.hotel.Server
```

Once it's running you'll see something like:

```
Hotel Booking System is running!
Open your browser at: http://localhost:8080
```

So go open that in your browser. Ctrl+C in the terminal stops it.

## Using it

Pretty self-explanatory once it's open:

- The homepage lists all rooms and whatever bookings are currently active
- Click "Reserve" on a room to book it — put in a name, dates, done
- "Cancel" next to a booking undoes it
- "Add Room" up top lets you create new rooms
- "Remove" on a room card deletes it (won't let you if it still has an active booking)

## Where the data goes

It creates a `data/` folder the first time you run it, with two CSV files:

- `rooms.csv`
- `bookings.csv`

They're plain text, so feel free to peek inside. Every action saves immediately —
nothing sits only in memory. If you ever want to wipe it back to the default
sample rooms, just delete those two files and restart.

## Files, if you're curious

```
hotelbooking/
├── run.sh / run.bat
└── src/com/hotel/
    ├── Server.java        <- entry point, handles all the HTTP routes
    ├── HotelService.java  <- the actual logic: booking, cancelling, saving
    ├── Room.java / RoomType.java
    ├── Booking.java / BookingStatus.java
    ├── View.java           <- builds the HTML pages
    ├── Css.java            <- styling, served at /style.css
    └── HttpUtils.java      <- small helpers for parsing form data etc.
```

## Changing the port

It runs on 8080 by default. If that's taken on your machine, open
`Server.java` and change this line:

```java
private static final int PORT = 8080;
```

to whatever port you want, recompile, and run it again.

## Heads up

I wrote this without being able to actually compile it myself (long story —
my environment only had a JRE, not a full JDK). I went through it carefully by
hand to check for typos and dumb mistakes, but if `javac` complains about
anything when you build it, just send me the error and I'll sort it out.
