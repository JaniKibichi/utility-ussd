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

<script src="https://gist.github.com/JaniKibichi/00da7b235e901ea31d06aef3d7186e95.js"></script>
