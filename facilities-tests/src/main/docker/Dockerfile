FROM ghcr.io/department-of-veterans-affairs/health-apis-docker-octopus/dev-tools:mvn-3.8-jdk-17

COPY maven/ /sentinel
RUN chmod 755 /sentinel/*sh
ENTRYPOINT ["/sentinel/entrypoint.sh"]
CMD []
