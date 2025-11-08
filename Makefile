# SAPI4 Makefile for Windows (MinGW/MSYS2)
# Usage: make or mingw32-make

# Compiler settings
CXX = g++
CXXFLAGS = -O2 -Wall -std=c++17 -DWIN32_LEAN_AND_MEAN
LDFLAGS = -shared
LIBS = -lole32 -luser32 -luuid -lodbc32 -lodbccp32

# Directories
SPEECH_SDK_PATH = C:/Program\ Files\ \(x86\)/Microsoft\ Speech\ SDK
INCLUDES = -I"$(SPEECH_SDK_PATH)/Include"

# Targets
TARGETS = build/sapi4.dll build/sapi4out.exe build/sapi4limits.exe
SOURCES = sapi4.cpp sapi4out.cpp sapi4limits.cpp

.PHONY: all clean dirs

all: $(TARGETS)

dirs:
	@mkdir -p build

build/sapi4.dll: sapi4.cpp dirs
	$(CXX) $(CXXFLAGS) $(INCLUDES) $(LDFLAGS) -o $@ $< $(LIBS)

build/sapi4out.exe: sapi4out.cpp build/sapi4.dll dirs
	$(CXX) $(CXXFLAGS) $(INCLUDES) -o $@ $< -L./build -lsapi4 $(LIBS)

build/sapi4limits.exe: sapi4limits.cpp build/sapi4.dll dirs
	$(CXX) $(CXXFLAGS) $(INCLUDES) -o $@ $< -L./build -lsapi4 $(LIBS)

clean:
	rm -f build/*.dll build/*.exe

install: all
	@echo "Copy build/sapi4.dll to your application directory or system32"
	@echo "Executables are available at build/sapi4out.exe and build/sapi4limits.exe"

help:
	@echo "SAPI4 Makefile for Windows"
	@echo "Targets:"
	@echo "  all      - Build all components (default)"
	@echo "  clean    - Remove built files"
	@echo "  install  - Show installation instructions"
	@echo "  help     - Show this help"
	@echo ""
	@echo "Note: Requires MinGW/MSYS2 with g++ and the Microsoft Speech SDK"