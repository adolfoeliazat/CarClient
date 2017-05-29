## Commands
Each command starts from **$** and contains of a few bytes
### Control command
This command is being sent to car  
**$mslh**  
**m** main motor. -127 for full back, 128 full forward, 0 - neutral  
**s** steering. 0-180 degrees  
**l** light. 0-255  
**h** hash. Special value to ensure data integrity. **h=(m+s+l)%256**  
### Sensors command
This command is being recieved from car  
**$vsd**  
**v** voltage of battery. Absolute value depends on voltage divider and equal to v*k, where k is voltage multiplier  
**s** period of wheel turn. It is being used for velocity calculations. (absolute value is not determined yet)  
**d** distance to nearest object in cm  
Maybe it makes sense to use hash mechanism also  
