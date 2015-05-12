NAME=awele
VERSION=1.0.0
COPYRIGHT=Awele (C) Michel CASABIANCA 1996-2015
SOURCE_DIR=src
BUILD_DIR=build
PACKAGE=net/sweetohm/awele

.PHONY: build

all: jar

clean:
	rm -rf $(BUILD_DIR)

const:
	sed -e "s/@VERSION@/$(VERSION)/" -e "s/@COPYRIGHT@/$(COPYRIGHT)/" \
		$(SOURCE_DIR)/$(PACKAGE)/Constants > $(SOURCE_DIR)/$(PACKAGE)/Constants.java

build: const
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) `find $(SOURCE_DIR) -name *.java`
	cp $(SOURCE_DIR)/$(PACKAGE)/*.gif $(BUILD_DIR)/$(PACKAGE)/

jar: clean build
	cd $(BUILD_DIR) && jar cvfm $(NAME)-$(VERSION).jar ../$(SOURCE_DIR)/manifest *

run: jar
	java -jar $(BUILD_DIR)/$(NAME)-$(VERSION).jar
