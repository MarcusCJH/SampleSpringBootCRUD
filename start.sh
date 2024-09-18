#!/bin/bash

# For Backend Crud-App
sudo yum install java-17-amazon-corretto-devel git tmux wget -y
sudo yum install maven -y
CLOUDWATCH_AGENT_DOWNLOAD_URL="https://amazoncloudwatch-agent.s3.amazonaws.com/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm"
wget $CLOUDWATCH_AGENT_DOWNLOAD_URL
sudo rpm -U ./amazon-cloudwatch-agent.rpm

rm -rf SampleSpringBootCRUD
git clone https://github.com/MarcusCJH/SampleSpringBootCRUD.git
cd SampleSpringBootCRUD
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:cloudwatch-agent.json
mvn compile
mvn package

export AWS_ADOT_JAVA_INSTRUMENTATION_PATH=./aws-opentelemetry-agent.jar
JAVA_TOOL_OPTIONS=" -javaagent:$AWS_ADOT_JAVA_INSTRUMENTATION_PATH" \
OTEL_METRICS_EXPORTER=none \
OTEL_LOGS_EXPORT=none \
OTEL_AWS_APPLICATION_SIGNALS_ENABLED=true \
OTEL_AWS_APPLICATION_SIGNALS_EXPORTER_ENDPOINT=http://localhost:4316/v1/metrics \
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf \
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4316/v1/traces \
OTEL_RESOURCE_ATTRIBUTES="service.name=crud-demo" \
nohup java -jar target/crud-demo-0.0.1-SNAPSHOT.jar > springboot.log 2>&1 &


#For Web Application  1
sudo yum update -y
sudo yum install httpd -y
sudo yum install git -y
CLOUDWATCH_AGENT_DOWNLOAD_URL="https://amazoncloudwatch-agent.s3.amazonaws.com/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm"
wget $CLOUDWATCH_AGENT_DOWNLOAD_URL
sudo rpm -U ./amazon-cloudwatch-agent.rpm
rm -rf SampleSpringBootCRUD
git clone https://github.com/MarcusCJH/SampleSpringBootCRUD.git
cd SampleSpringBootCRUD
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:cloudwatch-agent.json
cd SampleSpringBootCRUD/src/main/resources/html
sudo cp -r * /var/www/html/
sudo systemctl start httpd
sudo systemctl enable httpd


# Define the jar file name or a unique identifier for your Spring Boot application
JAR_NAME="crud-demo-0.0.1-SNAPSHOT.jar"

# Find the PID of the Java process running the Spring Boot application
PID=$(ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}')

# Check if the process is running
if [ -z "$PID" ]; then
  echo "No process found for $JAR_NAME."
else
  echo "Killing process $PID for $JAR_NAME..."
  # Kill the process
  kill $PID

  # Optionally, force kill if it's not stopping
  sleep 5  # Give some time for the process to terminate gracefully
  if ps -p $PID > /dev/null; then
    echo "Process $PID did not terminate, force killing..."
    kill -9 $PID
  fi
  echo "Process $PID killed successfully."
fi