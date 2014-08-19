StegExpose
==========

Description
-----------
StegExpose is a steganalysis tool specialized in detecting LSB (least significant bit) steganography in lossless images such as PNG and BMP. It has a command line interface and is designed to analyse images in bulk while providing reporting capabilities and customization which is comprehensible for non forensic experts. StegExpose rating algorithm is derived from an intelligent and thoroughly tested combination of pre-existing pixel based staganalysis methods including Sample Pairs by Dumitrescu (2003), RS Analysis by Fridrich (2001), Chi Square Attack by Westfeld (2000) and Primary Sets by Dumitrescu (2002). In addition to detecting the presence of steganography, StegExpose also features the quantitative steganalysis (determining the length of the hidden message).

For more information, please download the StegExpose research paper (comming soon..)

Usage
-----
*java -jar StegExpose.jar [directory] [speed] [threshold] [csv file]*

where

*[directory]* - directory containing images to be diagnosed

*[speed]* - Optional. Can be set to 'default' or 'fast' (set to 'default if left blank). default mode will try and run all detectors whereas fast mode will skip the expensive detectors in case cheap detectors are able to determine if a file is clean.

*[threshold]* - Optional. The default value here is 0.2 (for both speed modes) and determines the the level at which files are considered to be hiding data or not. A floating point value between 0 and 1 can be used here to update the threshold. If keeping false positives at bay is of priority, set the threshold slightly higher ~0.25. If reducing false negatives is more important, set the threshold slightly lower ~0.15

*[csv file]* - Optional. Name of the csv (comma separated value) file that is to be generated. that If left blank, the program will simply output to the console. 

Example
------
Basic usage of Stegexpose, providing a directory of images as the only argument

*java -jar StegExpose.jar testFolder*

Produce a steganalytic report in the form of a csv file named 'steganalysisOfTestFolder'

*java -jar StegExpose.jar testFolder default default steganalysisOfTestFolder*

Updating the threshold and running the program in fast mode to save time.

*java - jar StegExpose testFolder fast 0.3*


Bugs
----
Component detectors do not all generate results for all images. This bug is present in the reused source code listed below. This bug impacts the speed of the fast mode as well as the accuracy of both fast and standard modes of StegExpose.

Reused source code
--------
Faure, Bastien (2013). simple-steganalysis-suite. com/p/simple-steganalysis-suite/

Hempstalk, Kathryn (2006). Digital Invisible Ink Toolkit. sourceforge.net/.
