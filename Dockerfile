ARG APP_INSIGHTS_AGENT_VERSION=3.4.15
ARG PLATFORM=""
# Application image

FROM hmctspublic.azurecr.io/base/java${PLATFORM}:17-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/rd-commondata-api.jar /opt/app/

EXPOSE 4550
CMD [ "rd-commondata-api.jar" ]
