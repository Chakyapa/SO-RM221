FROM openjdk:17-slim

WORKDIR /app

COPY out/artifacts/BarberShopService_jar/BarberShopService.jar /app/barbershop_service/
COPY out/artifacts/ClientService_jar/ClientService.jar /app/client_service/

EXPOSE 8080

RUN echo '#!/bin/sh\n\
if [ "$SERVICE_TYPE" = "barbershop_service" ]; then\n\
    cd /app/barbershop_service && java -jar BarberShopService.jar\n\
elif [ "$SERVICE_TYPE" = "client_service" ]; then\n\
    cd /app/client_service && java -jar ClientService.jar\n\
else\n\
    echo "You need to indicate SERVICE_TYPE=barbershop_service or SERVICE_TYPE=client_service"\n\
    exit 1\n\
fi' > /app/start.sh

RUN chmod +x /app/start.sh

CMD ["/app/start.sh"]