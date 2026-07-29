# Makefile

.PHONY: build
build:
	./gradlew build

run:
	./gradlew bootRun

.PHONY: jacoco
report:
	./gradlew jacocoTestReport
	open build/reports/jacoco/test/html/index.html

