# https://stackoverflow.com/a/69534299
# blocks until Kafka is reachable
kafka-topics --bootstrap-server kafka:29092 --list

echo "Creating Kafka topics"
kafka-topics --bootstrap-server kafka:29092 \
  --create --if-not-exists \
  --topic cmd.user-account \
  --replication-factor 1 --partitions 1
kafka-topics --bootstrap-server kafka:29092 \
  --create --if-not-exists \
  --topic fct.user-account \
  --replication-factor 1 --partitions 3

echo "Successfully created the following topics:"
kafka-topics --bootstrap-server kafka:29092 --list
