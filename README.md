[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/donate/?business=7JXD6EDFHXF5C&no_recurring=1&item_name=To+develop%2C+mantain+and+evolve+a+type+of+software+that+is+not+easy+to+get+from+great+corporations&currency_code=USD)
# Dropins based DSU for Java Servlet 4.0 apps
This project was conceived as a (kind of) reference implementation for [Softalks DSU](https://github.com/softalks/dsu.http) that uses [Apache Felix File Install](https://felix.apache.org/documentation/subprojects/apache-felix-file-install.html) to install/uninstall/update OSGi components providing JEE web contexts. You can choose to use it:
* to build your WAR in order to have a (dropins based) [DSU](https://en.wikipedia.org/wiki/Dynamic_software_updating) enabled web application (like [this one](https://github.com/softalks/dsu.example)) or
* as an example to build a DSU solution best suited for your needs
## Atomic updates
To allow atomically update a set of files you must stop de Apache Felix File Install bundle while the dropins directory is being refreshed (using, for example, `wget -m http://host.domain/bundles`). The included Apache Felix [Gogo](https://felix.apache.org/documentation/subprojects/apache-felix-gogo.html)/[Web Console](https://felix.apache.org/documentation/subprojects/apache-felix-web-console.html) components allows you to start/stop bundles manual or [programmatically](https://felix.apache.org/documentation/subprojects/apache-felix-web-console/web-console-restful-api.html)
