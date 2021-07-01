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
```shell script
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

##### TECHNOLOGY
- API : Akka
- DB : Firestore
- Mobile Money : Safaricom API
- Short Code : Africa's Talking API
- Deployed : Google Cloud Platform


##### CREATE GKE CLUSTER, GKE NODES + AUTOSCALER, HORIZONTAL POD AUTOSCALER

1. gcloud init && gcloud compute machine-types list --filter="zone:(us-central1-a)"
2. gcloud container clusters create ussd-cluster --zone us-central1-a --num-nodes 1 --enable-autoscaling --min-nodes 1 --max-nodes 10  --machine-type=n1-standard-1
3. gcloud container clusters get-credentials ussd-cluster --zone us-central1-a --project smart-surf-285319
4. gcloud projects add-iam-policy-binding smart-surf-285319 --member=serviceAccount:665270238595@cloudbuild.gserviceaccount.com --role=roles/container.developer

##### OPERATIONS
Set Up CloudBuild to: Build Image, Upload Image to GCR, Deploy Image to GKE

1. Run sbt stage to create files to target/universal/stage/bin
2. Generate Dockerfile using sbt docker:stage
3. This file can be found at target/docker/stage/Dockerfile
4. Create the CloudBuild Yaml
5. gcloud builds submit --timeout=900S --project=smart-surf-285319 --config cloudbuild.yaml

kubectl --namespace default get services -o wide -w nginx-ingress-ingress-nginx-controller
