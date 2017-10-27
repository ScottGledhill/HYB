Simple setup with b2c accelerator which shows how to create Docker Images of the platform and run them as docker containers
The Images: hsql, solr, loadbalancer and platform (with several aspects inside).
For now only with "static" oad balancer

To perform follow this scenario:
================================

-> ./install.sh -r b2c_acc_dockerized createImagesStructure
--> *Note* To run with solrCloud run ./install.sh -r b2c_acc_dockerized createImagesStructure -A solrMode=cloud 
-> cd work/output_images/b2caccDeployment
-> ./build-images.sh
-> cd - && cd recipes/b2c_acc_dockerized
-> docker-compose up solr platform_hac - recommended for the first platform init on hac
--> *Note* To run with solrCloud: docker-compose up solrc2 platform_hac
-> docker-compose up load_balancer_frontend platformacceleratorstorefront1 platformacceleratorstorefront2 
-> docker-compose up load_balancer_backend platformbackoffice1 platformbackoffice2


Alternative platform init
==========================
-> docker-compose up platform_admin_init


To add background processing nodes:
===================================
-> docker-compose scale platform_background_processing=1


To access the storefronts on running docker containers:
===============================
-> https://localhost:9111   - hac, directly
-> https://localhost/yacceleratorstorefront/?site=apparel-uk   - accstorefronts behind the frontend load balancer
-> https://localhost:444/backoffice/ - backoffice storefronts behind the backend load balancer



