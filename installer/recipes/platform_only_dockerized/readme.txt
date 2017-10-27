Simple setup with Platform only which shows how to create Docker Images of the Platform.

To perform follow this scenario:
================================

-> ./install.sh -r platform_only_dockerized createImagesStructure
-> cd work/output_images/simpleDeployment
-> ./build-images.sh
-> cd - && cd recipes/platform_only_dockerized
-> docker-compose up platform_admin_init
-> docker-compose up load_balancer platformhac1 platformhac2

To add background processing nodes:
===================================
-> docker-compose scale platform_background_processing=1




