FROM @{docker.registry}/itom-itsma-openjdk-suse-base:1.0.0.007

# File Author / Maintainer
MAINTAINER libin.tian <libin.tian@hpe.com>

ENV http_proxy @{http.proxy}

#install wget
RUN zypper --non-interactive install wget tar \
 && zypper clean

# install Nodejs 6.91
RUN wget http://nodejs.org/dist/v6.9.1/node-v6.9.1-linux-x64.tar.gz -P /app \
 && tar -vzxf /app/node-v6.9.1-linux-x64.tar.gz -C /app \
 && rm -rf /app/node-v6.9.1-linux-x64.tar.gz

#set node environment
ENV NODE_HOME /app/node-v6.9.1-linux-x64/bin
ENV PATH $NODE_HOME:$PATH
ENV http_proxy ""

RUN mkdir -p /app/itsma-deployer/config/2017.07/ \
        /app/itsma-deployer/config/2017.11/ \
        /app/itsma-deployer/config/yaml_templates

COPY deployer-ui.tar.gz /app/
COPY itsmaProducts.json /app/itsma-deployer/config/
COPY 2017.07/* /app/itsma-deployer/config/2017.07/
COPY 2017.11/* /app/itsma-deployer/config/2017.11/
COPY *.yaml /app/itsma-deployer/config/yaml_templates/
COPY deployer-backend.jar /app/itsma-deployer/deployer-backend.jar
COPY run.sh /app/

RUN tar -C /app -xzf /app/deployer-ui.tar.gz \
    && rm -rf /app/deployer-ui.tar.gz \
    && chmod 777 /app/run.sh

EXPOSE 8080

WORKDIR /app

CMD ["/app/run.sh"]
