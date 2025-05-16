FROM openjdk:17
WORKDIR /app

# Сначала копируем Main.java из текущей директории в контейнер
COPY Main.java .

# Потом компилируем
RUN javac Main.java

# И наконец запускаем
ENTRYPOINT ["java", "Main"]