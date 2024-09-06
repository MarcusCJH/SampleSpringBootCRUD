```
sudo yum install java-17-amazon-corretto-devel git tmux wget -y
CLOUDWATCH_AGENT_DOWNLOAD_URL="https://amazoncloudwatch-agent.s3.amazonaws.com/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm"
JAVA_INSTRUMENTATION_AGENT_DOWNLOAD_URL="https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar"
wget $CLOUDWATCH_AGENT_DOWNLOAD_URL
sudo rpm -U ./amazon-cloudwatch-agent.rpm
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:cloudwatch-agent.json