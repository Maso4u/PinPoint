# PinPoint
Local landmarks guide app for android. 
This android app is built using Google Maps API platform to provide users the following:
- Display the users current location on the map embedded on the app.
- Display local landmarks filtered based on the user's preferred type of landmark as indicated in their settings.
- Enable users to select a landmark and display information on the selected landmark.
  - Display directions to a selected landmark
  - Calculate the best route between the users current location and the selected landmark
  - Display the estimated time and distance between the users current location and the selected landmark taking into account the mode of transportation that the user has indicated in their settings
  - Display the distance in kilometres or miles, depending on the users settings.
  - Route to the landmark is displayed visually on the embedded map
  - User can "favourite" a landmark and the landmark will then be added into a list of favourite landmarks. (this collection is stored in a firebase database)
- Only display landmarks within the radius indicated by the user in their settings.
