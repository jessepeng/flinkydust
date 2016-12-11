echo off
echo Usage: startup.sh ^<port^>
echo.

if %1.==. (
	echo Open your browser and go to http://localhost/
) else (
	echo Open your browser and go to http://localhost:%1/
)

echo.
echo Starting server...
echo.

java -classpath "flinkydust-server-2.0.war;../lib/*;" de.hu.flinkydust.server.main.Server %1
