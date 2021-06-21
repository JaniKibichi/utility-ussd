FROM centos

#Install Java
RUN yum update -y && yum upgrade -y && yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
ENV JAVA_HOME /etc/alternatives/jre

#Install SBT
RUN curl https://bintray.com/sbt/rpm/rpm > bintray-sbt-rpm.repo && mv bintray-sbt-rpm.repo /etc/yum.repos.d && yum install -y sbt
WORKDIR /utility-ussd
ADD . /utility-ussd
EXPOSE 7500/tcp

CMD sbt run
