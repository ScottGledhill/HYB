FROM httpd:alpine

ENV DEFAULT_PORT=80 HTTPS_PORT=443 SERVER_NAME=localhost


COPY ./my-httpd.conf     /usr/local/apache2/conf/httpd.conf
COPY ./my-httpd-ssl.conf /usr/local/apache2/conf/extra/httpd-ssl.conf
COPY ./addNodesAndRun.sh /usr/local/bin/addNodesAndRun
RUN chmod +x /usr/local/bin/addNodesAndRun

EXPOSE ${DEFAULT_PORT} ${HTTPS_PORT}

CMD ["addNodesAndRun","/usr/local/apache2/conf/httpd.conf"]
