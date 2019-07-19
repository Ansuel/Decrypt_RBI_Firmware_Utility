# Decrypt_RBI_Firmware_Utility

If `java.lang.OutOfMemoryError: Java heap space` error occurs increase heap space to at least twice the size of the inflated firmware image.
eg `java -Xmx384m -jar Decrypt_RBI_Firmware_Utility.jar` works with `vant-y_CRF691-17.2.0188-820-RA.rbi`

To add osck key change the file here:
https://github.com/Ansuel/Decrypt_RBI_Firmware_Utility/blob/master/src/decrypt_rbi/board.java
