have_config := $(wildcard config.mk)
ifneq ($(strip $(have_config)),)
include config.mk
endif

JDK_BASE ?= /opt/sun-jdk-1.6.0.29
J2ME_BASE ?= /opt/sun-j2me-bin-2.5.2.01

JAVAC ?= $(JDK_BASE)/bin/javac
PREVERIFY ?= $(J2ME_BASE)/bin/preverify

JAVA_CFLAGS=-bootclasspath "$(J2ME_BASE)/lib/midpapi20.jar:$(J2ME_BASE)/lib/cldcapi10.jar"  \
						-target 1.3                                                                     \
						-source 1.3                                                                     \
						-d compiled                                                                     \
						-classpath compiled                                                             \
						-sourcepath .                                                                   \
						-g
PREVERIFY_CLASSPATH=$(J2ME_BASE)/lib/midpapi20.jar:$(J2ME_BASE)/lib/cldcapi10.jar

TARGET="Gay-fi"
MAIN_CLASS="Gayfi"
VERIFIED = verified
SRC = org/bouncycastle/crypto/util/Pack.java \
	org/bouncycastle/crypto/Digest.java \
	org/bouncycastle/crypto/ExtendedDigest.java \
	org/bouncycastle/crypto/digests/GeneralDigest.java \
	org/bouncycastle/crypto/digests/MD5Digest.java \
	org/bouncycastle/crypto/digests/SHA256Digest.java \
	crypt/GeneralDigest.java \
	crypt/MD5.java \
	crypt/SHA256.java \
	crypt/Hashword.java \
	gayfi/MIDlet.java \
	gayfi/Service.java \
	gayfi/services/AliceAGPF.java \
	gayfi/services/FastwebPirelli.java \
	gayfi/services/FastwebTelsey.java \
	Gayfi.java

CLASSES = $(patsubst %, $(VERIFIED)/%, $(SRC:.java=.class))

all: $(TARGET).jar $(TARGET).jad

run: $(TARGET).jad
	$(J2ME_BASE)/bin/emulator -Xdescriptor:$(TARGET).jad

$(TARGET).jar: verified compiled Manifest $(CLASSES)
	@echo JAR $(TARGET).jar
	@jar cfm $(TARGET).jar Manifest icon.png res -C verified .

compiled:
	@mkdir -p compiled

verified:
	@mkdir -p verified

$(CLASSES): $(VERIFIED)/%.class: %.java
	@echo JAVAC $<
	@$(JAVAC) $(JAVA_CFLAGS) $<
	@$(PREVERIFY) -classpath $(PREVERIFY_CLASSPATH):compiled -d verified $*

$(VERIFIED)/gayfi/services/AliceAGPF.class: gayfi/services/AliceAGPF.java
	@echo JAVAC $<
	@$(JAVAC) $(JAVA_CFLAGS) $<
	@$(PREVERIFY) -classpath $(PREVERIFY_CLASSPATH):compiled -d verified gayfi/services/AliceAGPF
	@$(PREVERIFY) -classpath $(PREVERIFY_CLASSPATH):compiled -d verified 'gayfi/services/AliceAGPF$$Series'


%.jad: %.jar
	@echo JAD $<
	@unzip -aa -j -p $< "META-INF/MANIFEST.MF" > $@
	@echo "MIDlet-Jar-URL: $<" >> $@
	@echo "MIDlet-Jar-Size: " `stat -c%s $<` >> $@

clean:
	@echo Cleaning...
	@rm -Rf compiled verified
	@rm -f $(TARGET).jar *.jad

.PHONY: clean all run
