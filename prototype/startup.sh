echo "Usage: startup.sh <port>"
echo

if [ -z "$1" ]
	then
	echo "Open your browser and go to http://localhost/"
else
	echo "Open your browser and go to http://localhost:$1/"
fi

echo
echo "Starting server..."
echo

java -classpath "flinkydust-server-3.0.war:../lib/*;" de.hu.flinkydust.server.main.Server "$@"
