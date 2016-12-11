echo "Usage: startup.sh <port>"

java -jar flinkydust-server-2.0.war "$@"

if [ -z "$1" ]
	then
	echo "Open your browser and go to http://localhost/"
else
	echo "Open your browser and go to http://localhost:$1/"
fi