#!/bin/sh

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z "$host" "$port"; do
  >&2 echo "Esperando que MySQL esté disponible en $host:$port..."
  sleep 2
done

>&2 echo "MySQL está listo - ejecutando aplicación"
exec $cmd