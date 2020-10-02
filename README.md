# EOLCleaner

Eol cleaner removes EOLS from csv file, thus files can be imported into Oracle data base or Excel.

Example file before:
---------------------------------------
Id,Address
1,"Main Street
Warsaw
Poland"

Example file after:
Id,Address
1,"Main Street Warsaw Poland"

Example use:
---------------------------------------
java -Dfile.encoding=UTF8 -jar C:\qif\core\EOLCleaner.jar "in.csv" "out.csv" Y

Do you need drag and drop?
---------------------------------------
Create a *.bat with the content:
java -Dfile.encoding=UTF8 -jar C:\qif\core\EOLCleaner.jar "%1" "%1.noeol"
