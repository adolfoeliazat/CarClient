## Commands
Each command starts from **$** and contains a few bytes.  
All values are binary, so command $x where x = 96 will look like $a (ascii code of a is 96) but not $96.  

### Control command
This command is being sent to car   
**$mslh**   
**m** main motor. It consists of 8 bits: **dbxxxxxx**.  
* **d** dirrection. 1 - forward, 0 - backward  
* **s** brake. 1 - enabled, 0 - disabled  
* **xxxxxx** 6 bits indicating throttle level. 0 (000000) - min throttle, 63 - max (111111)  

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
