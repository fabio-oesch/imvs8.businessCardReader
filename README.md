imvs8.businessCardReader
========================

A project which will be able to read text from business cards.

  


1. Project Setup
-------------------
   Download the Project folder to your machine. 
   
   Create a new Environment Variable: "TESSDATA_PREFIX", and put the Path to the project directory. 
      Tesseract needs to know the location of the folder "tessdata", which contains the trainingfiles.
      
   Run the Program with the VM Arguments "-Dfile.encoding=UTF8"
      This fixed encoding issues on Windows machine.
      
   Run it as a 32 Bit Application
      Tess4j only works if you run it as 32 Bit Process.
