Commented classes: Main, Vectorio, UnitVector, Ball, Collision, Line

For interesting stuff, read the comments for handle() in Collision, and checkCollision() in Line

Shortcuts: With all the balls stopped, press 's' to remove balls. Balls are removed in order of proximity to the cue ball, starting with the closest ball. Cue ball and 8 ball cannot be removed.

Known bugs/issues:

If a ball is touching a wall and is hit EXACTLY parallel to it, it will slow down and stop. Has only occurred twice, and one time was in class :(.

For very slight collisions, the projection may not detect it, but the actual physics might, or the other way around

If the break is hit very lightly, the rack glitches slightly

If the last of a colour's ball was scored by the opposite player in a legal shot, the colour of the number for the next player doesn't change to black

As a whole, due to difficulty in testing, the rules/winning/coloured number system may have other bugs

The pocket "hit boxes" are such that some balls may appear to hover over the hole without scoring

Slow moving balls can appear to clip into walls/corners, though this doesn't noticeably impact physics. Rarely, they can come to rest while clipped into the walls; they can simply be hit out from this position as normal.

Future work:

Fix the issues above.
Add shot projection that extends all the way to the wall if it does not impact a ball first
Add simple spin (forward and back only)