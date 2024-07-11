MFC ?= 0

BUILD_DIR 	:= ./build
CACHE_DIR 	:= ./.cache

#=========================================================
#======================== firtool ========================
#=========================================================
FIRTOOL_VERSION := 1.62.0
FIRTOOL_URL := https://github.com/llvm/circt/releases/download/firtool-$(FIRTOOL_VERSION)/firrtl-bin-linux-x64.tar.gz
CACHE_FIRTOOL_PATH := $(CACHE_DIR)/firtool-$(FIRTOOL_VERSION)/bin/firtool
ifeq ($(MFC),1)
ifeq ($(wildcard $(CACHE_FIRTOOL_PATH)),)
$(info [INFO] Downloading from $(FIRTOOL_URL))
$(shell mkdir -p $(CACHE_DIR) && curl -L $(FIRTOOL_URL) | tar -xzC $(CACHE_DIR))
endif
MFC_ARGS := --firtool-binary-path $(CACHE_FIRTOOL_PATH)
CHISEL_VERSION  := chisel
else
CHISEL_VERSION  := chisel3
endif

PRJ_NAME := playground[$(CHISEL_VERSION)]
MAIN_CLASS := top.Elaborate

test:
	mill -i $(PRJ_NAME).test

verilog:
	mkdir -p $(BUILD_DIR)
	mill -i $(PRJ_NAME).runMain $(MAIN_CLASS) -td $(BUILD_DIR) $(MFC_ARGS)

%Main:
	mill -i $(PRJ_NAME).runMain $@

help:
	mill -i $(PRJ_NAME).runMain $(MAIN_CLASS) --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	-rm -rf $(BUILD_DIR)

distclean: clean
	-rm -rf  out
	-rm -rf .metals
	-rm -rf .bloop
	-rm -rf $(CACHE_DIR)

.PHONY: test verilog help compile bsp reformat checkformat clean distclean