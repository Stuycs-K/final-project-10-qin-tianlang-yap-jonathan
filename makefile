encode: AES.class
	@java AES $(ARGS)
AES.class: AES.java
	@javac AES.java
hexdump: Hexdump.class
	@java Hexdump $(ARGS)
Hexdump.class: Hexdump.java
	@javac Hexdump.java