#### UTILITY USSD APP

##### INTRODUCTION
#### EXPECTED FEATURE SET
The brand expected their user to dial into a quick code e.g. <strong>*483#</strong> and receive their service menu. The menu would be different depending on:
- Country - so language
- Whether the user is registered to the brand's services

The admin user would expect the following:
- Dashboard through which to search for customers and customer payments made
- A downloadable report

The end user would expect to dial the service code:
- Check their account status
- Fund a wallet (theirs or a friends') using mobile money
- Call the customer care center

#### OPERATIONS
##### SET UP THE VM TO RUN DOCKER AND DOCKER-COMPOSE
Here is the shell script that we use to set up our compute instance including adding docker and nginx load balancer.
```
$ echo UPDATE THE VM LIBRARIES ======================================= \
&& sudo apt-get update \
&& echo INSTALL SNAP ================================================= \
&& sudo apt-get install snapd \
&& sudo snap install core; sudo snap refresh core \
&& echo INSTALL GIT ================================================== \
&& sudo apt-get -y install git-all \
&& echo INSTALL DOCKER =============================================== \
&& curl -fsSL https://get.docker.com -o get-docker.sh \
&& sudo sh get-docker.sh \
&& echo INSTALL DOCKER-COMPOSE ======================================= \
&& sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
&& sudo chmod +x /usr/local/bin/docker-compose \
&& echo PURGE APACHE2 ================================================ \
&& sudo service apache2 stop \
&& sudo apt-get purge apache2* \
&& sudo apt-get autoremove \
&& echo INSTALL NGINX ================================================ \
&& sudo apt-get -y install nginx \
&& echo INSTALL CERTBOT ============================================== \
&& sudo snap install --classic certbot \
&& sudo ln -s /snap/bin/certbot /usr/bin/certbot \
&& echo INSTALL SSL ================================================== \
&& sudo certbot --nginx
```
