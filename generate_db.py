# Import required Python libraries
import csv  # For reading and writing CSV files
import random  # For generating random data
from datetime import datetime, timedelta  # For working with dates and times

# These lists contain sample data that we'll use to generate realistic-looking venue information
# Each list represents different aspects of a venue booking system
venue_types = [
    "Conference Room", "Wedding Hall", "Concert Venue", "Meeting Room",
    "Exhibition Space", "Outdoor Garden", "Rooftop Terrace", "Auditorium",
    "Sports Hall", "Theater", "Banquet Hall", "Training Room"
]

venue_names = [
    "Crystal Palace", "The Grand Hall", "Innovation Hub", "Sunset Gardens",
    "Metropolitan Center", "Skyline View", "Ocean Breeze", "Tech Space",
    "Heritage Hall", "Green Valley", "Royal Court", "Business Hub",
    "Urban Loft", "Lighthouse Point", "Downtown Arena"
]

# List of possible facilities that venues might offer
facilities = [
    "WiFi", "Projector", "Sound System", "Catering Services", 
    "Parking", "Air Conditioning", "Stage", "Lighting System",
    "Security Service", "Wheelchair Access", "Breakout Rooms"
]

# Lists for other venue-related data
cities = ["New York", "London", "Tokyo", "Paris", "Sydney", "Singapore", "Dubai", "Toronto"]
event_types = ["Wedding", "Conference", "Meeting", "Concert", "Exhibition", "Training", "Party", "Seminar"]
booking_status = ["Confirmed", "Pending", "Cancelled", "Completed"]

# Helper functions to generate different types of IDs and data
def generate_venue_id():
    """
    Generate a unique venue ID in the format 'VENxxxx' where x is a random digit
    Example: VEN1234
    """
    return f"VEN{random.randint(1000, 9999)}"

def generate_booking_id():
    """
    Generate a unique booking ID in the format 'BKGxxxxx' where x is a random digit
    Example: BKG12345
    """
    return f"BKG{random.randint(10000, 99999)}"

def generate_user_id():
    """
    Generate a unique user ID in the format 'USERxxx' where x is a random digit
    Example: USER123
    """
    return f"USER{random.randint(100, 999)}"

def generate_price():
    """
    Generate a random price between 500 and 5000 with 2 decimal places
    Example: 1234.56
    """
    return round(random.uniform(500, 5000), 2)

def generate_capacity():
    """
    Generate a random venue capacity from predefined options
    Returns one of: 50, 100, 150, 200, 300, 500, or 1000
    """
    return random.choice([50, 100, 150, 200, 300, 500, 1000])

def generate_rating():
    """
    Generate a random rating between 3.5 and 5.0 with one decimal place
    Example: 4.5
    """
    return round(random.uniform(3.5, 5.0), 1)

def generate_date(future=False):
    """
    Generate a random date either in the past year or upcoming year
    Parameters:
        future (bool): If True, generates a date in the next year
                      If False, generates a date in the past year
    Returns:
        str: Date in format 'YYYY-MM-DD'
    """
    if future:
        # For future dates, start from today and go up to 365 days ahead
        start_date = datetime.now()
        end_date = start_date + timedelta(days=365)
    else:
        # For past dates, go back up to 365 days from today
        end_date = datetime.now()
        start_date = end_date - timedelta(days=365)
    
    # Generate a random number of days to add to the start date
    random_days = random.randint(0, 365)
    random_date = start_date + timedelta(days=random_days)
    return random_date.strftime("%Y-%m-%d")  # Convert date to string format

def generate_time_slot():
    """
    Generate a random time slot between 8:00 and 22:00
    Returns a string like '09:00-13:00'
    """
    start_hour = random.randint(8, 20)  # Start between 8 AM and 8 PM
    duration = random.choice([2, 3, 4, 6, 8])  # Event duration in hours
    end_hour = min(start_hour + duration, 22)  # Ensure it doesn't go past 10 PM
    return f"{start_hour:02d}:00-{end_hour:02d}:00"

# Generate venues dataset
venues = []  # List to store all venue dictionaries
venue_ids = []  # Keep track of venue IDs for referencing in bookings

# Create a venue entry for each venue name in our list
for venue_name in venue_names:
    venue_id = generate_venue_id()
    venue_ids.append(venue_id)
    
    # Create a dictionary containing all venue information
    venue = {
        "venue_id": venue_id,
        "name": venue_name,
        "type": random.choice(venue_types),
        "capacity": generate_capacity(),
        "price_per_hour": generate_price(),
        "city": random.choice(cities),
        "facilities": ", ".join(random.sample(facilities, random.randint(3, 6))),  # Pick 3-6 random facilities
        "rating": generate_rating(),
        "is_active": random.choice([True, True, True, False]),  # 75% chance of being active
        "created_date": generate_date()
    }
    venues.append(venue)

# Generate bookings dataset
bookings = []
# Generate 200 random bookings
for _ in range(200):  
    booking_date = generate_date(future=random.choice([True, False]))
    booking = {
        "booking_id": generate_booking_id(),
        "venue_id": random.choice(venue_ids),  # Randomly assign to an existing venue
        "user_id": generate_user_id(),
        "event_type": random.choice(event_types),
        "booking_date": booking_date,
        "time_slot": generate_time_slot(),
        "attendees": random.randint(10, 1000),
        "status": random.choice(booking_status),
        "total_cost": round(random.uniform(1000, 10000), 2),
        "booking_created": generate_date()
    }
    bookings.append(booking)

# Generate reviews dataset
reviews = []
# Generate 150 random reviews
for _ in range(150):
    venue = random.choice(venues)  # Pick a random venue to review
    review = {
        "review_id": f"REV{random.randint(1000, 9999)}",
        "venue_id": venue["venue_id"],
        "user_id": generate_user_id(),
        "rating": generate_rating(),
        "review_date": generate_date(),
        "event_type": random.choice(event_types),
        # Generate a comment by combining event type with a random positive review
        "comment": f"Great {venue['type']} for our {random.choice(event_types).lower()}. " + 
                  random.choice([
                      "Excellent facilities and staff.",
                      "Perfect location and amenities.",
                      "Would highly recommend.",
                      "Will book again.",
                      "Exceeded our expectations."
                  ])
    }
    reviews.append(review)

# Write the generated data to CSV files

# Write venues data to venues.csv
with open('venues.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.DictWriter(file, fieldnames=venues[0].keys())  # Create CSV writer with dictionary field names
    writer.writeheader()  # Write the header row (column names)
    writer.writerows(venues)  # Write all venue data rows

# Write bookings data to bookings.csv
with open('bookings.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.DictWriter(file, fieldnames=bookings[0].keys())
    writer.writeheader()
    writer.writerows(bookings)

# Write reviews data to reviews.csv
with open('reviews.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.DictWriter(file, fieldnames=reviews[0].keys())
    writer.writeheader()
    writer.writerows(reviews)

# Print sample data to see what was generated
print("Sample Venue:")
print(venues[0])
print("\nSample Booking:")
print(bookings[0])
print("\nSample Review:")
print(reviews[0])