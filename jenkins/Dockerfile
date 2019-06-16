from jenkins/jenkins:lts

# Install plugins
RUN /usr/local/bin/install-plugins.sh greenballs
RUN /usr/local/bin/install-plugins.sh pipeline-model-definition
RUN /usr/local/bin/install-plugins.sh git
RUN /usr/local/bin/install-plugins.sh ws-cleanup
RUN /usr/local/bin/install-plugins.sh m2release

USER root

# Install Maven
RUN wget https://www-us.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz -P /tmp
RUN tar xf /tmp/apache-maven-*.tar.gz -C /opt

# Install AdoptOpenJDK 10
RUN wget https://github.com/AdoptOpenJDK/openjdk10-binaries/releases/download/jdk-10.0.2%2B13/OpenJDK10U-jdk_x64_linux_hotspot_10.0.2_13.tar.gz -P /tmp
RUN tar xf /tmp/OpenJDK10U-jdk_x64_linux_hotspot_10.0.2_13.tar.gz -C /opt

# Install kubectl
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
RUN chmod +x ./kubectl
RUN mv ./kubectl /usr/local/bin/kubectl

# Install docker
RUN curl -fsSLO https://download.docker.com/linux/static/stable/x86_64/docker-18.06.3-ce.tgz \
  && tar xzvf docker-18.06.3-ce.tgz --strip 1 -C /opt \
  && rm docker-18.06.3-ce.tgz

USER jenkins