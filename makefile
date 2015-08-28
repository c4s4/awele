NAME=awele
VERSION=$(shell changelog release version)
COPYRIGHT=Awele (C) Michel CASABIANCA 1996-2015
BUILD_DIR=build
SOURCE_DIR=src
PACKAGE=net/sweetohm/awele

YELLOW=\033[1m\033[93m
CYAN=\033[1m\033[96m
CLEAR=\033[0m

.PHONY: build

help:
	@echo "$(YELLOW)Help page$(CLEAR)"
	@echo "$(CYAN)help$(CLEAR)  Print this help page"
	@echo "$(CYAN)const$(CLEAR) Generate constant source file"
	@echo "$(CYAN)build$(CLEAR) Build class files"
	@echo "$(CYAN)jar$(CLEAR)   Generate JAR file"
	@echo "$(CYAN)run$(CLEAR)   Run application"
	@echo "$(CYAN)clean$(CLEAR) Clean generated files"

const:
	@echo "$(YELLOW)Generating constant source file$(CLEAR)"
	sed -e "s/@VERSION@/$(VERSION)/" -e "s/@COPYRIGHT@/$(COPYRIGHT)/" \
		$(SOURCE_DIR)/$(PACKAGE)/Constants > $(SOURCE_DIR)/$(PACKAGE)/Constants.java

build: const
	@echo "$(YELLOW)Building class files$(CLEAR)"
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) `find $(SOURCE_DIR) -name *.java`
	cp $(SOURCE_DIR)/$(PACKAGE)/*.gif $(BUILD_DIR)/$(PACKAGE)/

jar: clean build
	@echo "$(YELLOW)Generating JAR file$(CLEAR)"
	cd $(BUILD_DIR) && jar cvfm $(NAME)-$(VERSION).jar ../$(SOURCE_DIR)/manifest *

run: jar
	@echo "$(YELLOW)Runing application$(CLEAR)"
	java -jar $(BUILD_DIR)/$(NAME)-$(VERSION).jar

clean:
	@echo "$(YELLOW)Cleaning generated files$(CLEAR)"
	rm -rf $(BUILD_DIR)
