Simple setup with Platform only showing how to use mod_cluster based load balancer

To perform follow this scenario:
================================

-> ./install.sh -r platform_only_mod_cluster createImagesStructure
-> cd work/output_images/dynamicLoadBalancerDemo/
-> ./build-images.sh
-> cd - && cd recipes/platform_only_mod_cluster
-> docker-compose up platform_admin_init
-> docker-compose up -d load_balancer
    # Verify that you can access mod_cluster management console at http://localhost:7777/mcm
-> docker-compose up -d platform_hac
    # Verify new node has been registered (management console)
    # Access hac at https://localhost/

To add more 'hac' nodes:
===================================
-> docker-compose scale platform_hac=4

To add background processing nodes:
===================================
-> docker-compose scale platform_background_processing=1
