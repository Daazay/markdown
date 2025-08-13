@echo off

set JFLEX_JAR=lib\jflex-full-1.9.1.jar
set FLEX_FILE=src\commonMain\kotlin\org\intellij\markdown\flavours\custom\lexer\custom.flex
set OUTPUT_DIR=src\commonMain\kotlin\org\intellij\markdown\flavours\custom\lexer

REM Run JFlex
echo Generating lexer from %FLEX_FILE%
java -jar %JFLEX_JAR% %FLEX_FILE% --outdir %OUTPUT_DIR%

echo Done.
pause